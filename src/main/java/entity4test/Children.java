package entity4test;

import bean.annotation.DBScanColumn;
import bean.annotation.DBScanEntity;
import bean.annotation.GeneratedValue;
import bean.annotation.DBScanId;
import bean.annotation.DBScanTable;

/**
 * 测试用 后续这个文件夹需要删除
 */
@DBScanEntity
@DBScanTable(name = "children")
public class Children {
    @DBScanId
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @DBScanColumn(name = "id", unique = true)
    private int id;

    @DBScanColumn(name = "egg")
    private int egg;

    @DBScanColumn(name = "pigs")
    private int pigs;

    @DBScanColumn(name = "oaa")
    private int oaa;


}
