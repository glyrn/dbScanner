package bean;

import java.util.Date;

/**
 * 字段枚举类型
 *
 * @author gly
 */
public enum FieldType {

    /**
     * 数值类型
     * 整数 [ TINYINT ] [ SMALLINT ] [ MEDIUMINT ] [ INT ] [ BIGINT ]
     * 浮点数 [ FLOAT ] [ DOUBLE ] [ DECIMAL ]
     *
     * 字符串类型
     * 定长字符串 [ CHAR ]
     * 变长字符串 [ VARCHAR ] [ TEXT ]
     *
     * 日期和时间类型
     * 日期类型 [ DATE ]
     * 时间类型 [ TIME ]
     * 日期时间类型 [ DATETIME ] [ TIMESTAMP ]
     *
     * 其他类型
     * 二进制类型 [ BINARY ] [ VARBINARY ] [ BLOB ]
     * boll类型 [ BOOLEAN ]
     *
     */

    // 数值类型 - 整数
    BYTE (new Class[]{byte.class, Byte.class}, "TINYINT", 0),
    SHORT(new Class[]{short.class, Short.class}, "SMALLINT", 0),
    INT(new Class[]{int.class, Integer.class}, "INT", 0),
    LONG(new Class[]{long.class, Long.class}, "BIGINT", 0),
    // 数值类型 - 浮点数
    FLOAT(new Class[]{float.class, Float.class}, "FLOAT", 0),
    DOUBLE(new Class[]{double.class, Double.class}, "DOUBLE", 0),
    // 字符串类型
    CHAR(new Class[]{String.class}, "CHAR", 0),
    VARCHAR(new Class[]{String.class}, "VARCHAR", 0),
    TEXT(new Class[]{String.class}, "TEXT", 0),
    // 日期时间类型
    TIMESTAMP(new Class[]{Date.class}, "TIMESTAMP", 0),
    // bool类型
    BOOLEAN(new Class[]{boolean.class, Boolean.class}, "TINYINT", 1),
    // 未指定
    NULL(new Class[]{}, "NULL", 0)
    ;






    /**
     * 变量类型 -> 对应代码中类型
     */
    private Class<?>[] fieldTypes;
    /**
     * 字段类型 -> 对应mysql中基本类型
     */
    private String dbType;
    /**
     * 字段长度
     */
    private int length;
    /**
     * 字符集
     */
    private String charSet;

    /**
     * 默认字符集
     */
    public static final String DEFAULT_CHARSET_COLLATE = "utf8mb4_general_ci";

    FieldType(Class<?>[] fieldTypes, String dbType, int length) {
        this.fieldTypes = fieldTypes;
        this.dbType = dbType;
        this.length = length;
        this.charSet = DEFAULT_CHARSET_COLLATE;
    }

    /**
     * 按照class类型查找type
     * @param clz
     * @return
     */
    public static FieldType valueOfType(Class<?> clz) {
        for (FieldType value : FieldType.values()) {
            for (Class<?> fieldType : value.fieldTypes) {
                if (fieldType == clz) {
                    return value;
                }
            }

        }
        return null;
    }

}
