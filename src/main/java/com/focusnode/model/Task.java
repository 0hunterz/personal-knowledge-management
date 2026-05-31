package com.focusnode.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Task {
    public enum Status {
        PENDING("Pending", 1),
        IN_PROGRESS("In Progress", 2),
        COMPLETED("Completed", 3);

        private final String label;
        private final int id;

        Status(String label, int id) {
            this.label = label;
            this.id = id;
        }

        public String getLabel() { return label; }
        public int getId() { return id; }
        
        public static Status fromId(int id) {
            for (Status s : values()) {
                if (s.id == id) return s;
            }
            return PENDING;
        }
    }

    public enum Priority {
        LOW("↓ Low", 1),
        MEDIUM("— Medium", 2),
        HIGH("↑ High", 3);

        private final String label;
        private final int id;

        Priority(String label, int id) {
            this.label = label;
            this.id = id;
        }

        public String getLabel() { return label; }
        public int getId() { return id; }
        
        public static Priority fromId(int id) {
            for (Priority p : values()) {
                if (p.id == id) return p;
            }
            return MEDIUM;
        }
    }

    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty category; // Mapped from the first Tag for UI compatibility
    private final ObjectProperty<Status> status;
    private final ObjectProperty<Priority> priority;
    private final ObjectProperty<LocalDateTime> dueDate;
    private final IntegerProperty estimatedMinutes; // Maps to focusMinutes
    private final IntegerProperty actualMinutes;
    private final ObservableList<String> tags; // Replaces category/taskType conceptually
    private final ObservableList<Integer> linkedNoteIds; // Joined from TaskNotes
    private final BooleanProperty completed;
    private final ObjectProperty<LocalDateTime> createdAt;
    private final BooleanProperty isDeleted;

    public Task(int id, int userId, String title, String description, Status status, Priority priority, 
                LocalDateTime dueDate, int estimatedMinutes, int actualMinutes, List<String> tags, 
                List<Integer> linkedNoteIds, LocalDateTime createdAt, boolean isDeleted) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleObjectProperty<>(status);
        this.priority = new SimpleObjectProperty<>(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.estimatedMinutes = new SimpleIntegerProperty(estimatedMinutes);
        this.actualMinutes = new SimpleIntegerProperty(actualMinutes);
        
        this.tags = FXCollections.observableArrayList(tags != null ? tags : List.of());
        this.category = new SimpleStringProperty(!this.tags.isEmpty() ? this.tags.get(0) : "General");
        
        this.linkedNoteIds = FXCollections.observableArrayList(linkedNoteIds != null ? linkedNoteIds : List.of());
        
        this.completed = new SimpleBooleanProperty(status == Status.COMPLETED);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
        this.isDeleted = new SimpleBooleanProperty(isDeleted);

        // Auto-sync completed property based on status
        this.status.addListener((obs, oldVal, newVal) -> {
            this.completed.set(newVal == Status.COMPLETED);
        });
        
        // Auto-sync category when tags change
        this.tags.addListener((javafx.collections.ListChangeListener.Change<? extends String> c) -> {
            this.category.set(!this.tags.isEmpty() ? this.tags.get(0) : "General");
        });
    }

    public Task(String title, Status status, Priority priority, LocalDateTime dueDate, int estimatedMinutes) {
        this(-1, 1, title, "", status, priority, dueDate, estimatedMinutes, 0, List.of(), List.of(), LocalDateTime.now(), false);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { 
        this.category.set(value);
        if (value != null && !value.isEmpty()) {
            if (this.tags.isEmpty()) this.tags.add(value);
            else this.tags.set(0, value);
        }
    }
    public StringProperty categoryProperty() { return category; }

    public Status getStatus() { return status.get(); }
    public void setStatus(Status value) { status.set(value); }
    public ObjectProperty<Status> statusProperty() { return status; }

    public Priority getPriority() { return priority.get(); }
    public void setPriority(Priority value) { priority.set(value); }
    public ObjectProperty<Priority> priorityProperty() { return priority; }

    public LocalDateTime getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDateTime value) { dueDate.set(value); }
    public ObjectProperty<LocalDateTime> dueDateProperty() { return dueDate; }

    public int getEstimatedMinutes() { return estimatedMinutes.get(); }
    public void setEstimatedMinutes(int value) { estimatedMinutes.set(value); }
    public IntegerProperty estimatedMinutesProperty() { return estimatedMinutes; }
    
    // For backwards compatibility with old UI bindings
    public int getFocusMinutes() { return estimatedMinutes.get(); }
    public void setFocusMinutes(int value) { estimatedMinutes.set(value); }
    public IntegerProperty focusMinutesProperty() { return estimatedMinutes; }

    public int getActualMinutes() { return actualMinutes.get(); }
    public void setActualMinutes(int value) { actualMinutes.set(value); }
    public IntegerProperty actualMinutesProperty() { return actualMinutes; }

    public ObservableList<String> getTags() { return tags; }
    public ObservableList<Integer> getLinkedNoteIds() { return linkedNoteIds; }

    public boolean isCompleted() { return completed.get(); }
    public void setCompleted(boolean value) { completed.set(value); }
    public BooleanProperty completedProperty() { return completed; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    public boolean isDeleted() { return isDeleted.get(); }
    public void setDeleted(boolean value) { isDeleted.set(value); }
    public BooleanProperty isDeletedProperty() { return isDeleted; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
