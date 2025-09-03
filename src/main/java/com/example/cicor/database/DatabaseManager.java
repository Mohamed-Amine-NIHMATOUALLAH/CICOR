package com.example.cicor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    static final String DB_URL = "jdbc:mysql://localhost:3306/cicor_db";
    static final String USER = "root";
    private static final String PASS = "";

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}




    /*
    // تهيئة قاعدة البيانات
    public static void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // إنشاء جدول الفئات
            stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "hardware_ver TEXT," +
                    "software_ver TEXT," +
                    "voltage REAL," +
                    "current REAL)");

            // إنشاء جدول الكراتين
            stmt.execute("CREATE TABLE IF NOT EXISTS cardboards (" +
                    "number TEXT PRIMARY KEY," +
                    "category_id INTEGER," +
                    "manufacture_date TEXT," +
                    "quantity INTEGER," +
                    "FOREIGN KEY (category_id) REFERENCES categories(id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // إضافة فئة جديدة
    public static void addCategory(Category cat) {
        String sql = "INSERT INTO categories VALUES (?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cat.getId());
            pstmt.setString(2, cat.getName());
            pstmt.setString(3, cat.getHardwareVersion());
            pstmt.setString(4, cat.getSoftwareVersion());
            pstmt.setDouble(5, cat.getVoltage());
            pstmt.setDouble(6, cat.getCurrent());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}*/