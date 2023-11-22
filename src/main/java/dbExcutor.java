import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import util.YamlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 与数据库建立连接对象
 *
 * 初始化连接 init 创建连接对象 索引 连接对象 执行过的sql 构造方法 创建数据库专用连接 如果存在不管 不存在就创建sql库
 *
 * 关闭连接
 *
 * get执行过的sql
 *
 * 库中全部的表名字
 *
 * 更新sql
 *
 * 查询sql
 *
 */
public class dbExcutor {

    static class Conn{
        @Getter
        private int index;
        Connection connection = null;
        List<String> excuteSqlRecords = new ArrayList<>();
        public Conn(int index){
            this.index = index;
        }
    }


    private Conn conn = new Conn(0);
    /**
     * 与数据库建立连接
     * @return
     */
    public boolean init() {
        String driverClass = "com.mysql.jdbc.Driver";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String qry = "show databases like 'sanguo'";

            Map<String, Object> cfg = YamlUtils.getMap();
            String url = (String) cfg.get("url");
            String password = (String) cfg.get("password");
            String username = (String) cfg.get("username");

            // 建立连接
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(qry);
            conn.connection = connection;
            if (resultSet.next()) {
                // 数据库存在
            }else {
                // 数据库不存在 --创建数据库

            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;

    }
    /**
     * 关闭连接
     */
    public void close() {
        if (conn != null && conn.connection != null) {
            try{
                conn.connection.close();
                conn.connection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
