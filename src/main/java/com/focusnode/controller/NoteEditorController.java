package com.focusnode.controller;

import com.focusnode.model.Note;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NoteEditorController {

    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private ComboBox<String> masteryComboBox;
    @FXML private ComboBox<String> reviewComboBox;
    @FXML private TextArea contentArea;
    @FXML private TextField tagsField;
    @FXML private Button deleteButton;

    private Note currentNote;
    private Runnable onSaveCallback;
    private Runnable onCancelCallback;

    @FXML
    public void initialize() {
        masteryComboBox.setItems(FXCollections.observableArrayList("Chưa hiểu", "Đang hiểu", "Đã hiểu", "Cần ôn"));
        reviewComboBox.setItems(FXCollections.observableArrayList("Mới", "Đang ôn", "Tốt", "Quên"));
        
        masteryComboBox.getSelectionModel().selectFirst();
        reviewComboBox.getSelectionModel().selectFirst();
    }

    public void setNote(Note note, Runnable onSave, Runnable onCancel) {
        this.currentNote = note;
        this.onSaveCallback = onSave;
        this.onCancelCallback = onCancel;
        if (note != null) {
            deleteButton.setVisible(true);
            deleteButton.setManaged(true);
            titleField.setText(note.getTitle());
            categoryField.setText(note.getCategory());
            masteryComboBox.getSelectionModel().select(note.getMasteryLevel());
            reviewComboBox.getSelectionModel().select(note.getReviewStatus());
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

        String previewText = contentArea.getText();
        if (previewText.length() > 100) {
            previewText = previewText.substring(0, 100) + "...";
        }

        if (currentNote == null) {
            // Note creation logic should happen in parent or service
            currentNote = new Note(
                    titleField.getText(),
                    previewText,
                    Arrays.stream(tagsField.getText().split(",")).map(String::trim).collect(Collectors.toList()),
                    LocalDate.now()
            );
        } else {
            currentNote.setTitle(titleField.getText());
            currentNote.setPreview(previewText);
            currentNote.getTags().setAll(Arrays.stream(tagsField.getText().split(",")).map(String::trim).collect(Collectors.toList()));
            currentNote.setUpdatedAt(LocalDate.now());
        }
        
        currentNote.setContent(contentArea.getText());
        currentNote.setCategory(categoryField.getText());
        currentNote.setMasteryLevel(masteryComboBox.getValue());
        currentNote.setReviewStatus(reviewComboBox.getValue());

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
        categoryField.clear();
        masteryComboBox.getSelectionModel().selectFirst();
        reviewComboBox.getSelectionModel().selectFirst();
        contentArea.clear();
        tagsField.clear();
    }

    public Note getCurrentNote() {
        return currentNote;
    }
}
