package com.focusnode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBSeed {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=FocusNodeDB;user=sa;password=12345;encrypt=true;trustServerCertificate=true;"
            );
            Statement stmt = conn.createStatement();
            
            // Check if user 1 exists, if not, create it
            try {
                stmt.executeUpdate("SET IDENTITY_INSERT dbo.Users ON");
                stmt.executeUpdate("INSERT INTO dbo.Users (UserId, Username, PasswordHash, Email) VALUES (1, 'defaultuser', 'hash', 'default@focusnode.com')");
                stmt.executeUpdate("SET IDENTITY_INSERT dbo.Users OFF");
                System.out.println("Seeded User 1 successfully!");
            } catch (Exception e) {
                System.out.println("User 1 might already exist or another error occurred: " + e.getMessage());
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
