package com.focusnode.model;

import java.time.LocalDateTime;

public class Folder implements KnowledgeNode {
    private int folderId;
    private int userId;
    private String name;
    private Integer parentId;
    private LocalDateTime createdAt;

    public Folder(int folderId, int userId, String name, Integer parentId, LocalDateTime createdAt) {
        this.folderId = folderId;
        this.userId = userId;
        this.name = name;
        this.parentId = parentId;
        this.createdAt = createdAt;
    }

    public Folder() {}

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int getId() { return folderId; }

    @Override
    public Integer getParentFolderId() { return parentId; }

    @Override
    public LocalDateTime getUpdatedAt() { return createdAt; }

    @Override
    public boolean isDeleted() { return false; /* Handled by DB query currently */ }

    @Override
    public String getType() { return "Folder"; }
}
