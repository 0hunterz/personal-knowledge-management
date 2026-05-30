package com.focusnode.controller;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SurveyDialogController {

    @FXML private ToggleGroup focusGroup;
    @FXML private RadioButton rate1, rate2, rate3, rate4, rate5;
    @FXML private TextArea distractionsArea;

    private Consumer<SurveyResult> onComplete;
    private Stage stage;

    public static class SurveyResult {
        public int focusScore;
        public String distractions;

        public SurveyResult(int score, String dist) {
            this.focusScore = score;
            this.distractions = dist;
        }
    }

    public void initData(Stage stage, Consumer<SurveyResult> onComplete) {
        this.stage = stage;
        this.onComplete = onComplete;
    }

    @FXML
    private void handleSave() {
        int score = 3;
        if (rate1.isSelected()) score = 1;
        else if (rate2.isSelected()) score = 2;
        else if (rate3.isSelected()) score = 3;
        else if (rate4.isSelected()) score = 4;
        else if (rate5.isSelected()) score = 5;

        onComplete.accept(new SurveyResult(score, distractionsArea.getText()));
        stage.close();
    }

    @FXML
    private void handleSkip() {
        onComplete.accept(new SurveyResult(0, ""));
        stage.close();
    }
}
