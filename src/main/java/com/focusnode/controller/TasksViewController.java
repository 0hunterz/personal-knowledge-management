package com.focusnode.controller;

import com.focusnode.model.Task;
import com.focusnode.service.AppDataService;
import com.focusnode.service.ServiceLocator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TasksViewController {

    @FXML
    private TextField searchField;

    @FXML
    private Label taskSummaryLabel;

    @FXML private VBox taskListContainer;
    @FXML private ScrollPane mainScrollPane;
    @FXML private StackPane editorContainer;
    @FXML private VBox rightSidebar;

    private TaskEditorController taskEditorController;
    private Node taskEditorNode;

    private final AppDataService dataService = ServiceLocator.getAppDataService();

    @FXML
    public void initialize() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTaskList(newValue));
        }

        loadEditor();
        loadTasksAsync("");
    }

    private void loadEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/TaskEditor.fxml"));
            taskEditorNode = loader.load();
            taskEditorController = loader.getController();
            if (editorContainer != null) {
                editorContainer.getChildren().add(taskEditorNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void createNewTask() {
        openEditor(null);
    }

    private void openEditor(Task task) {
        if (taskEditorController != null && mainScrollPane != null) {
            taskEditorController.setTask(task, () -> {
                closeEditor();
                loadTasksAsync(searchField != null ? searchField.getText() : "");
            }, this::closeEditor);
            editorContainer.setVisible(true);
        }
    }

    private void closeEditor() {
        if (editorContainer != null) {
            editorContainer.setVisible(false);
        }
    }

    private List<Task> allTasks = new java.util.ArrayList<>();

    private void loadTasksAsync(String query) {
        com.focusnode.util.AsyncExecutor.execute(() -> {
            List<Task> tasks = dataService.getTasks();
            javafx.application.Platform.runLater(() -> {
                this.allTasks = tasks;
                filterTaskList(query);
            });
        });
    }

    private void filterTaskList(String query) {
        if (taskListContainer == null) {
            return;
        }

        List<Task> filtered = allTasks.stream()
                .filter(task -> matchesQuery(task, query))
                .sorted((t1, t2) -> {
                    // Sort by Priority (High > Medium > Low) -> Deadline (nearest first) -> Estimated time (longest first)
                    int pCmp = t1.getPriority().compareTo(t2.getPriority());
                    if (pCmp != 0) return pCmp;
                    int dCmp = t1.getDueDate().compareTo(t2.getDueDate());
                    if (dCmp != 0) return dCmp;
                    return Integer.compare(t2.getFocusMinutes(), t1.getFocusMinutes());
                })
                .collect(Collectors.toList());

        taskListContainer.getChildren().clear();
        for (Task task : filtered) {
            Node row = loadTaskRow(task);
            if (row != null) {
                taskListContainer.getChildren().add(row);
            }
        }

        if (taskSummaryLabel != null) {
            taskSummaryLabel.setText(String.format(Locale.ENGLISH, "%d tasks shown • %d total", filtered.size(), allTasks.size()));
        }
    }

    private boolean matchesQuery(Task task, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String lowerQuery = query.toLowerCase(Locale.ENGLISH);
        return task.getTitle().toLowerCase(Locale.ENGLISH).contains(lowerQuery)
                || task.getCategory().toLowerCase(Locale.ENGLISH).contains(lowerQuery)
                || task.getStatus().getLabel().toLowerCase(Locale.ENGLISH).contains(lowerQuery);
    }

    private Node loadTaskRow(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/TaskRowItem.fxml"));
            Node row = loader.load();
            TaskRowController controller = loader.getController();
            if (controller != null) {
                controller.bind(task);
            }
            if (row != null) {
                row.setOnMouseClicked(e -> openEditor(task));
            }
            return row;
        } catch (IOException e) {
            System.err.println("Unable to load task row: " + e.getMessage());
            return null;
        }
    }
}
