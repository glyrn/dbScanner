package entity4test;

import bean.FieldType;
import bean.annotation.DBScanColumn;
import bean.annotation.DBScanId;
import bean.annotation.DBScanTable;
import bean.annotation.GeneratedValue;
import bean.annotation.KeyConstraint;

import java.util.Date;

//@DBScanTable(name = "scannerTest", keyConstraints = @KeyConstraint(name = "playerIdName", columnNames = {"playerId", "playerName"}))
public class ScannerTestEntity {
    @DBScanId
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @DBScanColumn(name = "id", unique = true)
    private int id ;


    @DBScanColumn(name = "playerId", size = 10)
//    @DBScanColumn(name = "playerId", extra = "unsigned", size = 10)
    private int playerId;


    @DBScanColumn(name = "playerName", type = FieldType.VARCHAR, size = 66)
    private String playerName = "";

    @DBScanColumn(name = "createTime", nullable = false)
    protected Date createTime = null;


}
