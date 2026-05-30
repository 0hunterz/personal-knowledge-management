package com.focusnode.model;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.Objects;

public class Task {
    public enum Status {
        COMPLETED("Completed"),
        IN_PROGRESS("In Progress"),
        PENDING("Pending");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Priority {
        HIGH("↑ High"),
        MEDIUM("— Medium"),
        LOW("↓ Low");

        private final String label;

        Priority(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty category;
    private final ObjectProperty<Status> status;
    private final ObjectProperty<Priority> priority;
    private final ObjectProperty<LocalDate> dueDate;
    private final IntegerProperty focusMinutes;
    private final IntegerProperty actualMinutes;
    private final IntegerProperty linkedNoteId;
    private final StringProperty taskType;
    private final BooleanProperty completed;

    public Task(int id, String title, String description, String category, Status status, Priority priority, LocalDate dueDate, int focusMinutes, int actualMinutes, int linkedNoteId, String taskType) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.status = new SimpleObjectProperty<>(status);
        this.priority = new SimpleObjectProperty<>(priority);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.focusMinutes = new SimpleIntegerProperty(focusMinutes);
        this.actualMinutes = new SimpleIntegerProperty(actualMinutes);
        this.linkedNoteId = new SimpleIntegerProperty(linkedNoteId);
        this.taskType = new SimpleStringProperty(taskType);
        this.completed = new SimpleBooleanProperty(status == Status.COMPLETED);

        // Auto-sync completed property based on status
        this.status.addListener((obs, oldVal, newVal) -> {
            this.completed.set(newVal == Status.COMPLETED);
        });
    }

    public Task(String title, String category, Status status, Priority priority, LocalDate dueDate, int focusMinutes) {
        this(-1, title, "", category, status, priority, dueDate, focusMinutes, 0, -1, "General");
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { category.set(value); }
    public StringProperty categoryProperty() { return category; }

    public Status getStatus() { return status.get(); }
    public void setStatus(Status value) { status.set(value); }
    public ObjectProperty<Status> statusProperty() { return status; }

    public Priority getPriority() { return priority.get(); }
    public void setPriority(Priority value) { priority.set(value); }
    public ObjectProperty<Priority> priorityProperty() { return priority; }

    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate value) { dueDate.set(value); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    public int getFocusMinutes() { return focusMinutes.get(); }
    public void setFocusMinutes(int value) { focusMinutes.set(value); }
    public IntegerProperty focusMinutesProperty() { return focusMinutes; }

    public int getActualMinutes() { return actualMinutes.get(); }
    public void setActualMinutes(int value) { actualMinutes.set(value); }
    public IntegerProperty actualMinutesProperty() { return actualMinutes; }

    public int getLinkedNoteId() { return linkedNoteId.get(); }
    public void setLinkedNoteId(int value) { linkedNoteId.set(value); }
    public IntegerProperty linkedNoteIdProperty() { return linkedNoteId; }

    public String getTaskType() { return taskType.get(); }
    public void setTaskType(String value) { taskType.set(value); }
    public StringProperty taskTypeProperty() { return taskType; }

    public boolean isCompleted() { return completed.get(); }
    public void setCompleted(boolean value) { completed.set(value); }
    public BooleanProperty completedProperty() { return completed; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId() &&
               Objects.equals(getTitle(), task.getTitle()) &&
               Objects.equals(getDescription(), task.getDescription()) &&
               Objects.equals(getCategory(), task.getCategory()) &&
               getStatus() == task.getStatus() &&
               getPriority() == task.getPriority() &&
               Objects.equals(getDueDate(), task.getDueDate()) &&
               getFocusMinutes() == task.getFocusMinutes() &&
               getActualMinutes() == task.getActualMinutes() &&
               getLinkedNoteId() == task.getLinkedNoteId() &&
               Objects.equals(getTaskType(), task.getTaskType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getCategory(), getStatus(), getPriority(), getDueDate(), getFocusMinutes(), getActualMinutes(), getLinkedNoteId(), getTaskType());
    }
}
