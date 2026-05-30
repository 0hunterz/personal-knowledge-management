package com.focusnode.repository;

import com.focusnode.model.Task;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("category"),
                    Task.Status.valueOf(rs.getString("status")),
                    Task.Priority.valueOf(rs.getString("priority")),
                    LocalDate.parse(rs.getString("due_date")),
                    rs.getInt("focus_minutes"),
                    rs.getInt("actual_minutes"),
                    rs.getInt("linked_note_id"),
                    rs.getString("task_type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void add(Task task) {
        String sql = "INSERT INTO tasks(title, description, category, status, priority, due_date, focus_minutes, actual_minutes, linked_note_id, task_type) " +
                     "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getCategory());
            pstmt.setString(4, task.getStatus().name());
            pstmt.setString(5, task.getPriority().name());
            pstmt.setString(6, task.getDueDate().toString());
            pstmt.setInt(7, task.getFocusMinutes());
            pstmt.setInt(8, task.getActualMinutes());
            pstmt.setInt(9, task.getLinkedNoteId());
            pstmt.setString(10, task.getTaskType());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Task task) {
        String sql = "UPDATE tasks SET title=?, description=?, category=?, status=?, priority=?, due_date=?, focus_minutes=?, actual_minutes=?, linked_note_id=?, task_type=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getCategory());
            pstmt.setString(4, task.getStatus().name());
            pstmt.setString(5, task.getPriority().name());
            pstmt.setString(6, task.getDueDate().toString());
            pstmt.setInt(7, task.getFocusMinutes());
            pstmt.setInt(8, task.getActualMinutes());
            pstmt.setInt(9, task.getLinkedNoteId());
            pstmt.setString(10, task.getTaskType());
            pstmt.setInt(11, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
