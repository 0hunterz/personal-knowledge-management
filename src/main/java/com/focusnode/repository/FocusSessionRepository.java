package com.focusnode.repository;

import com.focusnode.model.FocusSession;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FocusSessionRepository {
    public List<FocusSession> findAll() {
        List<FocusSession> sessions = new ArrayList<>();
        String sql = "SELECT SessionId, UserId, TaskId, NoteId, PresetId, StartedAt, EndedAt, PlannedMinutes, ActualMinutes, IsCompleted FROM dbo.FocusSessions";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDateTime endedAt = rs.getTimestamp("EndedAt") != null ? rs.getTimestamp("EndedAt").toLocalDateTime() : null;
                
                sessions.add(new FocusSession(
                    rs.getInt("SessionId"),
                    rs.getInt("UserId"),
                    rs.getInt("TaskId"),
                    rs.getInt("NoteId"),
                    rs.getInt("PresetId"),
                    rs.getTimestamp("StartedAt").toLocalDateTime(),
                    endedAt,
                    rs.getInt("PlannedMinutes"),
                    rs.getInt("ActualMinutes"),
                    rs.getBoolean("IsCompleted")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void add(FocusSession session) {
        String sql = "INSERT INTO dbo.FocusSessions(UserId, TaskId, NoteId, PresetId, StartedAt, EndedAt, PlannedMinutes, ActualMinutes, IsCompleted) " +
                     "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            int userId = session.getUserId() > 0 ? session.getUserId() : 1;
            pstmt.setInt(1, userId);
            
            if (session.getTaskId() > 0) pstmt.setInt(2, session.getTaskId()); 
            else pstmt.setNull(2, java.sql.Types.INTEGER);
            
            if (session.getNoteId() > 0) pstmt.setInt(3, session.getNoteId()); 
            else pstmt.setNull(3, java.sql.Types.INTEGER);

            if (session.getPresetId() > 0) pstmt.setInt(4, session.getPresetId()); 
            else pstmt.setNull(4, java.sql.Types.INTEGER);
            
            pstmt.setTimestamp(5, Timestamp.valueOf(session.getStartedAt() != null ? session.getStartedAt() : LocalDateTime.now()));
            if (session.getEndedAt() != null) pstmt.setTimestamp(6, Timestamp.valueOf(session.getEndedAt())); else pstmt.setNull(6, java.sql.Types.TIMESTAMP);
            
            pstmt.setInt(7, session.getPlannedMinutes());
            pstmt.setInt(8, session.getActualMinutes());
            pstmt.setBoolean(9, session.isCompleted());
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
