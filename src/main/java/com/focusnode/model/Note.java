package com.focusnode.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Note {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty preview;
    private final StringProperty content;
    private final ObservableList<String> tags;
    private final StringProperty category;
    private final StringProperty masteryLevel;
    private final StringProperty reviewStatus;
    private final ObjectProperty<LocalDate> updatedAt;

    public Note(int id, String title, String preview, String content, List<String> tags, String category, String masteryLevel, String reviewStatus, LocalDate updatedAt) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.preview = new SimpleStringProperty(preview);
        this.content = new SimpleStringProperty(content);
        this.tags = FXCollections.observableArrayList(tags);
        this.category = new SimpleStringProperty(category);
        this.masteryLevel = new SimpleStringProperty(masteryLevel);
        this.reviewStatus = new SimpleStringProperty(reviewStatus);
        this.updatedAt = new SimpleObjectProperty<>(updatedAt);
    }

    public Note(String title, String preview, List<String> tags, LocalDate updatedAt) {
        this(-1, title, preview, "", tags, "Uncategorized", "New", "Needs Review", updatedAt);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getPreview() { return preview.get(); }
    public void setPreview(String value) { preview.set(value); }
    public StringProperty previewProperty() { return preview; }

    public String getContent() { return content.get(); }
    public void setContent(String value) { content.set(value); }
    public StringProperty contentProperty() { return content; }

    public ObservableList<String> getTags() { return tags; }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { category.set(value); }
    public StringProperty categoryProperty() { return category; }

    public String getMasteryLevel() { return masteryLevel.get(); }
    public void setMasteryLevel(String value) { masteryLevel.set(value); }
    public StringProperty masteryLevelProperty() { return masteryLevel; }

    public String getReviewStatus() { return reviewStatus.get(); }
    public void setReviewStatus(String value) { reviewStatus.set(value); }
    public StringProperty reviewStatusProperty() { return reviewStatus; }

    public LocalDate getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDate value) { updatedAt.set(value); }
    public ObjectProperty<LocalDate> updatedAtProperty() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return getId() == note.getId() &&
               Objects.equals(getTitle(), note.getTitle()) &&
               Objects.equals(getPreview(), note.getPreview()) &&
               Objects.equals(getContent(), note.getContent()) &&
               Objects.equals(getTags(), note.getTags()) &&
               Objects.equals(getCategory(), note.getCategory()) &&
               Objects.equals(getMasteryLevel(), note.getMasteryLevel()) &&
               Objects.equals(getReviewStatus(), note.getReviewStatus()) &&
               Objects.equals(getUpdatedAt(), note.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getPreview(), getContent(), getTags(), getCategory(), getMasteryLevel(), getReviewStatus(), getUpdatedAt());
    }
}
