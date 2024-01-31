package bean;

import java.io.Serializable;

/**
 * 专门存每一个表对应的md5值的表
 * data:
 */
public class DBScannerEntityMD5 implements Serializable {

    private String packageName;
    private String className;
    private String md5;
    private String tableName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
