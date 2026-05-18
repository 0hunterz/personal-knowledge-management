package com.focusnode;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import atlantafx.base.theme.PrimerLight; 

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_24pt-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_24pt-Medium.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/fonts/Inter_24pt-Bold.ttf"), 14);
        } catch (Exception e) {
            System.out.println("Lưu ý: Không tìm thấy Font, đang dùng font mặc định.");
        }

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/views/MainLayout.fxml"));
        
        Scene scene = new Scene(root, 1100, 750); 
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("Focus-Node UI Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
