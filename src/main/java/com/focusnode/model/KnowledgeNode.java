package com.focusnode.model;

import java.time.LocalDateTime;

public interface KnowledgeNode {
    int getId();
    String getName();
    Integer getParentFolderId();
    LocalDateTime getUpdatedAt();
    boolean isDeleted();
    String getType();
}
