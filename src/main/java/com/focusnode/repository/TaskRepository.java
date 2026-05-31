package com.focusnode.repository;

import com.focusnode.model.Task;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskRepository {

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT t.TaskId, t.UserId, t.Title, t.Description, t.DueDate, t.EstimatedMinutes, t.ActualMinutes, t.StatusId, t.PriorityId, t.CreatedAt, t.IsDeleted, " +
                     "(SELECT STRING_AGG(tag.Name, ',') FROM dbo.TaskTags tt JOIN dbo.Tags tag ON tt.TagId = tag.TagId WHERE tt.TaskId = t.TaskId) AS TagsList, " +
                     "(SELECT STRING_AGG(CAST(tn.NoteId AS VARCHAR), ',') FROM dbo.TaskNotes tn WHERE tn.TaskId = t.TaskId) AS NoteIds " +
                     "FROM dbo.Tasks t " +
                     "WHERE t.IsDeleted = 0";
                     
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                String tagsStr = rs.getString("TagsList");
                List<String> tags = (tagsStr == null || tagsStr.trim().isEmpty()) ? List.of() : Arrays.asList(tagsStr.split(","));
                
                String noteIdsStr = rs.getString("NoteIds");
                List<Integer> linkedNoteIds = (noteIdsStr == null || noteIdsStr.trim().isEmpty()) ? List.of() : 
                                              Arrays.stream(noteIdsStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

                LocalDateTime dueDate = rs.getTimestamp("DueDate") != null ? rs.getTimestamp("DueDate").toLocalDateTime() : null;
                
                tasks.add(new Task(
                    rs.getInt("TaskId"),
                    rs.getInt("UserId"),
                    rs.getString("Title"),
                    rs.getString("Description") != null ? rs.getString("Description") : "",
                    Task.Status.fromId(rs.getInt("StatusId")),
                    Task.Priority.fromId(rs.getInt("PriorityId")),
                    dueDate,
                    rs.getInt("EstimatedMinutes"),
                    rs.getInt("ActualMinutes"),
                    tags,
                    linkedNoteIds,
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getBoolean("IsDeleted")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void add(Task task) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = task.getUserId() > 0 ? task.getUserId() : 1;
                
                String sql = "INSERT INTO dbo.Tasks(UserId, Title, Description, DueDate, EstimatedMinutes, ActualMinutes, StatusId, PriorityId, CreatedAt, IsDeleted) " +
                             "VALUES(?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, task.getTitle());
                    pstmt.setString(3, task.getDescription());
                    if (task.getDueDate() != null) pstmt.setTimestamp(4, Timestamp.valueOf(task.getDueDate())); else pstmt.setNull(4, java.sql.Types.TIMESTAMP);
                    pstmt.setInt(5, task.getEstimatedMinutes());
                    pstmt.setInt(6, task.getActualMinutes());
                    pstmt.setInt(7, task.getStatus().getId());
                    pstmt.setInt(8, task.getPriority().getId());
                    pstmt.setTimestamp(9, Timestamp.valueOf(task.getCreatedAt() != null ? task.getCreatedAt() : LocalDateTime.now()));
                    pstmt.setBoolean(10, false);
                    pstmt.executeUpdate();
                    
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            task.setId(rs.getInt(1));
                        }
                    }
                }
                
                updateTaskTags(conn, task.getId(), userId, task.getTags());
                updateTaskNotes(conn, task.getId(), task.getLinkedNoteIds());
                
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Task task) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = task.getUserId() > 0 ? task.getUserId() : 1;
                
                String sql = "UPDATE dbo.Tasks SET Title=?, Description=?, DueDate=?, EstimatedMinutes=?, ActualMinutes=?, StatusId=?, PriorityId=? WHERE TaskId=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, task.getTitle());
                    pstmt.setString(2, task.getDescription());
                    if (task.getDueDate() != null) pstmt.setTimestamp(3, Timestamp.valueOf(task.getDueDate())); else pstmt.setNull(3, java.sql.Types.TIMESTAMP);
                    pstmt.setInt(4, task.getEstimatedMinutes());
                    pstmt.setInt(5, task.getActualMinutes());
                    pstmt.setInt(6, task.getStatus().getId());
                    pstmt.setInt(7, task.getPriority().getId());
                    pstmt.setInt(8, task.getId());
                    pstmt.executeUpdate();
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM dbo.TaskTags WHERE TaskId=?")) {
                    pstmt.setInt(1, task.getId());
                    pstmt.executeUpdate();
                }
                updateTaskTags(conn, task.getId(), userId, task.getTags());
                
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM dbo.TaskNotes WHERE TaskId=?")) {
                    pstmt.setInt(1, task.getId());
                    pstmt.executeUpdate();
                }
                updateTaskNotes(conn, task.getId(), task.getLinkedNoteIds());
                
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
        String sql = "UPDATE dbo.Tasks SET IsDeleted = 1 WHERE TaskId=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateTaskTags(Connection conn, int taskId, int userId, List<String> tagNames) throws SQLException {
        if (tagNames == null || tagNames.isEmpty()) return;
        
        for (String tagName : tagNames) {
            tagName = tagName.trim();
            if (tagName.startsWith("#")) tagName = tagName.substring(1);
            if (tagName.isEmpty()) continue;
            
            int tagId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT TagId FROM dbo.Tags WHERE Name = ? AND UserId = ?")) {
                pstmt.setString(1, tagName);
                pstmt.setInt(2, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) tagId = rs.getInt(1);
                }
            }
            
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
            
            if (tagId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO dbo.TaskTags(TaskId, TagId) VALUES(?, ?)")) {
                    pstmt.setInt(1, taskId);
                    pstmt.setInt(2, tagId);
                    pstmt.executeUpdate();
                } catch (SQLException ignore) {}
            }
        }
    }
    
    private void updateTaskNotes(Connection conn, int taskId, List<Integer> noteIds) throws SQLException {
        if (noteIds == null || noteIds.isEmpty()) return;
        
        for (Integer noteId : noteIds) {
            if (noteId == null || noteId <= 0) continue;
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO dbo.TaskNotes(TaskId, NoteId) VALUES(?, ?)")) {
                pstmt.setInt(1, taskId);
                pstmt.setInt(2, noteId);
                pstmt.executeUpdate();
            } catch (SQLException ignore) {}
        }
    }
}
