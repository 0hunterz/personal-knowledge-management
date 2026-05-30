package com.focusnode.controller;

import com.focusnode.model.ReviewItem;
import com.focusnode.service.ServiceLocator;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewViewController {

    @FXML private Label progressLabel;
    @FXML private VBox cardPane;
    @FXML private Label questionLabel;
    @FXML private Label answerLabel;
    @FXML private Button showAnswerBtn;
    @FXML private HBox ratingBox;
    @FXML private VBox completePane;

    private List<ReviewItem> dueItems;
    private int currentIndex = 0;

    @FXML
    public void initialize() {
        loadDueItems();
        if (dueItems.isEmpty()) {
            showComplete();
        } else {
            showCard(currentIndex);
        }
    }

    private void loadDueItems() {
        LocalDate today = LocalDate.now();
        List<ReviewItem> allItems = ServiceLocator.getAppDataService().getReviewItems();
        dueItems = allItems.stream()
                .filter(item -> !item.getNextReviewDate().isAfter(today))
                .collect(Collectors.toList());
    }

    private void showCard(int index) {
        if (index >= dueItems.size()) {
            showComplete();
            return;
        }
        
        ReviewItem item = dueItems.get(index);
        progressLabel.setText("Reviewing: " + (index + 1) + " / " + dueItems.size());
        
        questionLabel.setText(item.getQuestion());
        answerLabel.setText(item.getAnswer());
        
        answerLabel.setVisible(false);
        ratingBox.setVisible(false);
        showAnswerBtn.setVisible(true);

        // Simple enter animation
        cardPane.setOpacity(0);
        cardPane.setTranslateY(20);
        
        FadeTransition ft = new FadeTransition(Duration.millis(300), cardPane);
        ft.setToValue(1.0);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), cardPane);
        tt.setToY(0);
        
        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    @FXML
    private void onShowAnswer() {
        showAnswerBtn.setVisible(false);
        answerLabel.setVisible(true);
        ratingBox.setVisible(true);
        
        // Fade in answer
        answerLabel.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), answerLabel);
        ft.setToValue(1.0);
        ft.play();
    }

    @FXML
    private void onHard() { processRating("Hard"); }

    @FXML
    private void onGood() { processRating("Good"); }

    @FXML
    private void onEasy() { processRating("Easy"); }

    private void processRating(String rating) {
        ReviewItem item = dueItems.get(currentIndex);
        
        int repetitions = item.getRepetitions();
        double easeFactor = item.getEaseFactor();
        int interval = item.getInterval();
        
        // SuperMemo-2 logic
        if (rating.equals("Hard")) {
            repetitions = 0;
            interval = 0;
            easeFactor = Math.max(1.3, easeFactor - 0.2);
            item.setDifficulty("Hard");
        } else {
            if (rating.equals("Easy")) {
                easeFactor += 0.15;
            }
            repetitions++;
            if (repetitions == 1) {
                interval = 1;
            } else if (repetitions == 2) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * easeFactor);
            }
            item.setDifficulty(rating.equals("Easy") ? "Easy" : "Good");
        }
        
        item.setRepetitions(repetitions);
        item.setEaseFactor(easeFactor);
        item.setInterval(interval);
        
        // Update next review date based on the interval
        // If hard, review tomorrow (interval 0 + 1 for safety or just 1 day)
        int actualInterval = interval == 0 ? 1 : interval;
        item.setNextReviewDate(LocalDate.now().plusDays(actualInterval));
        
        // Save to DB asynchronously
        ServiceLocator.getAsyncExecutor().submit(() -> {
            ServiceLocator.getAppDataService().saveReviewItem(item);
        });
        
        // Move to next card
        currentIndex++;
        showCard(currentIndex);
    }

    private void showComplete() {
        cardPane.setVisible(false);
        progressLabel.setText("All Done!");
        completePane.setVisible(true);
    }
}
