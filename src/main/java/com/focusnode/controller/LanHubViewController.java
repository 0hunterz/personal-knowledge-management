package com.focusnode.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class LanHubViewController {

    @FXML
    public void initialize() {
        // Initialization logic for LAN Hub View
    }

    // Since the task says "Cơ chế LAN Sync cơ bản (chỉ export/import dữ liệu)",
    // we'll implement simple Database Export and Import for now.
    
    @FXML
    public void onExportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Database");
        fileChooser.setInitialFileName("focusnode_backup.db");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQLite DB", "*.db"));
        
        // We need a reference to the window, but we don't have direct access here.
        // As a simple workaround, we can get the active window.
        Window window = javafx.stage.Window.getWindows().stream().filter(Window::isFocused).findFirst().orElse(null);
        if (window == null && !javafx.stage.Window.getWindows().isEmpty()) {
            window = javafx.stage.Window.getWindows().get(0);
        }
        
        File file = fileChooser.showSaveDialog(window);
        
        if (file != null) {
            try {
                File dbFile = new File("focusnode.db");
                if (dbFile.exists()) {
                    Files.copy(dbFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    showAlert("Export Successful", "Data exported to " + file.getAbsolutePath());
                } else {
                    showAlert("Export Failed", "Database file not found locally.");
                }
            } catch (Exception e) {
                showAlert("Export Error", e.getMessage());
            }
        }
    }

    @FXML
    public void onImportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Database");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQLite DB", "*.db"));
        
        Window window = javafx.stage.Window.getWindows().stream().filter(Window::isFocused).findFirst().orElse(null);
        if (window == null && !javafx.stage.Window.getWindows().isEmpty()) {
            window = javafx.stage.Window.getWindows().get(0);
        }
        
        File file = fileChooser.showOpenDialog(window);
        
        if (file != null) {
            try {
                File dbFile = new File("focusnode.db");
                Files.copy(file.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert("Import Successful", "Data imported successfully. Please restart the app to see changes.");
            } catch (Exception e) {
                showAlert("Import Error", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
