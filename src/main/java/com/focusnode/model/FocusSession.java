package com.focusnode.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class FocusSession {
    private final IntegerProperty id;
    private final IntegerProperty userId;
    private final IntegerProperty taskId;
    private final IntegerProperty noteId;
    private final IntegerProperty presetId;
    private final ObjectProperty<LocalDateTime> startedAt;
    private final ObjectProperty<LocalDateTime> endedAt;
    private final IntegerProperty plannedMinutes;
    private final IntegerProperty actualMinutes;
    private final BooleanProperty completed;

    public FocusSession(int id, int userId, int taskId, int noteId, int presetId, LocalDateTime startedAt, LocalDateTime endedAt, int plannedMinutes, int actualMinutes, boolean completed) {
        this.id = new SimpleIntegerProperty(id);
        this.userId = new SimpleIntegerProperty(userId);
        this.taskId = new SimpleIntegerProperty(taskId);
        this.noteId = new SimpleIntegerProperty(noteId);
        this.presetId = new SimpleIntegerProperty(presetId);
        this.startedAt = new SimpleObjectProperty<>(startedAt);
        this.endedAt = new SimpleObjectProperty<>(endedAt);
        this.plannedMinutes = new SimpleIntegerProperty(plannedMinutes);
        this.actualMinutes = new SimpleIntegerProperty(actualMinutes);
        this.completed = new SimpleBooleanProperty(completed);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }
    
    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public int getTaskId() { return taskId.get(); }
    public void setTaskId(int value) { taskId.set(value); }
    public IntegerProperty taskIdProperty() { return taskId; }

    public int getNoteId() { return noteId.get(); }
    public void setNoteId(int value) { noteId.set(value); }
    public IntegerProperty noteIdProperty() { return noteId; }
    
    public int getPresetId() { return presetId.get(); }
    public void setPresetId(int value) { presetId.set(value); }
    public IntegerProperty presetIdProperty() { return presetId; }

    public LocalDateTime getStartedAt() { return startedAt.get(); }
    public void setStartedAt(LocalDateTime value) { startedAt.set(value); }
    public ObjectProperty<LocalDateTime> startedAtProperty() { return startedAt; }

    public LocalDateTime getEndedAt() { return endedAt.get(); }
    public void setEndedAt(LocalDateTime value) { endedAt.set(value); }
    public ObjectProperty<LocalDateTime> endedAtProperty() { return endedAt; }

    public int getPlannedMinutes() { return plannedMinutes.get(); }
    public void setPlannedMinutes(int value) { plannedMinutes.set(value); }
    public IntegerProperty plannedMinutesProperty() { return plannedMinutes; }

    public int getActualMinutes() { return actualMinutes.get(); }
    public void setActualMinutes(int value) { actualMinutes.set(value); }
    public IntegerProperty actualMinutesProperty() { return actualMinutes; }

    public boolean isCompleted() { return completed.get(); }
    public void setCompleted(boolean value) { completed.set(value); }
    public BooleanProperty completedProperty() { return completed; }

    // Old getter mappings for compatibility with FXML
    public LocalDateTime getStartTime() { return startedAt.get(); }
    public ObjectProperty<LocalDateTime> startTimeProperty() { return startedAt; }

    public LocalDateTime getEndTime() { return endedAt.get(); }
    public ObjectProperty<LocalDateTime> endTimeProperty() { return endedAt; }

    public int getDuration() { return actualMinutes.get(); }
    public IntegerProperty durationProperty() { return actualMinutes; }
}
