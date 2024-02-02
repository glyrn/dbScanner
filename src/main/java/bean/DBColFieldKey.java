package bean;

/**
 * 扫表
 * entity 列字段类型
 *
 * @author guolinyun
 * @date 2024/2/1
 */
public enum DBColFieldKey {

    //mysql固定关键字1
    TABLE_NAME,//   表名
    ORDINAL_POSITION,// 字段位置

    COLUMN_NAME,//  字段名
    TYPE_NAME,//    字段类型
    COLUMN_SIZE,//
    REMARKS,//  注释
    COLUMN_DEF,//   默认值
    IS_NULLABLE,//  是否可以null
    IS_AUTOINCREMENT,// 是否自增
    HAS_DEF_VAL,//是否有默认值


    //mysql固定关键字2
    FIELD,//  字段名
    COLLATION,
    EXTRA,

    //自定义关键字
    INDEX_TYPE,
    FIELD_TYPE,//    字段类型
    MODIFY_MODE,    //modify column时对比规则

    ;

    public static DBColFieldKey forName(String name) {
        for (DBColFieldKey value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;

    }

    // 暂时无用
    public static DBColFieldKey[] getfix2() {
        return new DBColFieldKey[]{
                FIELD,// 字段名
                COLLATION,
                EXTRA,
        };
    }
}
