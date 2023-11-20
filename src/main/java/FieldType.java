/**
 * 字段枚举类型
 *
 * @author gly
 */
public enum FieldType {

    BYTE(new Class[]{Byte.class}, "byte", 0, "defaultCharset"),
    SHORT(new Class[]{Short.class}, "short", 0, "defaultCharset"),
    INT(new Class[]{Integer.class}, "int", 0, "defaultCharset"),
    LONG(new Class[]{Long.class}, "long", 0, "defaultCharset"),
    FLOAT(new Class[]{Float.class}, "float", 0, "defaultCharset"),
    DOUBLE(new Class[]{Double.class}, "double", 0, "defaultCharset"),

    BLOB(new Class[]{Byte[].class}, "blob", 0, "defaultCharset"),
    MEDIUM_BLOB(new Class[]{Byte[].class}, "medium blob", 0, "defaultCharset"),

    VARCHAR(new Class[]{String.class}, "varchar", 255, "defaultCharset"),
    TEXT(new Class[]{String.class}, "text", 0, "defaultCharset"),
    MEDIUM_TEXT(new Class[]{String.class}, "mediumtext", 0, "defaultCharset"),
    LONG_TEXT(new Class[]{String.class}, "longtext", 0, "defaultCharset"),

    NULL(null, "null", 0, "defaultCharset");


    /**
     * 变量类型
     */
    private Class<?>[] fieldTypeClass;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 字段长度
     */
    private int length;
    /**
     * 字符集
     */
    private String charSet;

    FieldType(Class<?>[] fieldTypeClass, String fieldType, int length, String charSet) {
        this.fieldTypeClass = fieldTypeClass;
        this.fieldType = fieldType;
        this.length = length;
        this.charSet = charSet;
    }

}
