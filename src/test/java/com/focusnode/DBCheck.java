package com.focusnode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBCheck {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=FocusNodeDB;user=sa;password=12345;encrypt=true;trustServerCertificate=true;"
            );
            Statement stmt = conn.createStatement();
            
            System.out.println("--- Users ---");
            ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.Users");
            while (rs.next()) {
                System.out.println("User: " + rs.getInt("UserId") + " - " + rs.getString("Username"));
            }
            rs.close();

            System.out.println("--- Folders ---");
            rs = stmt.executeQuery("SELECT * FROM dbo.Folders");
            while (rs.next()) {
                System.out.println("Folder: " + rs.getInt("FolderId") + " - " + rs.getString("Name") + " (UserId: " + rs.getInt("UserId") + ")");
            }
            rs.close();

            System.out.println("--- Notes ---");
            rs = stmt.executeQuery("SELECT * FROM dbo.Notes");
            while (rs.next()) {
                System.out.println("Note: " + rs.getInt("NoteId") + " - " + rs.getString("Title") + " (FolderId: " + rs.getInt("FolderId") + ")");
            }
            rs.close();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
