package com.focusnode.repository;

import com.focusnode.model.CategoryBreakdown;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryBreakdownRepository {
    public List<CategoryBreakdown> findAll() {
        List<CategoryBreakdown> items = new ArrayList<>();
        String sql = "SELECT category, SUM(actual_minutes) as total FROM tasks GROUP BY category HAVING total > 0";
        String[] colors = {"#10B981", "#60A5FA", "#A78BFA", "#FBBF24", "#F87171", "#34D399"};
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            int i = 0;
            while (rs.next()) {
                items.add(new CategoryBreakdown(
                    rs.getString("category"),
                    rs.getInt("total"),
                    colors[i % colors.length]
                ));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (items.isEmpty()) {
            items.add(new CategoryBreakdown("No Data", 1, "#CBD5E1"));
        }
        
        return items;
    }
}
