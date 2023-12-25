import conf.DbCfg;
import lombok.Getter;
import util.YamlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 与数据库建立连接对象
 * <p>
 * 初始化连接 init 创建连接对象 索引 连接对象 执行过的sql 构造方法 创建数据库专用连接 如果存在不管 不存在就创建sql库
 * <p>
 * 关闭连接
 * <p>
 * get执行过的sql
 * <p>
 * 库中全部的表名字
 * <p>
 * 更新sql
 * <p>
 * 查询sql
 */
public class DbExcutor implements Closeable {

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


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
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

    /**
     * 查询sql封装
     */
    public ResultSet query(String sql) {
        try {
            Connection connection = conn.connection;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            // 存查询记录sql
            conn.excuteSqlRecords.add(sql);
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    public List<String> getUpdateSqlRecords() {
        List<String> list = new ArrayList<>();
        list.addAll(conn.excuteSqlRecords);
        return list;
    }
}
