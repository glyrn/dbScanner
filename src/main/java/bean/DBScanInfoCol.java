package bean;

import com.mysql.cj.util.StringUtils;
import util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import static bean.DBColFieldKey.COLLATION;
import static bean.DBColFieldKey.COLUMN_DEF;
import static bean.DBColFieldKey.COLUMN_NAME;
import static bean.DBColFieldKey.COLUMN_SIZE;
import static bean.DBColFieldKey.EXTRA;
import static bean.DBColFieldKey.HAS_DEF_VAL;
import static bean.DBColFieldKey.IS_AUTOINCREMENT;
import static bean.DBColFieldKey.IS_NULLABLE;
import static bean.DBColFieldKey.MODIFY_MODE;
import static bean.DBColFieldKey.REMARKS;
import static bean.DBColFieldKey.TYPE_NAME;
import static bean.DBColFieldKey.values;

/**
 * 扫表
 * 列信息
 *
 * @author guolinyun
 * @date 2024/2/1
 */
public class DBScanInfoCol {
    private Map<DBColFieldKey, Object> fieldMap = new HashMap<>();

    private String tableCollation;

    public String getName(){
        return get(COLUMN_NAME);
    }

    public void setName(String columnName) {
        put(COLUMN_NAME,columnName);
    }

    public <T> T get(DBColFieldKey key){
        return (T) fieldMap.get(key);
    }

    public void put(DBColFieldKey key, Object val){
        fieldMap.put(key,val);
    }

    public void putMap(Map<DBColFieldKey, Object> columnsMap) {
        fieldMap.putAll(columnsMap);
    }

    public String getTableCollation() {
        return tableCollation;
    }

    public void setTableCollation(String tableCollation) {
        this.tableCollation = tableCollation;
    }

    public String toPKString() {
        return "PRIMARY KEY (`" + get(COLUMN_NAME) + "`)";
    }

    public FieldModifyMode getModifyMode() {
        FieldModifyMode mode = get(MODIFY_MODE);
        if (mode != null) {
            return mode;
        }
        return FieldModifyMode.STRICT;
    }

    public String toString() {
        return toCreateString(true);
    }

    private String toCreateString(boolean showCollate) {

        String columnName = get(COLUMN_NAME);
        String specialColumn = getSpecialColumn(columnName);
        if (!StringUtil.isEmptyString(specialColumn)) {
            // 特殊固定列
            return specialColumn;
        }

        String typeName = get(TYPE_NAME);
        int columnSize = get(COLUMN_SIZE);

        String collation = StringUtil.isEmptyString(get(COLLATION)) ? tableCollation : get(COLLATION);
        String columnDef = get(COLUMN_DEF) == null ? "NULL" : get(COLUMN_DEF);
        boolean hasDefValue = get(HAS_DEF_VAL);
        String extra = get(EXTRA) == null ? "" : get(EXTRA);
        String remarks = get(REMARKS);
        boolean isNullable = get(IS_NULLABLE);
        boolean isAutoIncrement = get(IS_AUTOINCREMENT);

        StringBuffer str = new StringBuffer("`").append(columnName).append("` ").append(typeName);
        if (columnSize > 0) {
            str.append("(").append(columnSize).append( ")");
        }
        if (!extra.isEmpty()) {
            if (extra.indexOf("unsigned") > -1) {
                str.append(" ").append("zerofill");
                extra = extra.replaceAll("zerofill", "");
            }
            extra = extra.trim();
        }

        // 字符串类型 字符排序类型 非默认
        if (showCollate && (typeName.indexOf("text") >= 0 || typeName.indexOf("char") >= 0)) {

            String tableCharSet = tableCollation.split("_")[0];

            if (!collation.equals(tableCollation)) {
                String charSet = collation.split("_")[0];
                // 与表不一样
                if (!charSet.equals(tableCharSet)) {
                    // 字符集也不一样
                    str.append(" CHARACTER SET ").append(charSet).append(" COLLATE ").append(collation);
                }else {
                    // 排序不一样
                    str.append(" COLLATE ").append(collation);
                }
            }else if (!tableCharSet.equals(FieldType.DEFAULT_CHARSET)) {
                // table字符集非数据库默认字符集 排序不一样
                str.append(" COLLATE ").append(collation);
            }// else 一样的不用特殊说明
        }

        //非空限制
        if (!isNullable) {
            str.append(" NOT NULL");
        }else if (typeName.indexOf("timestamp") > -1) {
            // 时间戳默认为 not null 所以要特殊指定
            str.append(" NULL");
        }

        if (isAutoIncrement) {
            //自增
            if(!extra.contains("AUTO_INCREMENT")){
                str.append(" AUTO_INCREMENT");
            }
        }else if(hasDefValue && !extra.contains(" DEFAULT ")){
            // 默认值
            if (typeName.indexOf("bolb") > -1 || typeName.indexOf("text") > -1) {
                // 无默认值 BLOB/TEXT column can't have a default value
            }else if ("NULL".equalsIgnoreCase(columnDef)) {

                if(typeName.indexOf("int") > -1 || typeName.indexOf("float") > -1 || typeName.indexOf("double") > -1){
                    //数字类型
                    str.append(" DEFAULT '0'");
                }else if (typeName.indexOf("timestamp") > -1) {
                    str.append(" DEFAULT '0000-00-00 00:00:00'");
                }else if (!isNullable) {
                    //不可为null
                }else{
                    str.append(" DEFAULT NULL");
                }

            }else {
                if(columnDef.startsWith("$")){
                    //特殊值 不用加''
                    str.append(" DEFAULT " + columnDef.substring(1));
                }else{
                    str.append(" DEFAULT '" + columnDef + "'");
                }
            }
        }

        if (!extra.isEmpty()) {
            // 特殊定义
            str.append(" " + extra.trim());
        }

        if (!remarks.isEmpty()) {
            if(!extra.contains(" COMMENT ")) {
                str.append(" COMMENT '" + remarks + "'");
            }
        }
        return str.toString();
    }

    /**
     * 判断是否与建表语句相同
     * 用于modify column
     * @param oldColumnStr
     * @param oldTableCollation 原表的字符排序
     * @return
     */
    public boolean checkModifyStr(boolean flagModifyStrict, String oldColumnStr, String oldTableCollation) {

        FieldModifyMode modifyMode = flagModifyStrict ? FieldModifyMode.STRICT : getModifyMode();
        if (modifyMode == FieldModifyMode.SKIP) {
            // 跳过检查
            return true;
        }

        String oldCollation = oldTableCollation.isEmpty() ? FieldType.DEFAULT_CHARSET_COLLATE : oldTableCollation;
        StringBuilder oldSB = new StringBuilder();
        if (oldCollation.trim().endsWith(",")){
            oldColumnStr = oldColumnStr.trim().substring(0, oldColumnStr.trim().length()-1);
        }
        String[] split = oldColumnStr.split(" ");
        for (int i = 0; i < split.length; i ++) {
            if (split[i].trim().equals(""))continue;
            if (split[i].trim().equalsIgnoreCase("CHARACTER")){
                //CHARACTER SET utf8
                i+=2;//skip
                continue;
            }

            if(split[i].trim().equalsIgnoreCase("COLLATE")){
                oldCollation = split[i+1].trim().replaceAll(",","");
                i+=1;//skip
                continue;
            }
            if(split[i].trim().equalsIgnoreCase("DEFAULT")){
                String typeName = get(TYPE_NAME);

                if(typeName.indexOf("int") > -1 || typeName.indexOf("float") > -1 || typeName.indexOf("double") > -1){
                    if(!"NULL".equalsIgnoreCase(split[i + 1].trim()) ){
                        //数字类型
                        double v = Double.parseDouble(split[i + 1].trim().substring(1, split[i + 1].trim().length()-1));
                        String[] splitV = String.valueOf(v).split("\\.");
                        if (splitV.length == 1) {
                            split[i + 1] = "'" + v + "'";
                        } else {
                            long lowV = Long.parseLong(splitV[1]);
                            if(lowV == 0){
                                split[i + 1] = "'" + splitV[0] + "'";
                            }else{
                                split[i + 1] = "'" + v + "'";
                            }

                        }
                    }

                }
            }

            if(oldSB.length() > 0){
                oldSB.append(" ");
            }
            oldSB.append(split[i]);
        }
        oldColumnStr = oldSB.toString();

        String newColumnStr = toCreateString(false);
        if (modifyMode == FieldModifyMode.STRICT) {
            if (!newColumnStr.equalsIgnoreCase(oldColumnStr)) {
                return false;
            }
        }else {
            //ONLY_CORE
            // TODO: 这里可能会出现问题 后面检查一下
            String newCore = StringUtil.join(newColumnStr.split(" ")," ", 0,2);
            String oldCore = StringUtil.join(split," ", 0,2);;

            String typeName = get(TYPE_NAME);
            if (typeName.indexOf("char") >= 0) {
                // 比对长度
                if (!newCore.equals(oldCore)) {
                    return false;
                }
            }else {
                // 不比对长度
                if (newCore.indexOf("(") > -1) {
                    newCore = newCore.substring(0, newCore.indexOf("("));
                }
                if (oldCore.indexOf("(") > -1) {
                    oldCore = oldCore.substring(0, oldCore.indexOf("("));
                }

                if (!newCore.equals(oldCore)) {
                    return false;
                }
            }
        }

        // 判断字符集
        String collation = ((String) get(COLLATION)).isEmpty() ? tableCollation : get(COLLATION);
        if (!collation.equalsIgnoreCase(oldCollation)) {
            return false; // 不一致
        }

        return true;
    }

    /**
     * 特殊列 固定格式
     * @param columnName
     * @return
     */
    static String getSpecialColumn(String columnName){
        switch (columnName){
            case "createTime":{
                return "`createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'";
            }
            case "updateTime":{
                return "`updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
            }
        }
        return null;
    }
}
