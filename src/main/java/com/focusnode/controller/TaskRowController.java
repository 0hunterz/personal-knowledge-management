package com.focusnode.controller;

import com.focusnode.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class TaskRowController {
    @FXML
    private VBox checkboxBox;

    @FXML
    private Label checkboxLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label priorityLabel;

    @FXML
    private Label dueDateLabel;

    @FXML
    private Label focusTimeLabel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public void bind(Task task) {
        if (task == null) {
            return;
        }

        if (nameLabel != null) {
            nameLabel.textProperty().bind(task.titleProperty());
        }
        if (categoryLabel != null) {
            categoryLabel.textProperty().bind(task.categoryProperty());
        }
        if (statusLabel != null) {
            statusLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
                () -> task.getStatus().getLabel(),
                task.statusProperty()
            ));
            task.statusProperty().addListener((obs, oldStatus, newStatus) -> updateStatusStyle(newStatus));
            updateStatusStyle(task.getStatus());
        }
        if (priorityLabel != null) {
            priorityLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
                () -> task.getPriority().getLabel(),
                task.priorityProperty()
            ));
            task.priorityProperty().addListener((obs, oldPrio, newPrio) -> updatePriorityStyle(newPrio));
            updatePriorityStyle(task.getPriority());
        }
        if (dueDateLabel != null) {
            dueDateLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
                () -> task.getDueDate().format(DATE_FORMATTER),
                task.dueDateProperty()
            ));
        }
        if (focusTimeLabel != null) {
            focusTimeLabel.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
                () -> "⏱ " + task.getFocusMinutes() + " min",
                task.focusMinutesProperty()
            ));
        }
        if (checkboxBox != null && checkboxLabel != null) {
            task.completedProperty().addListener((obs, oldVal, newVal) -> updateCheckboxStyle(newVal));
            updateCheckboxStyle(task.isCompleted());
        }
    }

    private void updateStatusStyle(Task.Status status) {
        if (statusLabel == null) return;
        switch (status) {
            case COMPLETED:
                statusLabel.getStyleClass().setAll("status-badge-completed");
                break;
            case IN_PROGRESS:
                statusLabel.getStyleClass().setAll("status-badge-inprogress");
                break;
            case PENDING:
                statusLabel.getStyleClass().setAll("status-badge-pending");
                break;
        }
    }

    private void updatePriorityStyle(Task.Priority priority) {
        if (priorityLabel != null) {
            priorityLabel.getStyleClass().setAll("priority-" + priority.name().toLowerCase());
        }
    }

    private void updateCheckboxStyle(boolean isCompleted) {
        if (isCompleted) {
            checkboxBox.getStyleClass().setAll("task-checkbox", "task-checkbox-checked");
            checkboxLabel.setText("✓");
        } else {
            checkboxBox.getStyleClass().setAll("task-checkbox");
            checkboxLabel.setText("");
        }
    }
}
