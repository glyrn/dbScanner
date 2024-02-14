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
@DBScanTable(name = "people")
public class People {
//    @Column(name = "age", type = FieldType.INT, size = 2, defaultValue = "1")

    @DBScanId
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @DBScanColumn(name = "id", unique = true)
    private int id = 0;



    @DBScanColumn(name = "ali")
    private String ali;

    @DBScanColumn(name = "tx")
    private int tx;
}
