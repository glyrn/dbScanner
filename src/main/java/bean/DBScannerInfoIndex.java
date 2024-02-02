package bean;

import java.util.HashSet;
import java.util.Set;

/**
 * 扫表
 * 索引信息
 *
 * @author guolinyun
 * @date 2024/2/2
 */
public class DBScannerInfoIndex {
    private String indexName;
    private String[] fields;
    private boolean unique; // 唯一
    public DBScannerInfoIndex(){}

    public static DBScannerInfoIndex valueOf(String indexName, String[] columnNames) {
        DBScannerInfoIndex index = new DBScannerInfoIndex();
        index.setIndexName(indexName);
        index.setFields(columnNames);
        index.setUnique(false);
        return index;
    }

    /**
     * 唯一索引
     * @param columnName
     * @return
     */
    public static DBScannerInfoIndex valueOfUnique(String columnName) {
        String[] fieldsArray = new String[]{columnName};
        String indexName = new StringBuilder("unique_index_").append(columnName).toString().toUpperCase();

        DBScannerInfoIndex index = new DBScannerInfoIndex();
        index.setIndexName(indexName);
        index.setFields(fieldsArray);
        index.setUnique(true);

        return index;
    }

    /**
     * 普通索引
     * @param columnName
     * @return
     */
    public static DBScannerInfoIndex valueOfKey(String columnName) {
        String[] fieldsArray = new String[]{columnName};
        String indexName = new StringBuilder("index_").append(columnName).toString().toUpperCase();

        DBScannerInfoIndex index = new DBScannerInfoIndex();
        index.setIndexName(indexName);
        index.setFields(fieldsArray);
        index.setUnique(false);
        return index;
    }
    /**
     * 通过sql反解析包含的字段
     * @param indexSql
     * @return
     */
    public static Set<String> parseFields(String indexSql) {
        do {
            int i1 = indexSql.indexOf("(");
            int i2 = indexSql.indexOf(")");
            if (i1 < 0 || i2 < 0) {
                break;
            }
            String[] spilt = indexSql.substring(i1 + 1, i2).split(",");
            Set<String> set = new HashSet<>();
            for (String field : spilt) {
                if (field.isEmpty() || field.trim().length() == 0) {
                    continue;
                }
                set.add(field.trim().replaceAll("`", ""));
            }
            return set;

        }while (false);
        return null;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    public String toString() {
        String fieldStr = String.join(",",fields);
        return toKeyString() + " (" + fieldStr + ") USING BTREE";
    }

    /**
     * 判断新旧sql是否一致
     * @param oldSql
     * @return
     */
    public boolean equalsWithOld(String oldSql) {
        String fieldStr = String.join(",",fields);
        String newSql = toKeyString() + " (" + fieldStr + ")";

        int indexOfUsing = oldSql.indexOf(" USING ");
        if (indexOfUsing > -1) {
            String[] spilt = oldSql.split(" ");
            for (int i = 0; i + 1 < spilt.length; i++) {
                if ("USING".equals(spilt[i]) && !"BTREE".equals(spilt[i+1])) {
                    return false;
                }
            }
            newSql += " USING BTREE";
        }
        // 忽略比较索引的最大长度限制
        return newSql.replaceAll("`","").equals(oldSql.replaceAll("`","").replaceAll("\\(191\\)","").replaceAll("\\(255\\)",""));
    }
    public String toKeyString() {
        return (unique ? "UNIQUE ":"")+"KEY `" + getIndexName() + "`";
    }
}
