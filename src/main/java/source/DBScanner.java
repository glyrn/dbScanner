package source;

import bean.annotation.DBScanColumn;
import bean.annotation.DBScanColumnExt;
import bean.DBColFieldKey;
import bean.DBScanInfoCol;
import bean.DBScannerEntityMD5;
import bean.DBScannerInfoIndex;
import bean.DBScannerInfoTable;
import bean.annotation.DBTableAndFields;
import bean.FieldModifyMode;
import bean.FieldType;
import bean.annotation.GeneratedValue;
import bean.annotation.DBScanId;
import bean.IndexType;
import bean.annotation.KeyConstraint;
import bean.annotation.DBScanTable;
import bean.annotation.DBScanTableExt;
import bean.annotation.UniqueConstraint;
import conf.DbCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ClassUtil;
import util.FileUtil;
import util.Md5Util;
import util.StringUtil;
import util.YamlUtil;

import java.beans.Transient;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static bean.DBColFieldKey.COLLATION;
import static bean.DBColFieldKey.COLUMN_DEF;
import static bean.DBColFieldKey.COLUMN_NAME;
import static bean.DBColFieldKey.COLUMN_SIZE;
import static bean.DBColFieldKey.EXTRA;
import static bean.DBColFieldKey.FIELD_TYPE;
import static bean.DBColFieldKey.HAS_DEF_VAL;
import static bean.DBColFieldKey.INDEX_TYPE;
import static bean.DBColFieldKey.IS_AUTOINCREMENT;
import static bean.DBColFieldKey.IS_NULLABLE;
import static bean.DBColFieldKey.MODIFY_MODE;
import static bean.DBColFieldKey.REMARKS;
import static bean.DBColFieldKey.TYPE_NAME;
import static bean.FieldModifyMode.STRICT;

/**
 * 使用mybatis框架的时候不会自动创建表 如果数据库中没有表 应该先自动创建表并存储md5 如果已经有表了 就修改表
 */

public class DBScanner {

    /**
     * 记录各个表的md5的表
     */
    public static final String TABLE_MD5 = "table_md5";

    /**
     * 全局实例对象
     */
    private static DBScanner instance = null;

    /**
     * className - md5
     */
    private Map<String, String> oldFileMd5Map = new ConcurrentHashMap<>();

    /**
     * tableName - className
     * 作废的表
     */
    private Map<String, String> noUseTableMap = new ConcurrentHashMap<>();

    /**
     * todo 1. 开关 删除无用的表 2. 自动创建或者删除索引 3. modify column时严格检查开关 4. modify column 是否修改已存在的列
     */

    /**
     * 开关 删除无用表
     */
    private boolean flagDropNoUseTable = true;
    /**
     * 开关 删除无用列
     */
    private boolean flagDropNoUseColumn = true;
    /**
     * 开关 自动创建或删除索引
     */
    private boolean flagAlterIndex = true;
    /**
     * 开关 modify column时严格检查开关
     */
    private boolean flagModifyStrict = true;
    /**
     * 开关 modify column 是否修改已存在的列
     */
    private boolean flagModifyCol = true;

    /**
     * 跳过的table 忽略检查
     */
    private Set<String> skipTables = new HashSet<>();

    Logger log = LoggerFactory.getLogger(DBScanner.class);

    /**
     * 获取实例对象
     */
    public static DBScanner getInstance() {
        if (instance == null) {
            instance = new DBScanner();
        }

        return instance;
    }

    /**
     * 构造
     */
    private DBScanner() {
    }

    /**
     * 对外接口 [ jar包依赖 ]
     *
     * 使用次接口默认从工作路径下的 src/main/resource下读取配置 (DbCfg.yaml)
     * @return
     */
    public boolean startWork(){
        DbCfg dbCfg = YamlUtil.loadYaml();
        return startWork(dbCfg);
    }

    /**
     * 开始扫表
     * @return
     */
    public boolean startWork(DbCfg dbCfg) {



        try(DBExcutor dbExcutor = new DBExcutor()){
            // 建立数据库连接失败
            if (!dbExcutor.init(dbCfg)) {
                return false;
            }
            // 扫表

            /**
             * 1. 先扫描数据库中现存的表的信息
             * 2. 扫描所有需要处理的entity类
             * 3.
             */
//            List<String> tableNames = dbExcutor.getTables();

            //设置开关 默认开
            flagAlterIndex = Boolean.parseBoolean(System.getProperty("dbScannerAlterIndex",String.valueOf(flagAlterIndex)));
            flagModifyStrict = Boolean.parseBoolean(System.getProperty("dbScannerModifyStrict",String.valueOf(flagModifyStrict)));


            long startTime = System.currentTimeMillis();

            boolean result = scanningDatabase(dbExcutor, dbCfg.getEntityPackage());

            //记录sql文件
            recordUpdateSql(startTime,dbExcutor.getUpdateSqRecords());


            return result;

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 扫描数据库
     * @param dbExcutor
     * @param tablePackages
     * @return
     */
    private boolean scanningDatabase(DBExcutor dbExcutor, String tablePackages) {
        try{
            List<String> tableNames = dbExcutor.getAllTablesName();

            Set<String> packageNames = new LinkedHashSet<>();

            // 创建md5表
            createTableMd5(dbExcutor, tableNames);

            // 遍历所有需要自动扫库的entity
            Map<String, List<Class<?>>> entities = new HashMap<>();
            Map<String, List<Class<?>>> batchConstrains = new HashMap<>();

            String[] entityPackageArray = tablePackages.split(",");

            if (entityPackageArray != null) {
                for (String entityPackage : entityPackageArray) {
                    entityPackage = entityPackage.trim();
                    {// entity

                        List<Class<?>> classSet = ClassUtil.scanClassesFilter(entityPackage, DBScanTable.class);
//                        Iterator<Class<?>> ite = classSet.iterator();

//                        while (ite.hasNext()) {
//                            Class<?> objClass = ite.next();
//                            // 过滤没有 @TableName 注解的类
//                            if (!objClass.isAnnotationPresent(DBScanTable.class)) {
//                                ite.remove();
//                            }
//                        }

                        if (classSet.size() > 0) {
                            packageNames.add(entityPackage);
                            entities.put(entityPackage, classSet);
                        }
                    }
                    {// batchConstrants
                        //TODO: 向前兼容层
                    }
                }

            }

            // 旧的md5
            if (oldFileMd5Map.size() <= 0) {

                List<Map<String, Object>> list = dbExcutor.qurey("select packageName,className,md5,tableName from " + TABLE_MD5);
                if (list != null && list.size() > 0) {
                    for (Map<String, Object> objectMap : list) {
                        DBScannerEntityMD5 entityMD5 = new DBScannerEntityMD5();

                        entityMD5.setPackageName(String.valueOf(objectMap.get("packageName")));
                        entityMD5.setClassName(String.valueOf(objectMap.get("className")));
                        entityMD5.setMd5(String.valueOf(objectMap.get("md5")));
                        entityMD5.setTableName(String.valueOf(objectMap.get("tableName")));

                        if (!packageNames.contains(entityMD5.getPackageName())) {
                            // 数据库中容易的包不再处理
                            tableNames.remove(entityMD5.getTableName());
                            continue;
                        }

                        oldFileMd5Map.put(entityMD5.getClassName(), entityMD5.getMd5());
                        noUseTableMap.put(entityMD5.getTableName(), entityMD5.getClassName());
                    }
                }

            }
            // 开始扫描
            if (!doScanningDatabase(dbExcutor, entities, batchConstrains, tableNames)) {
                return false;
            }

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    /**
     *
     * @param dbExcutor
     * @param entities 实体类
     * @param batchConstraints 批量约束
     * @param existTableNames 库中已经存在的表
     * @return
     */
    private boolean doScanningDatabase(DBExcutor dbExcutor, Map<String, List<Class<?>>> entities, Map<String, List<Class<?>>> batchConstraints, List<String> existTableNames) {

        log.debug("doScanningDatabase entities = {},existTableNames={}", entities, existTableNames);

        Map<String, List<DBTableAndFields>> tableBatchMap = new HashMap<>();

        // 处理批量约束
        if (batchConstraints != null) {
            // TODO: 兼容老代码
        }

        // 处理新代码
        for (Map.Entry<String, List<Class<?>>> entry : entities.entrySet()) {
            String packageName = entry.getKey();
            for (Class<?> objectClass : entry.getValue()) {

                // 遍历所有table
                try {
                    // 过滤没有 @Table 注解的类
                    if (!objectClass.isAnnotationPresent(DBScanTable.class)) {
                        continue;
                    }

                    String className = objectClass.getSimpleName() + ".class";
                    String oldMd5 = oldFileMd5Map.get(className); // 当前class 上一次检测的md5

                    // table
                    DBScanTable genrateDBScanTable = objectClass.getAnnotation(DBScanTable.class);
                    String tableName = genrateDBScanTable.name();
                    UniqueConstraint[] uniqueConstraints = genrateDBScanTable.uniqueConstraints();

                    // TODO 批量约束 兼容老代码
                    List<DBTableAndFields> oneBatchConstraints = tableBatchMap.get(tableName);
                    DBScanTableExt generateDBScanTableExt = objectClass.getAnnotation(DBScanTableExt.class);
                    if (generateDBScanTableExt == null && oneBatchConstraints != null && oneBatchConstraints.size() > 0) {
                        // 如果entity没有约束 使用批量约束
                        for (DBTableAndFields oneBatchConstraint : oneBatchConstraints) {
                            if (oneBatchConstraint == null || oneBatchConstraint.justColumns()) {
                                continue;
                            }
                            generateDBScanTableExt = oneBatchConstraint.table();
                            break;
                        }
                    }
                    String tableCollation = generateDBScanTableExt != null ? generateDBScanTableExt.collate() : null;

                    // 生产entity对于的table
                    DBScannerInfoTable tableFromEntity = new DBScannerInfoTable();
                    tableFromEntity.setTableName(tableName);
                    tableFromEntity.setCollation(tableCollation);
                    tableCollation = tableFromEntity.getCollation();

                    List<TreeMap<DBColFieldKey, Object>> fieldsFromEntity = getFieldList(objectClass, oneBatchConstraints);

                    // index
                    Map<String, DBScannerInfoIndex> indexMapFromEntity = new HashMap<>();
                    // 需要添加的索引
                    List<DBScannerInfoIndex> index2AddFromEntity = tableFromEntity.getIndexList();

                    // columns
                    Map<String, DBScanInfoCol> columnMapFromEntity = new HashMap<>();
                    for (TreeMap<DBColFieldKey, Object> fieldMap : fieldsFromEntity) {
                        if (fieldMap == null) {
                            // 临时数据
                            continue;
                        }
                        DBScanInfoCol column = new DBScanInfoCol();



                        // column annotation
                        column.putMap(fieldMap);
                        column.setTableCollation(tableCollation); // 记录table的默认字符排序

                        String columnName = column.get(COLUMN_NAME);

//                        String test = column.toString();

                        // 记录这个列
                        tableFromEntity.getColumns().add(column);
                        columnMapFromEntity.put(columnName, column);


                        // 索引
                        IndexType indexType = column.get(INDEX_TYPE);
                        if(indexType != null){
                            switch (indexType){
                                case PK:{
                                    tableFromEntity.setPk(column);
                                    column.put(IS_NULLABLE,false);
                                }break;
                                case UNIQUE:{
                                    //唯一索引
                                    DBScannerInfoIndex index = DBScannerInfoIndex.valueOfUnique(columnName);
                                    index2AddFromEntity.add(index);
                                    indexMapFromEntity.put(index.getIndexName(), index);
                                }break;
                                case KEY:{
                                    //普通索引
                                    DBScannerInfoIndex index = DBScannerInfoIndex.valueOfKey(columnName);
                                    index2AddFromEntity.add(index);
                                    indexMapFromEntity.put(index.getIndexName(), index);
                                }break;
                            }
                        }//end if 索引

                    }

                    // 联合索引
                    if (uniqueConstraints != null) {
                        for (UniqueConstraint uc : uniqueConstraints) {
                            if(uc == null){
                                continue;
                            }
                            DBScannerInfoIndex index = DBScannerInfoIndex.valueOf(uc.name(),uc.columnNames());
                            index.setUnique(true);//唯一
                            index2AddFromEntity.add(index);
                            indexMapFromEntity.put(index.getIndexName(), index);
                        }
                    }
                    //联合索引
                    if(generateDBScanTableExt != null && generateDBScanTableExt.keyConstraints()!=null){
                        for (KeyConstraint uc : generateDBScanTableExt.keyConstraints()) {
                            if(uc == null){
                                continue;
                            }
                            DBScannerInfoIndex index = DBScannerInfoIndex.valueOf(uc.name(),uc.columnNames());
                            index.setUnique(uc.unique());
                            index2AddFromEntity.add(index);
                            indexMapFromEntity.put(index.getIndexName(), index);
                        }
                    }

                    String newMd5 = Md5Util.makeMD5(tableFromEntity.createTable());

                    // 这个表还在使用
                    noUseTableMap.remove(tableName);




                    if (!StringUtil.isEmptyString(oldMd5) && newMd5.equals(oldMd5) && existTableNames.contains(tableFromEntity.getTableName())) {
                        // md5 未变化 不需要修改
                        continue;
                    }

                    // db 中的table结构
                    Map<String, String> key2CreateStrMap = new HashMap<>();// key-createStr

                    if (existTableNames.contains(tableName)) {
                        // 表存在 从db中获取

                        log.debug("scanning table: {}", tableName);
                        // 基础信息
                        queryTableBaseInfo(dbExcutor, tableName, key2CreateStrMap);

                        for (Map.Entry<String, String> stringEntry : key2CreateStrMap.entrySet()) {
                            if(stringEntry.getValue().endsWith(",")){
                                stringEntry.setValue(stringEntry.getValue().substring(0,stringEntry.getValue().length()-1));
                            }
                        }
                    }

                    //准备记录新的md5
                    String insertMd5Sql = "INSERT INTO " + TABLE_MD5 + "(className,packageName, md5, tableName) VALUES('" + className + "', '"
                            + packageName + "', '"
                            + newMd5 + "','" + tableName + "') ON DUPLICATE KEY UPDATE md5='" + newMd5 + "'";

                    // 表不存在,建新表
                    if (key2CreateStrMap.size() <= 0) {
                        //创建table
                        dbExcutor.update(tableFromEntity.dropTable(),true);
                        dbExcutor.update(tableFromEntity.createTable(),true);
                        //扫表完成 记录table md5
                        updateMd5Table(dbExcutor, insertMd5Sql);
                        continue;
                    }

                    if(skipTables.contains(tableName)){
                        //table 跳过检查
                        continue;
                    }

                    //alter collate
                    {
                        String collationNew = tableFromEntity.getCollation();
                        String collationOld = key2CreateStrMap.get("COLLATION");



                        if(StringUtil.isEmptyString(collationOld)){
                            collationOld = FieldType.DEFAULT_CHARSET_COLLATE;
                        }
                        if(!collationNew.equals(collationOld)){
                            //字符排序改变
                            dbExcutor.update(tableFromEntity.alterCollate(),true);
                        }
                    }

                    // add column
                    Iterator<DBScanInfoCol> addColumnIte = tableFromEntity.getColumns().iterator();
                    while (addColumnIte.hasNext()) {
                        DBScanInfoCol addColumn = addColumnIte.next();
                        String columnName = addColumn.getName();
                        // add column
                        if (getKeyIgnoreCase(key2CreateStrMap.keySet(),columnName) == null) {
                            dbExcutor.update(tableFromEntity.addColumn(addColumn),true);
                            continue;
                        }
                    }

                    //记录每个列身上有哪些索引
                    Map<String,Set<String>> oldFieldIndexMap = new HashMap<>();
                    //原先表中已存在且在扫表逻辑中没有删除的index名称
                    Set<String> oldAliveIndex = new HashSet<>();

                    // drop index 删除索引在删除列之前
                    Iterator<Map.Entry<String, String>> dropIndexIte = key2CreateStrMap.entrySet().iterator();
                    while (dropIndexIte.hasNext()) {
                        Map.Entry<String, String> ent = dropIndexIte.next();
                        String key = ent.getKey();
                        String line = ent.getValue();
                        if (!key.startsWith("KEY") && !key.startsWith("UNIQUE KEY")) {
                            //非索引
                            continue;
                        }
                        String indexName = key.split("`")[1];

                        //记录原先存在的索引 以及相关对应字段
                        oldAliveIndex.add(indexName);
                        Set<String> oldFields = DBScannerInfoIndex.parseFields(line);
                        if(oldFields != null){
                            oldFields.forEach(field -> {
                                oldFieldIndexMap.computeIfAbsent(field,s -> new HashSet<>()).add(indexName);
                            });
                        }

                        if (flagAlterIndex) {
                            DBScannerInfoIndex infoIndex = indexMapFromEntity.get(indexName);
                            if (infoIndex == null || !infoIndex.equalsWithOld(line)) {
                                //索引不存在了 或者 与原索引不一致
                                dbExcutor.update(tableFromEntity.dropIndex(indexName), true);
                                //标记该索引已经删除
                                oldAliveIndex.remove(indexName);
                                continue;
                            }
                        }
                    }

                    // drop column
                    if(flagDropNoUseColumn) {
                        Iterator<Map.Entry<String, String>> dropColumnIte = key2CreateStrMap.entrySet().iterator();
                        while (dropColumnIte.hasNext()) {
                            Map.Entry<String, String> ent = dropColumnIte.next();
                            String key = ent.getKey();
                            if (getKeyIgnoreCase(columnMapFromEntity.keySet(),key) == null
                                    && !key.startsWith("PRIMARY")
                                    && !key.startsWith("KEY") && !key.startsWith("UNIQUE KEY")
                                    && !key.startsWith("CHARSET") && !key.startsWith("COLLATION")
                                    && !key.startsWith("CREATE")) {

                                if(!flagAlterIndex){
                                    //如果没有删除索引 且当前列处于该索引，则需要强制删除索引
                                    Set<String> indexSet = oldFieldIndexMap.get(key);
                                    if (indexSet != null) {
                                        indexSet.forEach(indexName -> {
                                            try {
                                                if(!oldAliveIndex.contains(indexName)){
                                                    //该索引已经删除
                                                    return;
                                                }
                                                //标记该索引已经删除
                                                oldAliveIndex.remove(indexName);
                                                //删除列之前 先删除索引
                                                dbExcutor.update(tableFromEntity.dropIndex(indexName), true);
                                            } catch (Exception sqlException) {
                                                // todo 这里是sql 异常 记录sql
                                                log.error("err sql: {}", sqlException);
                                            }
                                        });
                                    }
                                }
                                //删除列
                                dbExcutor.update(tableFromEntity.dropColumn(key),true);
                                continue;
                            }
                        }
                    }

                    // add index 添加索引在删除索引和添加列之后
                    if(flagAlterIndex) {
                        Iterator<DBScannerInfoIndex> addIndexIte = index2AddFromEntity.iterator();
                        while (addIndexIte.hasNext()) {
                            DBScannerInfoIndex index = addIndexIte.next();
                            // 判断索引不存在
                            if(!oldAliveIndex.contains(index.getIndexName())){
//                                if (!key2CreateStrMap.containsKey(index.toKeyString())) {//这种方法 索引名称不变的情况无法判断
                                dbExcutor.update(tableFromEntity.addIndex(index), true);
                                continue;
                            }
                        }
                    }


                    Iterator<DBScanInfoCol> nowColumnIte = tableFromEntity.getColumns().iterator();
                    while (flagModifyCol && nowColumnIte.hasNext()) {
                        try {
                            DBScanInfoCol column = nowColumnIte.next();
                            if(column == null){
                                continue;
                            }
                            String columnName = column.getName();
                            String keyIgnoreCase = getKeyIgnoreCase(key2CreateStrMap.keySet(), columnName);
                            if (keyIgnoreCase != null) {
                                if (!column.checkModifyStr(flagModifyStrict,key2CreateStrMap.get(keyIgnoreCase),key2CreateStrMap.get("COLLATION"))) {
                                    dbExcutor.update(tableFromEntity.alterColumn(column),true);
                                    continue;
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }// end while

                    //扫表完成 记录table md5
                    updateMd5Table(dbExcutor, insertMd5Sql);

                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }// end for loop


        // 删除作废的表
        if (flagDropNoUseTable) {
            Iterator<Map.Entry<String, String>> itl = noUseTableMap.entrySet().iterator();
            while (itl.hasNext()) {
                Map.Entry<String, String> ent = itl.next();
                String tableName = ent.getKey();
                if (TABLE_MD5.equals(tableName)) {
                    continue;
                }


                String sql = "DROP TABLE IF EXISTS `" + tableName + "`;";
                try {
                    dbExcutor.update(sql,true);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
                String delMd5 = "DELETE FROM `" + TABLE_MD5 + "` WHERE tableName = \"" + tableName + "\";";
                try {
                    dbExcutor.update(delMd5,false);
                } catch (Exception e) {
                    throw new RuntimeException();
                }

            }// end while
        }//end if

        return true;
    }

    /**
     * 扫描所有需要处理的entity类
     * @param tablePackages
     * @return
     */
    private boolean scanningEntity(String tablePackages) {
        try{

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }



    /**
     * 创建classMd5表
     */
    private void createTableMd5(DBExcutor dbExcutor, List<String> tableNames) {
        try{
            Set<String> tableNameSet = new HashSet<>(tableNames);

            // 如果是第一次使用 创建md5专用表 用于记录各表class的md5值

            if (!tableNameSet.contains(TABLE_MD5)) {

                String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_MD5 + "(className varchar(120) not null primary key,packageName varchar(512),md5 varchar(100),tableName varchar(100) not null);";
                log.debug(sql);
                dbExcutor.update(sql , false);
            }

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<Class<?>> scanClassesFilter(String packageName, Class<?>... annotationClass) {
        return null;
    }

    /**
     * 读取entity中所包含的列信息
     * @param clazz
     * @param dbTableAndFieldsArray
     * @return
     * @throws NoSuchFieldException
     */
    public static List<TreeMap<DBColFieldKey, Object>> getFieldList(Class<?> clazz, List<DBTableAndFields> dbTableAndFieldsArray) throws NoSuchFieldException {

        Map<String,List<DBScanColumnExt>> columnBatchMap = new HashMap<>();
        if(dbTableAndFieldsArray!=null){
            for (DBTableAndFields tableAndFields : dbTableAndFieldsArray) {
                if(tableAndFields != null && tableAndFields.columns()!=null){
                    for (DBScanColumnExt column : tableAndFields.columns()) {
                        if(StringUtil.isEmptyString(column.name())){
                            continue;
                        }
                        if(getInstance().flagModifyStrict && column.modifyMode() != STRICT){
                            //严格执行时 非严格字段忽略
                            continue;
                        }
                        columnBatchMap.computeIfAbsent(column.name(),k->new ArrayList<>()).add(column);
                    }
                }
            }
        }


        List<TreeMap<DBColFieldKey, Object>> fields = new ArrayList<>();

        // 获得所有枚举字段成员（id, account, name, profession...），
        // 遍历每个枚举成员，获取属性，放入Map中
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {

            if(Modifier.isStatic(field.getModifiers())){
                continue;
            }


            Transient transientInfo = field.getAnnotation(Transient.class);
            if (transientInfo != null || Modifier.isTransient(field.getModifiers())) {
                //临时字段
                continue;
            }


            //定义表结构
            DBScanColumnExt column = field.getAnnotation(DBScanColumnExt.class);
            String columnName = column == null ? field.getName() : column.name();
            if(column != null){
                //加载最前面 优先
                columnBatchMap.computeIfAbsent(columnName,k->new ArrayList<>()).add(0,column);
            }

            DBScanId DBScanIdInfo = field.getAnnotation(DBScanId.class);
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            List<DBScanColumnExt> DBScanColumnExtList = columnBatchMap.get(columnName);

            TreeMap<DBColFieldKey, Object> fieldMap = new TreeMap<>();
            //列类型
            //首个非null
            FieldType type = selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct == null || columnStruct.type() == FieldType.NULL ? FieldType.valueOfType(field.getType()) : columnStruct.type()
            );
            fieldMap.put(FIELD_TYPE, type);
            fieldMap.put(TYPE_NAME, type.getDbType().toLowerCase(Locale.ROOT));
            fieldMap.put(COLUMN_NAME, columnName);

            //以下均为 首个非null
            fieldMap.put(COLUMN_SIZE, selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct != null && columnStruct.size() > 0 ? columnStruct.size() : type.getLen())
            );
            fieldMap.put(COLUMN_DEF, selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct == null ? null : columnStruct.defaults())
            );
            fieldMap.put(HAS_DEF_VAL, selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct == null ? true : columnStruct.hasDefaults())
            );

            fieldMap.put(REMARKS, selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct == null ? "" : columnStruct.comment())
            );
            fieldMap.put(COLLATION, selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct == null ? null : columnStruct.collate())
            );
            fieldMap.put(EXTRA, selectColumnValueFirst(DBScanColumnExtList,
                    columnStruct -> columnStruct == null ? null : columnStruct.extra())
            );
            //首个非默认值
            fieldMap.put(MODIFY_MODE, selectColumnValue(DBScanColumnExtList,
                    columnStruct -> columnStruct != null && columnStruct.modifyMode() != FieldModifyMode.STRICT,
                    columnStruct -> columnStruct == null ? null : columnStruct.modifyMode())
            );

            fieldMap.put(IS_NULLABLE, column == null ? true : column.nullable()); //是否可以null
            //是否自增
            fieldMap.put(IS_AUTOINCREMENT, (generatedValue != null && "AUTO_INCREMENT".equalsIgnoreCase(generatedValue.generator()))
                    || (fieldMap.get(EXTRA) != null && (fieldMap.get(EXTRA).toString().contains("AUTO_INCREMENT")))
            );

            //索引
            IndexType indexType = IndexType.NONE;
            if(DBScanIdInfo != null){
                indexType = IndexType.PK;
            }else if(column != null && column.unique()){
                indexType = IndexType.UNIQUE;
            }else if(selectColumnValueFirst(DBScanColumnExtList, columnStruct -> columnStruct == null ? false : columnStruct.index())){
                //取第一个不为空的
                indexType = IndexType.KEY;
            }
            fieldMap.put(INDEX_TYPE, indexType);

            fields.add(fieldMap);
        }

        return fields;
    }

    /**
     * @param columnList
     * @param predicate  筛选规则
     * @param function   获取值
     * @param <T>
     * @return
     */
    private static <T> T selectColumnValue(List<DBScanColumnExt> columnList, Predicate<DBScanColumnExt> predicate, Function<DBScanColumnExt, T> function) {
        if (columnList != null) {
            for (DBScanColumnExt DBScanColumnExt : columnList) {
                if (predicate.test(DBScanColumnExt)) {
                    return function.apply(DBScanColumnExt);
                }
            }
        }
        return function.apply(null);
    }

    private static <T> T selectColumnValueFirst(List<DBScanColumnExt> columnList, Function<DBScanColumnExt, T> function) {
        return selectColumnValue(columnList, c -> c != null, function);
    }

    /**
     * 获得数据表的结构
     *
     * @param dbSource
     */
    private void queryTableBaseInfo(DBExcutor dbSource, String tableName,Map<String, String> key2CreateStrMap) {

        List<Map<String, Object>> result = dbSource.query("show create table " + tableName);
        try {
            for (Map<String, Object> rowMap : result) {
                String createTable = String.valueOf(rowMap.get("Create Table"));

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(createTable.getBytes(Charset.forName("utf8"))),
                        Charset.forName("utf8")));
                String line;

                while (br.ready() && !(line = br.readLine().trim()).isEmpty()) {
                    //create
                    if (line.startsWith("CREATE")) {
                        key2CreateStrMap.put("CREATE", line);
                        continue;
                    }
                    //column
                    if (line.startsWith("`")) {
                        String[] arr = line.split("`");
                        String columnName = arr[1];
                        key2CreateStrMap.put(columnName, line);
                        continue;
                    }
                    //pk
                    if (line.startsWith("PRIMARY")) {
                        key2CreateStrMap.put("PRIMARY", line);
                        DBScanInfoCol pk = new DBScanInfoCol();
                        String[] arr = line.split("`");
                        pk.setName(arr[1]);
                        continue;
                    }

                    //index
                    boolean isKey = line.startsWith("KEY");
                    boolean isUnique = line.startsWith("UNIQUE KEY");
                    if (isKey || isUnique) {
                        String[] arr = line.split("`");
                        String indexName = arr[1];
                        String key = (isUnique ? "UNIQUE KEY `":"KEY `") + indexName + "`";
                        key2CreateStrMap.put(key, line);

                        List<String> fields = new ArrayList<>();
                        boolean flag = false;
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].trim().equals("(")) {
                                flag = true;
                                continue;
                            }
                            if (arr[i].trim().equals(")")) {
                                flag = false;
                                continue;
                            }

                            if (flag) {
                                fields.add(line.split("`")[1]);
                            }
                        }

                        continue;
                    }//end if key

                    if(line.indexOf("ENGINE=InnoDB") > 0){
                        String[] split = line.split(" ");
                        for (int i = 0; i < split.length; i++) {
                            if(split[i].startsWith("CHARSET=")){
                                String collation = split[i].split("=")[1];
                                key2CreateStrMap.put("CHARSET",collation);
                                continue;
                            }
                            if(split[i].startsWith("COLLATE=")){
                                String collation = split[i].split("=")[1];
                                key2CreateStrMap.put("COLLATION",collation);
                                continue;
                            }
                        }
                    }//end if engine


                }//end while line

                if (br != null) {
                    br.close();
                }
            }//end for table

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void updateMd5Table(DBExcutor dbSource, String sql) {
        try {
            dbSource.update(sql,false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否包含 忽略大小写
     * @param keySet
     * @return keySet中对应的key
     */
    public String getKeyIgnoreCase(Set<String> keySet, String name) {
        for (String key : keySet) {
            //忽略大小写
            if(name.equalsIgnoreCase(key)){
                //包含
                return key;
            }
        }
        return null;
    }

    /**
     * 记录修改语句
     * @param sqlList
     */
    private void recordUpdateSql(long startTime,List<String> sqlList){
        if (sqlList == null || sqlList.size() == 0) {
            return;
        }
        try {

            String sqlFilePath = System.getProperty("user.dir") + "/sql/auto-gen-local.sql";
            String textFile = "";

            StringBuilder sb = new StringBuilder();
            //新的在上面
            sb.append("---------------------------------- ")
                    .append("AUTO GENERATION @ ")
                    .append(new Date().toString())
                    .append(" Cost:")
                    .append(String.valueOf(System.currentTimeMillis()-startTime))
                    .append("ms ----------------------------------\n");

            Map<String,List<String>> tableMap = new LinkedHashMap<>();
            L1:
            for (String sql : sqlList) {
                log.debug("excuting sql:{}", sql);
                if(sql.startsWith("CREATE DATABASE")){
                    //重新创建库
                    textFile = "";//旧的不要了

                    sb.append("--\n");
                    sb.append("-- ").append("INIT DATABASE\n");
                    sb.append("--\n");
                    sb.append(sql).append("\n");
                    continue ;
                }



                String[] strings = sql.split(" ");
                for (int i = 0; i < strings.length; i++) {
                    if(strings[i].trim().equals("")){
                        continue;
                    }

                    if(strings[i].equalsIgnoreCase("table")){
                        for (int j = i+1; j < strings.length; j++) {
                            if(strings[j].startsWith("`")){
                                String tableName = strings[j].substring(1,strings[j].indexOf("`",1));
                                tableMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add(sql);
                                continue L1;
                            }
                        }
                        tableMap.computeIfAbsent("unknown", k -> new ArrayList<>()).add(sql);
                        continue L1;
                    }

                }
            }

            for (Map.Entry<String, List<String>> entry : tableMap.entrySet()) {
                sb.append("--\n");
                sb.append("-- ").append("CHANGES FOR TABLE `").append(entry.getKey()).append("`\n");
                sb.append("--\n");
                for (String sql : entry.getValue()) {
                    sb.append(sql).append("\n");
                }
            }



            //旧的
            sb.append(textFile);

            FileUtil.saveAsFile(sb.toString(),sqlFilePath,"utf8");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
