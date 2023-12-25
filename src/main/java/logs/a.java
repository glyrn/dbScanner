package logs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class JdbcExample {
    public static void main(String[] args) {
        // 加载 MySQL 驱动程序
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // 构建数据库连接 URL
        String url = "jdbc:mysql://localhost:3306/mydatabase";

        // 提供数据库连接的用户名和密码
        String username = "your_username";
        String password = "your_password";

        // 建立数据库连接
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // 连接成功，可以执行数据库操作
            // ...
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}