package com.focusnode.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // SQL Server Connection details
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=KnowledgeDB;user=sa;password=12345;encrypt=true;trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create Tasks table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tasks' AND xtype='U') " +
                    "CREATE TABLE tasks (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "title NVARCHAR(255) NOT NULL, " +
                    "description NVARCHAR(MAX) DEFAULT '', " +
                    "category NVARCHAR(100) NOT NULL, " +
                    "status NVARCHAR(50) NOT NULL, " +
                    "priority NVARCHAR(50) NOT NULL, " +
                    "due_date NVARCHAR(50) NOT NULL, " +
                    "focus_minutes INT NOT NULL, " +
                    "actual_minutes INT DEFAULT 0, " +
                    "linked_note_id INT DEFAULT -1, " +
                    "task_type NVARCHAR(50) DEFAULT 'General'" +
                    ")");

            // Create Notes table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='notes' AND xtype='U') " +
                    "CREATE TABLE notes (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "title NVARCHAR(255) NOT NULL, " +
                    "preview NVARCHAR(500) NOT NULL, " +
                    "content NVARCHAR(MAX) DEFAULT '', " +
                    "tags NVARCHAR(255) NOT NULL, " +
                    "category NVARCHAR(100) DEFAULT 'Uncategorized', " +
                    "mastery_level NVARCHAR(50) DEFAULT 'New', " +
                    "review_status NVARCHAR(50) DEFAULT 'Needs Review', " +
                    "updated_at NVARCHAR(50) NOT NULL" +
                    ")");

            // Create Focus Sessions table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='focus_sessions' AND xtype='U') " +
                    "CREATE TABLE focus_sessions (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "task_id INT DEFAULT -1, " +
                    "note_id INT DEFAULT -1, " +
                    "start_time NVARCHAR(50) NOT NULL, " +
                    "end_time NVARCHAR(50) NOT NULL, " +
                    "duration INT NOT NULL, " +
                    "mode NVARCHAR(50) NOT NULL, " +
                    "completed INT NOT NULL, " +
                    "interruptions INT DEFAULT 0, " +
                    "focus_score INT DEFAULT 0, " +
                    "distractions NVARCHAR(MAX) DEFAULT ''" +
                    ")");
            try { stmt.execute("IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'focus_score' AND Object_ID = Object_ID(N'focus_sessions')) ALTER TABLE focus_sessions ADD focus_score INT DEFAULT 0"); } catch (Exception e) {}
            try { stmt.execute("IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'distractions' AND Object_ID = Object_ID(N'focus_sessions')) ALTER TABLE focus_sessions ADD distractions NVARCHAR(MAX) DEFAULT ''"); } catch (Exception e) {}

            // Create Review Items table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='review_items' AND xtype='U') " +
                    "CREATE TABLE review_items (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "note_id INT NOT NULL, " +
                    "question NVARCHAR(MAX) NOT NULL, " +
                    "answer NVARCHAR(MAX) NOT NULL, " +
                    "difficulty NVARCHAR(50) NOT NULL, " +
                    "next_review_date NVARCHAR(50) NOT NULL, " +
                    "repetitions INT DEFAULT 0, " +
                    "ease_factor FLOAT DEFAULT 2.5, " +
                    "interval INT DEFAULT 0" +
                    ")");
            try { stmt.execute("IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'repetitions' AND Object_ID = Object_ID(N'review_items')) ALTER TABLE review_items ADD repetitions INT DEFAULT 0"); } catch (Exception e) {}
            try { stmt.execute("IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'ease_factor' AND Object_ID = Object_ID(N'review_items')) ALTER TABLE review_items ADD ease_factor FLOAT DEFAULT 2.5"); } catch (Exception e) {}
            try { stmt.execute("IF NOT EXISTS(SELECT * FROM sys.columns WHERE Name = N'interval' AND Object_ID = Object_ID(N'review_items')) ALTER TABLE review_items ADD interval INT DEFAULT 0"); } catch (Exception e) {}

            // Create Tag Usages table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tag_usages' AND xtype='U') " +
                    "CREATE TABLE tag_usages (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "name NVARCHAR(100) NOT NULL, " +
                    "value INT NOT NULL, " +
                    "color_hex NVARCHAR(20) NOT NULL" +
                    ")");

            // Create Weekly Focus table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='weekly_focus' AND xtype='U') " +
                    "CREATE TABLE weekly_focus (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "label NVARCHAR(50) NOT NULL, " +
                    "value INT NOT NULL" +
                    ")");

            // Create Category Breakdowns table
            stmt.execute("IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='category_breakdowns' AND xtype='U') " +
                    "CREATE TABLE category_breakdowns (" +
                    "id INT IDENTITY(1,1) PRIMARY KEY, " +
                    "category NVARCHAR(100) NOT NULL, " +
                    "value INT NOT NULL, " +
                    "color_hex NVARCHAR(20) NOT NULL" +
                    ")");
            
            // Note: Since SQL Server might have constraint requirements, we kept it mostly default to map with Java seamlessly.
            seedInitialData(conn);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static void seedInitialData(Connection conn) throws SQLException {
        try (var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM tasks")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        String insertTask = "INSERT INTO tasks(title, description, category, status, priority, due_date, focus_minutes, actual_minutes, linked_note_id, task_type) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (var pstmt = conn.prepareStatement(insertTask)) {
            pstmt.setString(1, "Review knowledge base UI"); pstmt.setString(2, "Review the UI for the knowledge base."); pstmt.setString(3, "Review"); pstmt.setString(4, "IN_PROGRESS"); pstmt.setString(5, "MEDIUM"); pstmt.setString(6, java.time.LocalDate.now().plusDays(1).toString()); pstmt.setInt(7, 45); pstmt.setInt(8, 0); pstmt.setInt(9, -1); pstmt.setString(10, "Review"); pstmt.executeUpdate();
            pstmt.setString(1, "Read AI research papers"); pstmt.setString(2, "Read the latest AI papers on Arxiv."); pstmt.setString(3, "Study"); pstmt.setString(4, "PENDING"); pstmt.setString(5, "MEDIUM"); pstmt.setString(6, java.time.LocalDate.now().plusDays(4).toString()); pstmt.setInt(7, 50); pstmt.setInt(8, 0); pstmt.setInt(9, -1); pstmt.setString(10, "Read"); pstmt.executeUpdate();
            pstmt.setString(1, "Finish product design mockups"); pstmt.setString(2, "Design the fxml layout."); pstmt.setString(3, "Project"); pstmt.setString(4, "IN_PROGRESS"); pstmt.setString(5, "HIGH"); pstmt.setString(6, java.time.LocalDate.now().plusDays(2).toString()); pstmt.setInt(7, 60); pstmt.setInt(8, 30); pstmt.setInt(9, -1); pstmt.setString(10, "Project"); pstmt.executeUpdate();
        }

        String insertNote = "INSERT INTO notes(title, preview, content, tags, category, mastery_level, review_status, updated_at) VALUES(?,?,?,?,?,?,?,?)";
        try (var pstmt = conn.prepareStatement(insertNote)) {
            pstmt.setString(1, "Weekly focus review"); pstmt.setString(2, "Summarize what worked this week and what to improve."); pstmt.setString(3, "Detailed notes about the weekly focus."); pstmt.setString(4, "#focus,#review"); pstmt.setString(5, "Productivity"); pstmt.setString(6, "Đã hiểu"); pstmt.setString(7, "Good"); pstmt.setString(8, java.time.LocalDate.now().minusDays(1).toString()); pstmt.executeUpdate();
            pstmt.setString(1, "JavaFX dashboard ideas"); pstmt.setString(2, "Use chart components for analytics and keep layout responsive."); pstmt.setString(3, "Detailed notes about JavaFX charts."); pstmt.setString(4, "#java,#ui"); pstmt.setString(5, "Programming"); pstmt.setString(6, "Đang hiểu"); pstmt.setString(7, "Needs Review"); pstmt.setString(8, java.time.LocalDate.now().minusDays(2).toString()); pstmt.executeUpdate();
        }

        String insertTag = "INSERT INTO tag_usages(name, value, color_hex) VALUES(?,?,?)";
        try (var pstmt = conn.prepareStatement(insertTag)) {
            pstmt.setString(1, "Study"); pstmt.setInt(2, 28); pstmt.setString(3, "#10B981"); pstmt.executeUpdate();
            pstmt.setString(1, "Project"); pstmt.setInt(2, 24); pstmt.setString(3, "#3B82F6"); pstmt.executeUpdate();
            pstmt.setString(1, "Java"); pstmt.setInt(2, 20); pstmt.setString(3, "#F59E0B"); pstmt.executeUpdate();
            pstmt.setString(1, "Network"); pstmt.setInt(2, 16); pstmt.setString(3, "#8B5CF6"); pstmt.executeUpdate();
            pstmt.setString(1, "Others"); pstmt.setInt(2, 12); pstmt.setString(3, "#CBD5E1"); pstmt.executeUpdate();
        }

        String insertWeekly = "INSERT INTO weekly_focus(label, value) VALUES(?,?)";
        try (var pstmt = conn.prepareStatement(insertWeekly)) {
            pstmt.setString(1, "Mon"); pstmt.setInt(2, 200); pstmt.executeUpdate();
            pstmt.setString(1, "Tue"); pstmt.setInt(2, 255); pstmt.executeUpdate();
            pstmt.setString(1, "Wed"); pstmt.setInt(2, 330); pstmt.executeUpdate();
            pstmt.setString(1, "Thu"); pstmt.setInt(2, 365); pstmt.executeUpdate();
            pstmt.setString(1, "Fri"); pstmt.setInt(2, 290); pstmt.executeUpdate();
            pstmt.setString(1, "Sat"); pstmt.setInt(2, 170); pstmt.executeUpdate();
            pstmt.setString(1, "Sun"); pstmt.setInt(2, 175); pstmt.executeUpdate();
        }

        String insertCategory = "INSERT INTO category_breakdowns(category, value, color_hex) VALUES(?,?,?)";
        try (var pstmt = conn.prepareStatement(insertCategory)) {
            pstmt.setString(1, "Study"); pstmt.setInt(2, 45); pstmt.setString(3, "#10B981"); pstmt.executeUpdate();
            pstmt.setString(1, "Project"); pstmt.setInt(2, 30); pstmt.setString(3, "#60A5FA"); pstmt.executeUpdate();
            pstmt.setString(1, "Reading"); pstmt.setInt(2, 15); pstmt.setString(3, "#A78BFA"); pstmt.executeUpdate();
            pstmt.setString(1, "Other"); pstmt.setInt(2, 10); pstmt.setString(3, "#FBBF24"); pstmt.executeUpdate();
        }

        try (var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM review_items")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertReview = "INSERT INTO review_items(note_id, question, answer, difficulty, next_review_date, repetitions, ease_factor, interval) VALUES(?,?,?,?,?,?,?,?)";
                try (var pstmt = conn.prepareStatement(insertReview)) {
                    pstmt.setInt(1, 1); pstmt.setString(2, "What is the core principle of Pomodoro?"); pstmt.setString(3, "Work for 25 mins, rest for 5 mins."); pstmt.setString(4, "Learning"); pstmt.setString(5, java.time.LocalDate.now().toString()); pstmt.setInt(6, 0); pstmt.setDouble(7, 2.5); pstmt.setInt(8, 0); pstmt.executeUpdate();
                    pstmt.setInt(1, 2); pstmt.setString(2, "Why use Virtual Threads in Java 21?"); pstmt.setString(3, "They are lightweight and scale better for blocking I/O tasks."); pstmt.setString(4, "Learning"); pstmt.setString(5, java.time.LocalDate.now().toString()); pstmt.setInt(6, 0); pstmt.setDouble(7, 2.5); pstmt.setInt(8, 0); pstmt.executeUpdate();
                }
            }
        }
    }
}
