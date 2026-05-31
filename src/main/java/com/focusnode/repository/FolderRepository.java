package com.focusnode.repository;

import com.focusnode.model.Folder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderRepository {

    public Folder getFolderById(int folderId) {
        String sql = "SELECT * FROM Folders WHERE FolderId = ? AND (IsDeleted = 0 OR IsDeleted IS NULL)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, folderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Folder(
                            rs.getInt("FolderId"),
                            rs.getInt("UserId"),
                            rs.getString("Name"),
                            rs.getObject("ParentId") != null ? rs.getInt("ParentId") : null,
                            rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Folder> getFoldersByUserIdAndParent(int userId, Integer parentId) {
        List<Folder> folders = new ArrayList<>();
        String sql = "SELECT * FROM Folders WHERE UserId = ? AND (IsDeleted = 0 OR IsDeleted IS NULL) AND ParentId " + (parentId == null ? "IS NULL" : "= ?") + " ORDER BY Name ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            if (parentId != null) {
                stmt.setInt(2, parentId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Folder f = new Folder(
                            rs.getInt("FolderId"),
                            rs.getInt("UserId"),
                            rs.getString("Name"),
                            rs.getObject("ParentId") != null ? rs.getInt("ParentId") : null,
                            rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null
                    );
                    folders.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folders;
    }

    public List<Folder> getDeletedFoldersByUserId(int userId) {
        List<Folder> folders = new ArrayList<>();
        String sql = "SELECT * FROM Folders WHERE UserId = ? AND IsDeleted = 1 ORDER BY Name ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Folder f = new Folder(
                            rs.getInt("FolderId"),
                            rs.getInt("UserId"),
                            rs.getString("Name"),
                            rs.getObject("ParentId") != null ? rs.getInt("ParentId") : null,
                            rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null
                    );
                    folders.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folders;
    }

    public boolean createFolder(Folder folder) {
        String sql = "INSERT INTO Folders (UserId, Name, ParentId, IsDeleted) VALUES (?, ?, ?, 0)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, folder.getUserId());
            stmt.setString(2, folder.getName());
            if (folder.getParentId() != null) {
                stmt.setInt(3, folder.getParentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        folder.setFolderId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteFolder(int folderId) {
        String sql = "UPDATE Folders SET IsDeleted = 1 WHERE FolderId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, folderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restoreFolder(int folderId) {
        String sql = "UPDATE Folders SET IsDeleted = 0 WHERE FolderId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, folderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFolderPermanently(int folderId) {
        String sql = "DELETE FROM Folders WHERE FolderId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, folderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean renameFolder(int folderId, String newName) {
        String sql = "UPDATE Folders SET Name = ? WHERE FolderId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, newName);
            stmt.setInt(2, folderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
