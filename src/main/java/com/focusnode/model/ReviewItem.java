package com.focusnode.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ReviewItem {
    private final IntegerProperty id;
    private final IntegerProperty noteId;
    private final StringProperty question;
    private final StringProperty answer;
    private final StringProperty difficulty;
    private final ObjectProperty<LocalDate> nextReviewDate;
    private final IntegerProperty repetitions;
    private final DoubleProperty easeFactor;
    private final IntegerProperty interval;

    public ReviewItem(int id, int noteId, String question, String answer, String difficulty, LocalDate nextReviewDate, int repetitions, double easeFactor, int interval) {
        this.id = new SimpleIntegerProperty(id);
        this.noteId = new SimpleIntegerProperty(noteId);
        this.question = new SimpleStringProperty(question);
        this.answer = new SimpleStringProperty(answer);
        this.difficulty = new SimpleStringProperty(difficulty);
        this.nextReviewDate = new SimpleObjectProperty<>(nextReviewDate);
        this.repetitions = new SimpleIntegerProperty(repetitions);
        this.easeFactor = new SimpleDoubleProperty(easeFactor);
        this.interval = new SimpleIntegerProperty(interval);
    }

    public ReviewItem(int id, int noteId, String question, String answer, String difficulty, LocalDate nextReviewDate) {
        this(id, noteId, question, answer, difficulty, nextReviewDate, 0, 2.5, 0);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getNoteId() { return noteId.get(); }
    public void setNoteId(int value) { noteId.set(value); }
    public IntegerProperty noteIdProperty() { return noteId; }

    public String getQuestion() { return question.get(); }
    public void setQuestion(String value) { question.set(value); }
    public StringProperty questionProperty() { return question; }

    public String getAnswer() { return answer.get(); }
    public void setAnswer(String value) { answer.set(value); }
    public StringProperty answerProperty() { return answer; }

    public String getDifficulty() { return difficulty.get(); }
    public void setDifficulty(String value) { difficulty.set(value); }
    public StringProperty difficultyProperty() { return difficulty; }

    public LocalDate getNextReviewDate() { return nextReviewDate.get(); }
    public void setNextReviewDate(LocalDate value) { nextReviewDate.set(value); }
    public ObjectProperty<LocalDate> nextReviewDateProperty() { return nextReviewDate; }

    public int getRepetitions() { return repetitions.get(); }
    public void setRepetitions(int value) { repetitions.set(value); }
    public IntegerProperty repetitionsProperty() { return repetitions; }

    public double getEaseFactor() { return easeFactor.get(); }
    public void setEaseFactor(double value) { easeFactor.set(value); }
    public DoubleProperty easeFactorProperty() { return easeFactor; }

    public int getInterval() { return interval.get(); }
    public void setInterval(int value) { interval.set(value); }
    public IntegerProperty intervalProperty() { return interval; }
}
