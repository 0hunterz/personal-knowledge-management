package com.focusnode.repository;

import com.focusnode.model.FocusSession;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FocusSessionRepository {
    public List<FocusSession> findAll() {
        List<FocusSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM focus_sessions";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(new FocusSession(
                    rs.getInt("id"),
                    rs.getInt("task_id"),
                    rs.getInt("note_id"),
                    LocalDateTime.parse(rs.getString("start_time")),
                    LocalDateTime.parse(rs.getString("end_time")),
                    rs.getInt("duration"),
                    rs.getString("mode"),
                    rs.getInt("completed") == 1,
                    rs.getInt("interruptions"),
                    rs.getInt("focus_score"),
                    rs.getString("distractions")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void add(FocusSession session) {
        String sql = "INSERT INTO focus_sessions(task_id, note_id, start_time, end_time, duration, mode, completed, interruptions, focus_score, distractions) " +
                     "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, session.getTaskId());
            pstmt.setInt(2, session.getNoteId());
            pstmt.setString(3, session.getStartTime() != null ? session.getStartTime().toString() : LocalDateTime.now().toString());
            pstmt.setString(4, session.getEndTime() != null ? session.getEndTime().toString() : LocalDateTime.now().toString());
            pstmt.setInt(5, session.getDuration());
            pstmt.setString(6, session.getMode());
            pstmt.setInt(7, session.isCompleted() ? 1 : 0);
            pstmt.setInt(8, session.getInterruptions());
            pstmt.setInt(9, session.getFocusScore());
            pstmt.setString(10, session.getDistractions());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
