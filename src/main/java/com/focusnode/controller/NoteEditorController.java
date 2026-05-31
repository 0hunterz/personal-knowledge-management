package com.focusnode.controller;

import com.focusnode.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NoteEditorController {

    @FXML private TextField titleField;
    @FXML private TextField subjectField;
    @FXML private TextArea contentArea;
    @FXML private TextField tagsField;
    @FXML private Button deleteButton;

    private Note currentNote;
    private Runnable onSaveCallback;
    private Runnable onCancelCallback;

    @FXML
    public void initialize() {
        // Nothing to initialize since we removed mastery and review combo boxes
    }

    public void setNote(Note note, Runnable onSave, Runnable onCancel) {
        this.currentNote = note;
        this.onSaveCallback = onSave;
        this.onCancelCallback = onCancel;
        if (note != null) {
            deleteButton.setVisible(true);
            deleteButton.setManaged(true);
            titleField.setText(note.getTitle());
            subjectField.setText(note.getSubjectName());
            contentArea.setText(note.getContent());
            tagsField.setText(String.join(", ", note.getTags()));
        } else {
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            clearFields();
        }
    }

    @FXML
    private void handleSave() {
        if (titleField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Title cannot be empty!");
            alert.show();
            return;
        }

        if (currentNote == null) {
            // Note creation logic should happen in parent or service.
            // Using dummy userId 1 for now (will be handled by repository later).
            currentNote = new Note(
                    1, // dummy userId
                    titleField.getText(),
                    contentArea.getText(),
                    null // subjectId will be resolved in repository based on subjectName
            );
            currentNote.getTags().setAll(Arrays.stream(tagsField.getText().split(",")).map(String::trim).collect(Collectors.toList()));
            currentNote.setSubjectName(subjectField.getText());
        } else {
            currentNote.setTitle(titleField.getText());
            currentNote.setContent(contentArea.getText());
            currentNote.getTags().setAll(Arrays.stream(tagsField.getText().split(",")).map(String::trim).collect(Collectors.toList()));
            currentNote.setUpdatedAt(LocalDateTime.now());
            currentNote.setSubjectName(subjectField.getText());
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }

    @FXML
    private void handleDelete() {
        if (currentNote != null) {
            com.focusnode.service.ServiceLocator.getAppDataService().deleteNote(currentNote);
            if (onSaveCallback != null) {
                onSaveCallback.run(); // Refresh and close
            }
        }
    }

    @FXML
    private void handleCancel() {
        if (onCancelCallback != null) {
            onCancelCallback.run();
        }
    }

    private void clearFields() {
        titleField.clear();
        subjectField.clear();
        contentArea.clear();
        tagsField.clear();
    }

    public Note getCurrentNote() {
        return currentNote;
    }
}
