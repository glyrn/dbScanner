package bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

//    // 数值类型 - 整数
//    BYTE (new Class[]{byte.class, Byte.class}, "TINYINT", 0),
//    SHORT(new Class[]{short.class, Short.class}, "SMALLINT", 0),
//    INT(new Class[]{int.class, Integer.class}, "INT", 0),
//    LONG(new Class[]{long.class, Long.class}, "BIGINT", 0),
//    // 数值类型 - 浮点数
//    FLOAT(new Class[]{float.class, Float.class}, "FLOAT", 0),
//    DOUBLE(new Class[]{double.class, Double.class}, "DOUBLE", 0),
//    // 字符串类型
//    CHAR(new Class[]{String.class}, "CHAR", 0),
//    VARCHAR(new Class[]{String.class}, "VARCHAR", 0),
//    TEXT(new Class[]{String.class}, "TEXT", 0),
//    // 日期时间类型
//    TIMESTAMP(new Class[]{Date.class}, "TIMESTAMP", 0),
//    // bool类型
//    BOOLEAN(new Class[]{boolean.class, Boolean.class}, "TINYINT", 1),
//    // 未指定
//    NULL(new Class[]{}, "NULL", 0)
//    ;


//数字类型
    BYTE        (new Class[]{byte.class,Byte.class}         ,"TINYINT"      ,4),
    SHORT       (new Class[]{short.class,Short.class}       ,"SMALLINT"     ,6),
    INT         (new Class[]{int.class,Integer.class}       ,"INT"          ,11),
    LONG        (new Class[]{long.class,Long.class}         ,"BIGINT"       ,20),
    FLOAT       (new Class[]{float.class,Float.class}       ,"FLOAT"        ,0),
    DOUBLE      (new Class[]{double.class,Double.class}     ,"DOUBLE"       ,0),
    BOOLEAN     (new Class[]{boolean.class,Boolean.class}   ,"TINYINT"      ,1),
    //日期
    TIMESTAMP   (new Class[]{Date.class}                    ,"TIMESTAMP"    ,0),
    //二进制
    BLOB        (new Class[]{byte[].class,Byte[].class}     ,"BLOB"         ,0),
    MEDIUM_BLOB (new Class[]{byte[].class,Byte[].class}     ,"MEDIUMBLOB"   ,0),
    //字符串类型
    VARCHAR     (new Class[]{String.class}                  ,"VARCHAR"      ,255),
    TEXT        (new Class[]{String.class}                  ,"TEXT"         ,0),
    MEDIUMTEXT  (new Class[]{String.class}                  ,"MEDIUMTEXT"   ,0),
    LONGTEXT    (new Class[]{String.class}                  ,"LONGTEXT"     ,0),
    //null 无用 表示未指定
    NULL        (new Class[]{}                              ,"NULL"         ,0),
    ;



    /**
     * 默认字符集
     */
    public static final String DEFAULT_CHARSET_COLLATE = "utf8mb4_general_ci";
    public static final String DEFAULT_CHARSET = DEFAULT_CHARSET_COLLATE.split("_")[0];
    /**
     * 特殊定义的字段默认长度
     */
    public static Map<FieldType,Integer> extraDefLen = new HashMap<>();

    //基本类型
    private final Class<?>[] fieldTypes;
    private final String dbType;
    private final int len;
    private final String charset;

    FieldType(Class<?>[] fieldTypes, String dbType, int len) {
        this.fieldTypes = fieldTypes;
        this.dbType = dbType;
        this.len = len;
        this.charset = FieldType.DEFAULT_CHARSET_COLLATE;
    }

    /**
     * 针对没有指定type的字段 根据class类型查找默认type
     * @param clz
     * @return
     */
    public static FieldType valueOfType(Class<?> clz) {
        for (FieldType value : FieldType.values()) {
            for (Class<?> fieldType : value.fieldTypes) {
                if(fieldType == clz)return value;
            }
        }
        return null;
    }

    public String getDbType() {
        return dbType;
    }

    public int getLen() {
        Integer extraLen = extraDefLen.get(this);
        if(extraLen != null){
            //使用特殊指定的长度
            return extraLen;
        }
        return len;
    }

    public Class<?>[] getFieldTypes() {
        return fieldTypes;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * 类型对应着字符串类型字段
     *
     * @return
     */
    public boolean isString() {
        return this == FieldType.VARCHAR || this == TEXT || this == MEDIUMTEXT || this == LONGTEXT;
    }

}
