package com.focusnode.controller;

import com.focusnode.service.AppDataService;
import com.focusnode.service.ServiceLocator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HeaderController {
    @FXML
    private Label greetingLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label dateLabel;
    
    @FXML
    private Label dayLabel;

    @FXML
    public void initialize() {
        // Cập nhật ngày tháng hiện tại
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
        
        if (dateLabel != null) {
            dateLabel.setText(today.format(dateFormatter));
        }
        if (dayLabel != null) {
            dayLabel.setText(today.format(dayFormatter));
        }

        com.focusnode.util.AsyncExecutor.execute(() -> {
            AppDataService dataService = ServiceLocator.getAppDataService();
            String greeting = dataService.getGreetingMessage();
            javafx.application.Platform.runLater(() -> {
                if (greetingLabel != null) {
                    greetingLabel.setText(greeting);
                }
                if (subtitleLabel != null) {
                    subtitleLabel.setText("Small steps every day lead to big changes.");
                }
            });
        });
    }
}
