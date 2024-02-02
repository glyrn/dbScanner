package bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表信息
 *
 * @author guolinyun
 * @date 2024/2/1
 */
public class DBScannerInfoTable {
    private String tableName;
    private List<DBScanInfoCol> columns = new ArrayList<>();
    private DBScanInfoCol pk;
    private List<DBScannerInfoIndex> indexList = new ArrayList<>();
    private Map<String, DBScannerInfoIndex> indexMap = new HashMap<>();
    private Map<String, DBScanInfoCol> columnMap = new HashMap<>();
    private String collation;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<DBScanInfoCol> getColumns() {
        return columns;
    }

    public void setColumns(List<DBScanInfoCol> columns) {
        this.columns = columns;
    }

    public DBScanInfoCol getPk() {
        return pk;
    }

    public void setPk(DBScanInfoCol pk) {
        this.pk = pk;
    }

    public List<DBScannerInfoIndex> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<DBScannerInfoIndex> indexList) {
        this.indexList = indexList;
    }

    public Map<String, DBScannerInfoIndex> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<String, DBScannerInfoIndex> indexMap) {
        this.indexMap = indexMap;
    }

    public Map<String, DBScanInfoCol> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, DBScanInfoCol> columnMap) {
        this.columnMap = columnMap;
    }

    public String getCollation() {
        if(collation.isEmpty()){
            return FieldType.DEFAULT_CHARSET_COLLATE;
        }
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public String addColumn(DBScanInfoCol column) {
        StringBuffer sb = new StringBuffer("ALTER TABLE `" + getTableName() + "` ADD COLUMN ").append(column.toString())
                .append(";");
        return sb.toString();
    }

    public String dropColumn(String columnName) {
        StringBuffer sb = new StringBuffer("ALTER TABLE `" + getTableName() + "` DROP COLUMN `" + columnName + "`;");
        return sb.toString();
    }

    public String alterColumn(DBScanInfoCol column) {
        StringBuffer sb = new StringBuffer(
                "ALTER TABLE `" + getTableName() + "` MODIFY COLUMN " + column.toString() + ";");
        return sb.toString();
    }

    public String addIndex(DBScannerInfoIndex index) {
        String fieldStr = String.join( ",",index.getFields());
        StringBuffer sb = new StringBuffer("ALTER TABLE `" + getTableName() + "` ADD "+ (index.isUnique()?"UNIQUE":"INDEX") +" ");
        sb.append(index.getIndexName()).append("(").append(fieldStr).append(")");
        sb.append(";");
        return sb.toString();
    }

    public String dropIndex(String indexName) {
        StringBuffer sb = new StringBuffer("ALTER TABLE `" + getTableName() + "` DROP INDEX `" + indexName + "`;");
        return sb.toString();
    }

    public String alterCollate(){
        String collation = getCollation();
        StringBuffer sb = new StringBuffer("ALTER TABLE `" + getTableName() + "` DEFAULT CHARSET=").append(collation.split("_")[0]).append(" COLLATE=").append(collation).append(";");
        return sb.toString();
    }

    public String dropTable() {
        StringBuffer sb = new StringBuffer("DROP TABLE IF EXISTS `" + getTableName() + "`;");
        return sb.toString();
    }

    public String createTable() {
        StringBuffer sb = new StringBuffer(toString());
        for (DBScanInfoCol column : columns) {
            sb.append(column.toString()).append(",");
        }

        sb.append(pk.toPKString());

        if (indexList != null && indexList.size() > 0) {
            sb.append(",");
        }

        int size = indexList.size();
        for (DBScannerInfoIndex infoIndex : indexList) {
            size = size - 1;
            sb.append(infoIndex.toString());
            if (size > 0) {
                sb.append(",");
            }
        }

        String collation = getCollation();
        sb.append(") ").append("ENGINE=InnoDB DEFAULT CHARSET=").append(collation.split("_")[0]).append(" COLLATE=").append(collation).append(";");
        return sb.toString();
    }



    public String toString() {
        return "CREATE TABLE `" + this.getTableName() + "` (";
    }
}
