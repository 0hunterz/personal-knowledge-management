package com.focusnode.repository;

import com.focusnode.model.CategoryBreakdown;
import com.focusnode.model.FocusPoint;
import com.focusnode.model.TagUsageItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MetricsRepository {

    public List<FocusPoint> getWeeklyFocus(int userId) {
        List<FocusPoint> result = new ArrayList<>();
        // Get total actual minutes for each of the last 7 days
        String sql = """
            SELECT 
                CAST(StartedAt AS DATE) as FocusDate,
                DATENAME(dw, StartedAt) AS DayName, 
                SUM(ActualMinutes) AS TotalMinutes
            FROM dbo.FocusSessions
            WHERE UserId = ? AND StartedAt >= DATEADD(day, -6, CAST(GETDATE() AS DATE))
            GROUP BY CAST(StartedAt AS DATE), DATENAME(dw, StartedAt)
            ORDER BY FocusDate ASC
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dayName = rs.getString("DayName").substring(0, 3); // e.g. Mon, Tue
                    int totalMinutes = rs.getInt("TotalMinutes");
                    result.add(new FocusPoint(dayName, totalMinutes));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<CategoryBreakdown> getCategoryBreakdown(int userId) {
        List<CategoryBreakdown> result = new ArrayList<>();
        // Sum focus minutes per tag (acting as category)
        String sql = """
            SELECT TOP 4 t.Name, SUM(fs.ActualMinutes) AS TotalMinutes, MAX(t.ColorHex) AS ColorHex
            FROM dbo.FocusSessions fs
            JOIN dbo.TaskTags tt ON fs.TaskId = tt.TaskId
            JOIN dbo.Tags t ON tt.TagId = t.TagId
            WHERE fs.UserId = ?
            GROUP BY t.Name
            ORDER BY TotalMinutes DESC
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("Name");
                    int totalMinutes = rs.getInt("TotalMinutes");
                    String colorHex = rs.getString("ColorHex");
                    if (colorHex == null || colorHex.isEmpty()) {
                        colorHex = "#3B82F6"; // default blue
                    }
                    result.add(new CategoryBreakdown(name, totalMinutes, colorHex));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TagUsageItem> getTagUsage(int userId) {
        List<TagUsageItem> result = new ArrayList<>();
        // Count total usage of each tag across tasks and notes
        String sql = """
            SELECT TOP 5 
                t.Name, 
                MAX(t.ColorHex) as ColorHex,
                (SELECT COUNT(*) FROM dbo.TaskTags tt WHERE tt.TagId = t.TagId) + 
                (SELECT COUNT(*) FROM dbo.NoteTags nt WHERE nt.TagId = t.TagId) AS UsageCount
            FROM dbo.Tags t
            WHERE t.UserId = ?
            ORDER BY UsageCount DESC
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("Name");
                    int count = rs.getInt("UsageCount");
                    String colorHex = rs.getString("ColorHex");
                    if (colorHex == null || colorHex.isEmpty()) {
                        colorHex = "#8B5CF6"; // default purple
                    }
                    if (count > 0) {
                        result.add(new TagUsageItem(name, count, colorHex));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
