package entity4test;

import bean.annotation.DBScanColumn;
import bean.annotation.DBScanTable;
import bean.annotation.GeneratedValue;
import bean.annotation.DBScanId;

/**
 * 测试用 后续这个文件夹需要删除
 */
@DBScanTable(name = "children")
public class Children {
    @DBScanId
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @DBScanColumn(name = "id", unique = true)
    private int id;

    @DBScanColumn(name = "egg")
    private String egg;

    @DBScanColumn(name = "pigs")
    private int pigs;

    @DBScanColumn(name = "oaa")
    private int oaa;


}
