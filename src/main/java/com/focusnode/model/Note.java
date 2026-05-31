package com.focusnode.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Note implements KnowledgeNode {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final ObjectProperty<Integer> subjectId;
    private final ObjectProperty<Integer> folderId;
    private final StringProperty title;
    private final StringProperty content;
    private final StringProperty subjectName; // Joined from Subjects
    private final ObservableList<String> tags; // Joined from NoteTags -> Tags
    private final ObjectProperty<LocalDateTime> createdAt;
    private final ObjectProperty<LocalDateTime> updatedAt;
    private final BooleanProperty isDeleted;
    private final ObservableList<FileResource> attachedFiles = FXCollections.observableArrayList();

    public Note(int id, int userId, Integer subjectId, Integer folderId, String title, String content, String subjectName, List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.subjectId = new SimpleObjectProperty<>(subjectId);
        this.folderId = new SimpleObjectProperty<>(folderId);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.subjectName = new SimpleStringProperty(subjectName);
        this.tags = FXCollections.observableArrayList(tags != null ? tags : List.of());
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt);
        this.isDeleted = new SimpleBooleanProperty(isDeleted);
    }

    public Note(int userId, String title, String content, Integer subjectId, Integer folderId) {
        this(-1, userId, subjectId, folderId, title, content, null, List.of(), LocalDateTime.now(), LocalDateTime.now(), false);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public Integer getSubjectId() { return subjectId.get(); }
    public void setSubjectId(Integer value) { subjectId.set(value); }
    public ObjectProperty<Integer> subjectIdProperty() { return subjectId; }

    public Integer getFolderId() { return folderId.get(); }
    public void setFolderId(Integer value) { folderId.set(value); }
    public ObjectProperty<Integer> folderIdProperty() { return folderId; }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getContent() { return content.get(); }
    public void setContent(String value) { content.set(value); }
    public StringProperty contentProperty() { return content; }

    public String getSubjectName() { return subjectName.get(); }
    public void setSubjectName(String value) { subjectName.set(value); }
    public StringProperty subjectNameProperty() { return subjectName; }

    public String getPreview() {
        String text = content.get();
        if (text == null || text.trim().isEmpty()) return "No content";
        return text.length() > 60 ? text.substring(0, 60).trim() + "..." : text;
    }

    public ObservableList<String> getTags() { return tags; }
    
    public ObservableList<FileResource> getAttachedFiles() { return attachedFiles; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime value) { updatedAt.set(value); }
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }

    public boolean isDeleted() { return isDeleted.get(); }
    public void setDeleted(boolean value) { isDeleted.set(value); }
    public BooleanProperty isDeletedProperty() { return isDeleted; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return getId() == note.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String getName() { return getTitle(); }

    @Override
    public Integer getParentFolderId() { return getFolderId(); }

    @Override
    public String getType() { return "Note"; }
}
