import bean.DBScannerEntityMD5;
import bean.TableName;
import com.google.protobuf.Internal;
import conf.DbCfg;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * 使用mybatis框架的时候不会自动创建表 如果数据库中没有表 应该先自动创建表并存储md5 如果已经有表了 就修改表
 */
@Slf4j
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
     * 跳过的table 忽略检查
     */
    private Set<String> skipTable = new HashSet<>();

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


        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
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
                        // TODO 扫描所有带 @Entity 注解的class [静态] [classSet]
                        List<Class<?>> classSet = new ArrayList<>();
                        Iterator<Class<?>> ite = classSet.iterator();

                        while (ite.hasNext()) {
                            Class<?> objClass = ite.next();
                            // 过滤没有 @TableName 注解的类
                            if (!objClass.isAnnotationPresent(TableName.class)) {
                                ite.remove();
                            }
                        }
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


        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    /**
     *
     * @param dbExcutor
     * @param entities
     * @param batchConstraints
     * @param existTableNames
     * @return
     */
    private boolean doScanningDatabase(DBExcutor dbExcutor, Map<String, List<Class<?>>> entities, Map<String, List<Class<?>>> batchConstraints, List<String> existTableNames) {
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
                dbExcutor.update("CREATE TABLE IF NOT EXISTS" + TABLE_MD5 + "(className varchar(120) not null primary key,packageName varchar(512),md5 varchar(100),tableName varchar(100) not null);" , false);
            }

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<Class<?>> scanClassesFilter(String packageName, Class<?>... annotationClass) {
        return null;
    }

}
