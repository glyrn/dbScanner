package entity4test;

import bean.FieldType;
import bean.annotation.DBScanColumn;
import bean.annotation.DBScanColumnExt;
import bean.annotation.DBScanId;
import bean.annotation.DBScanTable;
import bean.annotation.GeneratedValue;
import bean.annotation.UniqueConstraint;

import java.util.Date;

@DBScanTable(name = "scannerTest", uniqueConstraints = @UniqueConstraint(name = "playerIdName", columnNames = {"playerId", "playerName"}))
public class ScannerTestEntity {
    @DBScanId
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @DBScanColumnExt(name = "id", unique = true)
    private int id ;


    @DBScanColumnExt(name = "playerId", size = 10)
//    @DBScanColumnExt(name = "playerId", extra = "unsigned", size = 10)
    private int playerId;


    @DBScanColumnExt(name = "playerName", type = FieldType.VARCHAR, size = 66)
    private String playerName = "";

    @DBScanColumnExt(name = "createTime", nullable = false)
    protected Date createTime = null;


}
