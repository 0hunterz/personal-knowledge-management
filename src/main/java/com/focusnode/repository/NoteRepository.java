package com.focusnode.repository;

import com.focusnode.model.Note;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteRepository {

    public List<Note> findAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT n.NoteId, n.UserId, n.SubjectId, n.Title, n.Content, n.CreatedAt, n.UpdatedAt, n.IsDeleted, " +
                     "s.Name AS SubjectName, " +
                     "(SELECT STRING_AGG(t.Name, ',') FROM dbo.NoteTags nt JOIN dbo.Tags t ON nt.TagId = t.TagId WHERE nt.NoteId = n.NoteId) AS TagsList " +
                     "FROM dbo.Notes n " +
                     "LEFT JOIN dbo.Subjects s ON n.SubjectId = s.SubjectId " +
                     "WHERE n.IsDeleted = 0";
                     
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                String tagsStr = rs.getString("TagsList");
                List<String> tags = (tagsStr == null || tagsStr.trim().isEmpty()) ? List.of() : Arrays.asList(tagsStr.split(","));
                
                Integer subjectId = rs.getObject("SubjectId") != null ? rs.getInt("SubjectId") : null;
                String subjectName = rs.getString("SubjectName") != null ? rs.getString("SubjectName") : "";
                
                notes.add(new Note(
                    rs.getInt("NoteId"),
                    rs.getInt("UserId"),
                    subjectId,
                    rs.getString("Title"),
                    rs.getString("Content"),
                    subjectName,
                    tags,
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getBoolean("IsDeleted")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void add(Note note) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = note.getUserId() > 0 ? note.getUserId() : 1; // Default to 1 if not set
                Integer subjectId = getOrCreateSubject(conn, userId, note.getSubjectName());
                
                String sql = "INSERT INTO dbo.Notes(UserId, SubjectId, Title, Content, CreatedAt, UpdatedAt, IsDeleted) " +
                             "VALUES(?,?,?,?,?,?,?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, userId);
                    if (subjectId != null) pstmt.setInt(2, subjectId); else pstmt.setNull(2, java.sql.Types.INTEGER);
                    pstmt.setString(3, note.getTitle());
                    pstmt.setString(4, note.getContent());
                    pstmt.setTimestamp(5, Timestamp.valueOf(note.getCreatedAt() != null ? note.getCreatedAt() : LocalDateTime.now()));
                    pstmt.setTimestamp(6, Timestamp.valueOf(note.getUpdatedAt() != null ? note.getUpdatedAt() : LocalDateTime.now()));
                    pstmt.setBoolean(7, false);
                    pstmt.executeUpdate();
                    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            note.setId(rs.getInt(1));
                        }
                    }
                }
                
                // Process Tags
                updateNoteTags(conn, note.getId(), userId, note.getTags());
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Note note) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = note.getUserId() > 0 ? note.getUserId() : 1;
                Integer subjectId = getOrCreateSubject(conn, userId, note.getSubjectName());
                
                String sql = "UPDATE dbo.Notes SET SubjectId=?, Title=?, Content=?, UpdatedAt=? WHERE NoteId=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    if (subjectId != null) pstmt.setInt(1, subjectId); else pstmt.setNull(1, java.sql.Types.INTEGER);
                    pstmt.setString(2, note.getTitle());
                    pstmt.setString(3, note.getContent());
                    pstmt.setTimestamp(4, Timestamp.valueOf(note.getUpdatedAt() != null ? note.getUpdatedAt() : LocalDateTime.now()));
                    pstmt.setInt(5, note.getId());
                    pstmt.executeUpdate();
                }
                
                // Clear old tags
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM dbo.NoteTags WHERE NoteId=?")) {
                    pstmt.setInt(1, note.getId());
                    pstmt.executeUpdate();
                }
                
                // Add new tags
                updateNoteTags(conn, note.getId(), userId, note.getTags());
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "UPDATE dbo.Notes SET IsDeleted = 1 WHERE NoteId=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Helper Methods for 3NF Normalization handling
    
    private Integer getOrCreateSubject(Connection conn, int userId, String subjectName) throws SQLException {
        if (subjectName == null || subjectName.trim().isEmpty()) return null;
        subjectName = subjectName.trim();
        
        // Try to find it
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT SubjectId FROM dbo.Subjects WHERE Name = ? AND UserId = ?")) {
            pstmt.setString(1, subjectName);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        
        // Create it if not found
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO dbo.Subjects(UserId, Name) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, subjectName);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }
    
    private void updateNoteTags(Connection conn, int noteId, int userId, List<String> tagNames) throws SQLException {
        if (tagNames == null || tagNames.isEmpty()) return;
        
        for (String tagName : tagNames) {
            tagName = tagName.trim();
            if (tagName.startsWith("#")) tagName = tagName.substring(1); // remove # if exists
            if (tagName.isEmpty()) continue;
            
            int tagId = -1;
            // Find Tag
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT TagId FROM dbo.Tags WHERE Name = ? AND UserId = ?")) {
                pstmt.setString(1, tagName);
                pstmt.setInt(2, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) tagId = rs.getInt(1);
                }
            }
            
            // Insert Tag if not exists
            if (tagId == -1) {
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO dbo.Tags(UserId, Name) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, tagName);
                    pstmt.executeUpdate();
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) tagId = rs.getInt(1);
                    }
                }
            }
            
            // Insert into NoteTags
            if (tagId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO dbo.NoteTags(NoteId, TagId) VALUES(?, ?)")) {
                    pstmt.setInt(1, noteId);
                    pstmt.setInt(2, tagId);
                    pstmt.executeUpdate();
                } catch (SQLException ignore) {
                    // Ignore duplicate key exception if note already has this tag
                }
            }
        }
    }
}
