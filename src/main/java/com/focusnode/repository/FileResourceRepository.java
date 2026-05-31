package com.focusnode.repository;

import com.focusnode.model.FileResource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileResourceRepository {

    public List<FileResource> getFilesByUserIdAndFolder(int userId, Integer folderId) {
        List<FileResource> files = new ArrayList<>();
        // Only select files that are NOT attached to any note, i.e., standalone files in the folder.
        // Or actually, wait. If a file is in a folder, it's a standalone file.
        // We will fetch files that match the folderId. Note attachments might have a FolderId too, or maybe not.
        // For Google Drive behavior, if a file has a FolderId, it belongs there.
        String sql = "SELECT * FROM dbo.FileResources WHERE UserId = ? AND FolderId " + (folderId == null ? "IS NULL" : "= ?") + " AND (IsDeleted = 0 OR IsDeleted IS NULL) ORDER BY FileName ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            if (folderId != null) {
                stmt.setInt(2, folderId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FileResource file = new FileResource(
                            rs.getInt("FileId"),
                            rs.getInt("UserId"),
                            rs.getObject("FolderId") != null ? rs.getInt("FolderId") : null,
                            rs.getString("FileName"),
                            rs.getString("FilePath"),
                            rs.getInt("FileTypeId"),
                            rs.getLong("SizeBytes"),
                            rs.getTimestamp("UploadedAt") != null ? rs.getTimestamp("UploadedAt").toLocalDateTime() : null,
                            rs.getBoolean("IsDeleted")
                    );
                    files.add(file);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<FileResource> getDeletedFilesByUserId(int userId) {
        List<FileResource> files = new ArrayList<>();
        String sql = "SELECT * FROM dbo.FileResources WHERE UserId = ? AND IsDeleted = 1 ORDER BY FileName ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    files.add(new FileResource(
                            rs.getInt("FileId"),
                            rs.getInt("UserId"),
                            rs.getObject("FolderId") != null ? rs.getInt("FolderId") : null,
                            rs.getString("FileName"),
                            rs.getString("FilePath"),
                            rs.getInt("FileTypeId"),
                            rs.getLong("SizeBytes"),
                            rs.getTimestamp("UploadedAt").toLocalDateTime(),
                            rs.getBoolean("IsDeleted")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return files;
    }

    public boolean addFile(FileResource file) {
        String sql = "INSERT INTO dbo.FileResources (UserId, FolderId, FileName, FilePath, FileTypeId, SizeBytes, UploadedAt, IsDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, file.getUserId() > 0 ? file.getUserId() : 1); // Default user 1
            if (file.getFolderId() != null) {
                stmt.setInt(2, file.getFolderId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, file.getFileName());
            stmt.setString(4, file.getFilePath());
            stmt.setInt(5, file.getFileTypeId() > 0 ? file.getFileTypeId() : 1);
            stmt.setLong(6, file.getSizeBytes());
            stmt.setTimestamp(7, Timestamp.valueOf(file.getUploadedAt() != null ? file.getUploadedAt() : LocalDateTime.now()));
            stmt.setBoolean(8, false);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        file.setFileId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFile(int fileId) {
        String sql = "UPDATE dbo.FileResources SET IsDeleted = 1 WHERE FileId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, fileId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void restoreFile(int fileId) {
        String sql = "UPDATE dbo.FileResources SET IsDeleted = 0 WHERE FileId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFilePermanently(int fileId) {
        String sql = "DELETE FROM dbo.FileResources WHERE FileId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean renameFile(int fileId, String newFileName) {
        String sql = "UPDATE dbo.FileResources SET FileName = ? WHERE FileId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, newFileName);
            stmt.setInt(2, fileId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
