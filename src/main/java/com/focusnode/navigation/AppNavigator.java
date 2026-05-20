package com.focusnode.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public final class AppNavigator {
    private static StackPane contentArea;

    private AppNavigator() {
    }

    public static void setContentArea(StackPane contentArea) {
        AppNavigator.contentArea = contentArea;
    }

    public static void navigateTo(AppView view) {
        if (contentArea == null) {
            throw new IllegalStateException("Content area has not been initialized.");
        }

        try {
            Node screen = FXMLLoader.load(AppNavigator.class.getResource(view.getFxmlPath()));
            contentArea.getChildren().setAll(screen);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load view: " + view.getFxmlPath(), e);
        }
    }
}
