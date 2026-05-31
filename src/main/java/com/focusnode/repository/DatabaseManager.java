package com.focusnode.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // SQL Server Connection details
    // using user=sa;password=12345;databaseName=FocusNodeDB;
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=FocusNodeDB;user=sa;password=12345;encrypt=true;trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        // Cấu trúc Database (Schema) và Dữ liệu mẫu (Data) 
        // hiện được quản lý hoàn toàn bằng SQL Scripts qua SQL Server Management Studio.
        // Java chỉ thực hiện kết nối, không còn tự tạo bảng để tránh lỗi cấu trúc.
        
        try (Connection conn = getConnection()) {
            System.out.println("Kết nối SQL Server FocusNodeDB thành công!");
        } catch (SQLException e) {
            System.err.println("Kết nối Database thất bại: " + e.getMessage());
        }
    }
}
