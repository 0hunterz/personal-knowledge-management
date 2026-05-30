package com.focusnode.repository;

import com.focusnode.model.ReviewItem;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReviewItemRepository {
    public List<ReviewItem> findAll() {
        List<ReviewItem> items = new ArrayList<>();
        String sql = "SELECT * FROM review_items";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new ReviewItem(
                    rs.getInt("id"),
                    rs.getInt("note_id"),
                    rs.getString("question"),
                    rs.getString("answer"),
                    rs.getString("difficulty"),
                    LocalDate.parse(rs.getString("next_review_date")),
                    rs.getInt("repetitions"),
                    rs.getDouble("ease_factor"),
                    rs.getInt("interval")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void update(ReviewItem item) {
        String sql = "UPDATE review_items SET next_review_date = ?, repetitions = ?, ease_factor = ?, interval = ?, difficulty = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getNextReviewDate().toString());
            pstmt.setInt(2, item.getRepetitions());
            pstmt.setDouble(3, item.getEaseFactor());
            pstmt.setInt(4, item.getInterval());
            pstmt.setString(5, item.getDifficulty());
            pstmt.setInt(6, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
