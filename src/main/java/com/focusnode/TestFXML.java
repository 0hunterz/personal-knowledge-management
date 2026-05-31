package com.focusnode;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
public class TestFXML {
    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(TestFXML.class.getResource("/fxml/views/KnowledgeView.fxml"));
                loader.load();
                System.out.println("SUCCESS");
                System.exit(0);
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
