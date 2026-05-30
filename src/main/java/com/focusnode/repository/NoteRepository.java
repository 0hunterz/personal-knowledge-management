package com.focusnode.repository;

import com.focusnode.model.Note;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteRepository {
    public List<Note> findAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String tagsStr = rs.getString("tags");
                List<String> tags = tagsStr.isEmpty() ? List.of() : Arrays.asList(tagsStr.split(","));
                notes.add(new Note(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("preview"),
                    rs.getString("content"),
                    tags,
                    rs.getString("category"),
                    rs.getString("mastery_level"),
                    rs.getString("review_status"),
                    LocalDate.parse(rs.getString("updated_at"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void add(Note note) {
        String sql = "INSERT INTO notes(title, preview, content, tags, category, mastery_level, review_status, updated_at) " +
                     "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getPreview());
            pstmt.setString(3, note.getContent());
            pstmt.setString(4, String.join(",", note.getTags()));
            pstmt.setString(5, note.getCategory());
            pstmt.setString(6, note.getMasteryLevel());
            pstmt.setString(7, note.getReviewStatus());
            pstmt.setString(8, note.getUpdatedAt().toString());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    note.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Note note) {
        String sql = "UPDATE notes SET title=?, preview=?, content=?, tags=?, category=?, mastery_level=?, review_status=?, updated_at=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getPreview());
            pstmt.setString(3, note.getContent());
            pstmt.setString(4, String.join(",", note.getTags()));
            pstmt.setString(5, note.getCategory());
            pstmt.setString(6, note.getMasteryLevel());
            pstmt.setString(7, note.getReviewStatus());
            pstmt.setString(8, note.getUpdatedAt().toString());
            pstmt.setInt(9, note.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM notes WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
