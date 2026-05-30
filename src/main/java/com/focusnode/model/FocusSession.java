package com.focusnode.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class FocusSession {
    private final IntegerProperty id;
    private final IntegerProperty taskId;
    private final IntegerProperty noteId;
    private final ObjectProperty<LocalDateTime> startTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final IntegerProperty duration;
    private final StringProperty mode;
    private final BooleanProperty completed;
    private final IntegerProperty interruptions;
    private final IntegerProperty focusScore;
    private final StringProperty distractions;

    public FocusSession(int id, int taskId, int noteId, LocalDateTime startTime, LocalDateTime endTime, int duration, String mode, boolean completed, int interruptions, int focusScore, String distractions) {
        this.id = new SimpleIntegerProperty(id);
        this.taskId = new SimpleIntegerProperty(taskId);
        this.noteId = new SimpleIntegerProperty(noteId);
        this.startTime = new SimpleObjectProperty<>(startTime);
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.duration = new SimpleIntegerProperty(duration);
        this.mode = new SimpleStringProperty(mode);
        this.completed = new SimpleBooleanProperty(completed);
        this.interruptions = new SimpleIntegerProperty(interruptions);
        this.focusScore = new SimpleIntegerProperty(focusScore);
        this.distractions = new SimpleStringProperty(distractions != null ? distractions : "");
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getTaskId() { return taskId.get(); }
    public void setTaskId(int value) { taskId.set(value); }
    public IntegerProperty taskIdProperty() { return taskId; }

    public int getNoteId() { return noteId.get(); }
    public void setNoteId(int value) { noteId.set(value); }
    public IntegerProperty noteIdProperty() { return noteId; }

    public LocalDateTime getStartTime() { return startTime.get(); }
    public void setStartTime(LocalDateTime value) { startTime.set(value); }
    public ObjectProperty<LocalDateTime> startTimeProperty() { return startTime; }

    public LocalDateTime getEndTime() { return endTime.get(); }
    public void setEndTime(LocalDateTime value) { endTime.set(value); }
    public ObjectProperty<LocalDateTime> endTimeProperty() { return endTime; }

    public int getDuration() { return duration.get(); }
    public void setDuration(int value) { duration.set(value); }
    public IntegerProperty durationProperty() { return duration; }

    public String getMode() { return mode.get(); }
    public void setMode(String value) { mode.set(value); }
    public StringProperty modeProperty() { return mode; }

    public boolean isCompleted() { return completed.get(); }
    public void setCompleted(boolean value) { completed.set(value); }
    public BooleanProperty completedProperty() { return completed; }

    public int getInterruptions() { return interruptions.get(); }
    public void setInterruptions(int value) { interruptions.set(value); }
    public IntegerProperty interruptionsProperty() { return interruptions; }

    public int getFocusScore() { return focusScore.get(); }
    public void setFocusScore(int value) { focusScore.set(value); }
    public IntegerProperty focusScoreProperty() { return focusScore; }

    public String getDistractions() { return distractions.get(); }
    public void setDistractions(String value) { distractions.set(value); }
    public StringProperty distractionsProperty() { return distractions; }
}
