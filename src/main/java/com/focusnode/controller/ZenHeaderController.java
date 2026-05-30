package com.focusnode.controller;

import com.focusnode.navigation.AppNavigator;
import com.focusnode.navigation.AppView;
import javafx.fxml.FXML;

public class ZenHeaderController {

    @FXML
    private void exitZenMode() {
        AppNavigator.navigateTo(AppView.HOME);
    }
}
