import conf.DbCfg;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 与数据库建立连接对象

 */
@Slf4j
public class DBExcutor implements Closeable {

    static class Conn {
        @Getter
        private int index;
        Connection connection = null;
        List<String> excuteSqlRecords = new ArrayList<>();

        public Conn(int index) {
            this.index = index;
        }
    }


    private Conn conn = new Conn(0);

    /**
     * 与数据库建立连接
     *
     * @return
     */
    public boolean init(DbCfg dbCfg) {
        log.info("init is called");
        String driverClass = "com.mysql.jdbc.Driver";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String dbUrl = dbCfg.getDbUrl();

            String _url = dbUrl.substring(0, dbUrl.lastIndexOf("/"));
            String database = dbUrl.split("/")[3];


            String checkDB = "show databases like \"" + database + "\";";

            String usr = dbCfg.getUsr();
            String pwd = dbCfg.getPwd();

            try (
                    Connection connection = DriverManager.getConnection(_url, usr, pwd);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(checkDB);
            ) {
                // 建立连接
                conn.connection = connection;

                if (resultSet.next()) {
                    // 数据库存在则不处理
                    System.out.println("database is exist !");
                } else {
                    System.out.println(1);
                    // 数据库不存在 -->创建数据库
                    // 扫库
                    String excuteCreateDB = "CREATE DATABASE " + database + ";";
                    update(excuteCreateDB, true);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }finally {
                conn.connection = null;
            }

            if (dbUrl.indexOf("characterEncoding") < 0) {
                if (dbUrl.indexOf("?") < 0) {
                    dbUrl = dbUrl + "?characterEncoding=utf8";
                } else {
                    dbUrl = dbUrl + "&characterEncoding=utf8";
                }
            }

            conn.connection = DriverManager.getConnection(dbUrl, usr, pwd);

            return true;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() throws IOException {
        if (conn != null && conn.connection != null) {
            try {
                conn.connection.close();
                conn.connection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    /**
//     * 查询sql封装 - 废弃
//     */
//    public ResultSet queryBrk(String sql) {
//        try {
//            Connection connection = conn.connection;
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
//            // 存查询记录sql
//            conn.excuteSqlRecords.add(sql);
//            return resultSet;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 查询sql
     * @param sql
     * @return
     */
    public List<Map<String, Object>> query(String sql){
        List<Map<String, Object>> rows = new ArrayList<>();

        try (
                Statement stat = conn.connection.createStatement();
                ResultSet rs = stat.executeQuery(sql);
        ) {
            //数据集信息
            ResultSetMetaData meta = rs.getMetaData();

            List<String> colList = new ArrayList<>();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String name = meta.getColumnName(i);
                colList.add(name);
            }

            while (rs.next()){
                Map<String, Object> columnMap = new LinkedHashMap<>();

                for (String colName : colList) {
                    Object object = rs.getObject(colName);
                    columnMap.put(colName,object);
                }

                rows.add(columnMap);
            }


        } catch (Exception ex) {
            // TODO: log ex = sql
            return null;
        }
        return rows;
    }

    /**
     * 创建sql封装
     */
    public void update(String sql, boolean record) {
        try (
                Statement statement = conn.connection.createStatement()
        ) {

            statement.executeUpdate(sql);
            // 存查询记录sql
            if (record) {
                conn.excuteSqlRecords.add(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询sql封装
     */
    public List<Map<String, Object>> qurey(String sql) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (
                Statement statement = conn.connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
        ){
            // 收集元数据
            ResultSetMetaData meta = rs.getMetaData();
            List<String> colList = new ArrayList<>();
            // 记录列的名字
            for (int i = 1; i < meta.getColumnCount(); i ++) {
                String columnName = meta.getColumnName(i);
                colList.add(columnName);
            }

            while (rs.next()) {
                Map<String, Object> columeMap = new LinkedHashMap<>();

                for (String colName : colList) {
                    Object object = rs.getObject(colName);
                    columeMap.put(colName, object);
                }
                rows.add(columeMap);
            }


        }catch (Exception ex) {
            // TODO 记录日志
            return null;
        }
        return rows;
    }

    /**
     * 获取执行的sql记录
     * @return
     */
    public List<String> getUpdateSqlRecords() {
        List<String> list = new ArrayList<>();
        list.addAll(conn.excuteSqlRecords);
        return list;
    }

    /**
     * 获取数据库所有的表名
     */
    public List<String> getAllTablesName() {
        try {
            DatabaseMetaData metaData = conn.connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", null);

            while (resultSet.next()) {
                System.out.println(resultSet.getString("TABLE_NAME"));
            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
