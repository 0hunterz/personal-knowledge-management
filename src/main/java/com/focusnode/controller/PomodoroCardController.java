package com.focusnode.controller;

import com.focusnode.navigation.AppNavigator;
import com.focusnode.navigation.AppView;
import javafx.fxml.FXML;

public class PomodoroCardController {

    @FXML
    public void initialize() {
        // Initialization if needed
    }

    @FXML
    private void startFocus() {
        AppNavigator.navigateTo(AppView.ZEN_MODE);
    }

    @FXML
    private void startBreak() {
        AppNavigator.navigateTo(AppView.ZEN_MODE);
    }
}
