package com.focusnode;

import com.focusnode.repository.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectSQL {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Đang kiểm tra kết nối tới cơ sở dữ liệu SQL Server...");
        System.out.println("=================================================");

        try {
            // Thử lấy connection từ DatabaseManager
            Connection conn = DatabaseManager.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("\n[SUCCESS] Kết nối với cơ sở dữ liệu thành công!");
                System.out.println("Thông tin kết nối hoạt động tốt.");
                conn.close();
            } else {
                System.out.println("\n[ERROR] Kết nối không hợp lệ hoặc đã bị đóng!");
            }
        } catch (SQLException e) {
            System.err.println("\n[FAILED] Kết nối thất bại!");
            System.err.println("Lỗi chi tiết: " + e.getMessage());
            System.err.println("\nVui lòng kiểm tra lại:");
            System.err.println("1. SQL Server đã được bật và TCP/IP (port 1433) hoạt động.");
            System.err.println("2. Tài khoản 'sa' với mật khẩu '12345' có đúng hay không.");
            System.err.println("3. Database 'KnowledgeDB' đã được tạo hay chưa.");
        }
        System.out.println("=================================================");
    }
}
