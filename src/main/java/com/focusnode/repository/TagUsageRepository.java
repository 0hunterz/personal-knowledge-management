package com.focusnode.repository;

import com.focusnode.model.TagUsageItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagUsageRepository {
    public List<TagUsageItem> findAll() {
        List<TagUsageItem> items = new ArrayList<>();
        String sql = "SELECT * FROM tag_usages";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new TagUsageItem(
                    rs.getString("name"),
                    rs.getInt("value"),
                    rs.getString("color_hex")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
