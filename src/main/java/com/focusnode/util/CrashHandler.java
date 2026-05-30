package com.focusnode.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class CrashHandler {
    private static final String LOG_FILE = "focus-node.log";

    public static void handle(Thread thread, Throwable throwable) {
        logToFile(thread, throwable);
        showCrashDialog(thread, throwable);
    }

    private static void logToFile(Thread thread, Throwable throwable) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("=== Crash Report: " + LocalDateTime.now() + " ===");
            writer.println("Thread: " + thread.getName());
            throwable.printStackTrace(writer);
            writer.println("=========================================\n");
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showCrashDialog(Thread thread, Throwable throwable) {
        // Mọi tương tác UI phải được gọi trên luồng JavaFX
        if (Platform.isFxApplicationThread()) {
            displayDialog(thread, throwable);
        } else {
            Platform.runLater(() -> displayDialog(thread, throwable));
        }
    }

    private static void displayDialog(Thread thread, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("Oops! Something went wrong.");
        alert.setContentText("A critical error occurred and was safely caught.\nDetails have been logged to focus-node.log.");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Exception Stacktrace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
