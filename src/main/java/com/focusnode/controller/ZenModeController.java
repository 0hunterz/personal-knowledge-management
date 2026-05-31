package com.focusnode.controller;

import com.focusnode.model.FocusSession;
import com.focusnode.model.Task;
import com.focusnode.service.ServiceLocator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

public class ZenModeController {

    @FXML private ComboBox<Task> taskComboBox;
    @FXML private Label taskCategoryLabel;
    @FXML private Label timerLabel;
    @FXML private Label statusLabel;
    @FXML private Circle progressCircle;
    @FXML private Button playPauseButton;

    private static final int POMODORO_MINUTES = 25;
    private static final int TOTAL_SECONDS = POMODORO_MINUTES * 60;
    private int secondsRemaining = TOTAL_SECONDS;
    private boolean isRunning = false;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timerTask;
    private LocalDateTime sessionStartTime;

    @FXML
    public void initialize() {
        scheduler = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
        
        loadTasks();
        updateTimerDisplay();

        taskComboBox.setConverter(new StringConverter<Task>() {
            @Override
            public String toString(Task task) {
                return task == null ? "" : task.getTitle();
            }

            @Override
            public Task fromString(String string) {
                return null;
            }
        });

        taskComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                taskCategoryLabel.setText(newVal.getCategory());
                taskCategoryLabel.setVisible(true);
            } else {
                taskCategoryLabel.setVisible(false);
            }
        });
    }

    private void loadTasks() {
        ServiceLocator.getAsyncExecutor().execute(() -> {
            List<Task> tasks = ServiceLocator.getAppDataService().getTasks().stream()
                    .filter(t -> t.getStatus() != Task.Status.COMPLETED)
                    .toList();
            Platform.runLater(() -> taskComboBox.setItems(FXCollections.observableArrayList(tasks)));
        });
    }

    @FXML
    private void toggleTimer() {
        if (isRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (sessionStartTime == null) {
            sessionStartTime = LocalDateTime.now();
        }
        isRunning = true;
        playPauseButton.setText("||");
        statusLabel.setText("Focusing");
        
        timerTask = scheduler.scheduleAtFixedRate(() -> {
            if (secondsRemaining > 0) {
                secondsRemaining--;
                Platform.runLater(this::updateTimerDisplay);
            } else {
                Platform.runLater(this::handleSessionComplete);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void pauseTimer() {
        isRunning = false;
        playPauseButton.setText("▶");
        statusLabel.setText("Paused");
        if (timerTask != null) {
            timerTask.cancel(false);
        }
    }

    private void updateTimerDisplay() {
        int min = secondsRemaining / 60;
        int sec = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", min, sec));

        double progress = 1.0 - ((double) secondsRemaining / TOTAL_SECONDS);
        double circumference = 2 * Math.PI * 112; // radius 112
        progressCircle.setStrokeDashOffset(circumference * (1.0 - progress));
    }

    private void handleSessionComplete() {
        pauseTimer();
        statusLabel.setText("Session Complete!");
        showSurveyDialog();
    }

    @FXML
    private void endSession() {
        pauseTimer();
        if (secondsRemaining < TOTAL_SECONDS) {
            showSurveyDialog();
        } else {
            resetTimer();
        }
    }

    private void showSurveyDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/SurveyDialog.fxml"));
            Parent root = loader.load();
            SurveyDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Session Survey");
            stage.setScene(new Scene(root));

            controller.initData(stage, result -> {
                saveSessionData(result);
                resetTimer();
            });

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSessionData(SurveyDialogController.SurveyResult result) {
        Task selectedTask = taskComboBox.getValue();
        int minutesSpent = (TOTAL_SECONDS - secondsRemaining) / 60;

        if (selectedTask != null) {
            selectedTask.setActualMinutes(selectedTask.getActualMinutes() + minutesSpent);
            // Save logic
            ServiceLocator.getAppDataService().saveTask(selectedTask);
        }

        FocusSession session = new FocusSession(
                -1,
                1, // Default userId
                selectedTask != null ? selectedTask.getId() : -1,
                -1,
                -1, // PresetId
                sessionStartTime,
                LocalDateTime.now(),
                POMODORO_MINUTES,
                minutesSpent,
                minutesSpent >= POMODORO_MINUTES
        );

        ServiceLocator.getAsyncExecutor().execute(() -> {
            // Save logic
            ServiceLocator.getAppDataService().getFocusSessions().add(session);
            System.out.println("Saved Focus Session: " + session.getActualMinutes() + " mins");
        });
    }

    private void resetTimer() {
        secondsRemaining = TOTAL_SECONDS;
        sessionStartTime = null;
        updateTimerDisplay();
        statusLabel.setText("Ready");
        playPauseButton.setText("▶");
        progressCircle.setStrokeDashOffset(2 * Math.PI * 112);
    }
}
