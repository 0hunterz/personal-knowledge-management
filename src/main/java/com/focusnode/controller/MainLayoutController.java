package com.focusnode.controller;

import com.focusnode.navigation.AppNavigator;
import com.focusnode.navigation.AppView;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainLayoutController {
    @FXML
    private StackPane contentArea;

    @FXML
    private void initialize() {
        AppNavigator.setContentArea(contentArea);
        AppNavigator.navigateTo(AppView.HOME);
    }
}
