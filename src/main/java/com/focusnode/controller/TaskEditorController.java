package com.focusnode.controller;

import com.focusnode.model.Note;
import com.focusnode.model.Task;
import com.focusnode.service.ServiceLocator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class TaskEditorController {

    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private ComboBox<Task.Priority> priorityComboBox;
    @FXML private ComboBox<Task.Status> statusComboBox;
    @FXML private DatePicker dueDatePicker;
    @FXML private Spinner<Integer> estTimeSpinner;
    @FXML private ComboBox<Note> linkedNoteComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button deleteButton;

    private Task currentTask;
    private Runnable onSaveCallback;
    private Runnable onCancelCallback;

    @FXML
    public void initialize() {
        priorityComboBox.setItems(FXCollections.observableArrayList(Task.Priority.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(Task.Status.values()));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 30, 5);
        estTimeSpinner.setValueFactory(valueFactory);

        // Load notes for dropdown
        ServiceLocator.getAsyncExecutor().execute(() -> {
            List<Note> notes = ServiceLocator.getAppDataService().getNotes();
            Platform.runLater(() -> {
                linkedNoteComboBox.setItems(FXCollections.observableArrayList(notes));
            });
        });

        linkedNoteComboBox.setConverter(new StringConverter<Note>() {
            @Override
            public String toString(Note note) {
                return note == null ? "" : note.getTitle();
            }

            @Override
            public Note fromString(String string) {
                return null;
            }
        });
    }

    public void setTask(Task task, Runnable onSave, Runnable onCancel) {
        this.currentTask = task;
        this.onSaveCallback = onSave;
        this.onCancelCallback = onCancel;
        
        if (task != null) {
            deleteButton.setVisible(true);
            deleteButton.setManaged(true);
            titleField.setText(task.getTitle());
            categoryField.setText(task.getCategory());
            priorityComboBox.getSelectionModel().select(task.getPriority());
            statusComboBox.getSelectionModel().select(task.getStatus());
            
            if (task.getDueDate() != null) {
                dueDatePicker.setValue(task.getDueDate().toLocalDate());
            } else {
                dueDatePicker.setValue(null);
            }
            
            estTimeSpinner.getValueFactory().setValue(task.getEstimatedMinutes());
            descriptionArea.setText(task.getDescription());

            // Find linked note
            if (!task.getLinkedNoteIds().isEmpty()) {
                int firstNoteId = task.getLinkedNoteIds().get(0);
                for (Note n : linkedNoteComboBox.getItems()) {
                    if (n.getId() == firstNoteId) {
                        linkedNoteComboBox.getSelectionModel().select(n);
                        break;
                    }
                }
            } else {
                linkedNoteComboBox.getSelectionModel().clearSelection();
            }
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

        int noteId = linkedNoteComboBox.getValue() != null ? linkedNoteComboBox.getValue().getId() : -1;

        if (currentTask == null) {
            currentTask = new Task(
                    titleField.getText(),
                    statusComboBox.getValue() != null ? statusComboBox.getValue() : Task.Status.PENDING,
                    priorityComboBox.getValue() != null ? priorityComboBox.getValue() : Task.Priority.MEDIUM,
                    dueDatePicker.getValue() != null ? dueDatePicker.getValue().atStartOfDay() : LocalDate.now().atStartOfDay(),
                    estTimeSpinner.getValue()
            );
            currentTask.setCategory(categoryField.getText());
            currentTask.setDescription(descriptionArea.getText());
            
            if (noteId != -1) {
                currentTask.getLinkedNoteIds().clear();
                currentTask.getLinkedNoteIds().add(noteId);
            }
            
            // Should add to database
            ServiceLocator.getAppDataService().saveTask(currentTask);
            ServiceLocator.getAppDataService().getTasks().add(currentTask);
        } else {
            currentTask.setTitle(titleField.getText());
            currentTask.setCategory(categoryField.getText());
            currentTask.setPriority(priorityComboBox.getValue() != null ? priorityComboBox.getValue() : Task.Priority.MEDIUM);
            currentTask.setStatus(statusComboBox.getValue() != null ? statusComboBox.getValue() : Task.Status.PENDING);
            currentTask.setDueDate(dueDatePicker.getValue() != null ? dueDatePicker.getValue().atStartOfDay() : LocalDate.now().atStartOfDay());
            currentTask.setEstimatedMinutes(estTimeSpinner.getValue());
            currentTask.setDescription(descriptionArea.getText());
            
            currentTask.getLinkedNoteIds().clear();
            if (noteId != -1) {
                currentTask.getLinkedNoteIds().add(noteId);
            }
            
            ServiceLocator.getAppDataService().saveTask(currentTask);
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }

    @FXML
    private void handleDelete() {
        if (currentTask != null) {
            ServiceLocator.getAppDataService().deleteTask(currentTask);
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
        priorityComboBox.getSelectionModel().select(Task.Priority.MEDIUM);
        statusComboBox.getSelectionModel().select(Task.Status.PENDING);
        dueDatePicker.setValue(LocalDate.now());
        estTimeSpinner.getValueFactory().setValue(30);
        descriptionArea.clear();
        linkedNoteComboBox.getSelectionModel().clearSelection();
    }
}
