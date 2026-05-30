package com.focusnode.controller;

import com.focusnode.navigation.AppNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TopBarController {
    
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
    }
}
