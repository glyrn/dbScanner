/**
 * 字段枚举类型
 *
 * @author gly
 */
public enum FieldType {

    /**
     * 数字类型
     */
    BYTE (new Class[]{byte.class, Byte.class}, "TINYINT", 4),
    SHORT(new Class[]{short.class, Short.class}, "SMALLINT", 6);


    /**
     * 变量类型
     */
    private Class<?>[] fieldTypes;
    /**
     * 字段类型
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
