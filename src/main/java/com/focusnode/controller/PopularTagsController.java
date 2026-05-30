package com.focusnode.controller;

import com.focusnode.model.TagUsageItem;
import com.focusnode.service.ServiceLocator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PopularTagsController {

    @FXML private VBox tagsContainer;

    @FXML
    public void initialize() {
        ServiceLocator.getAsyncExecutor().execute(() -> {
            List<TagUsageItem> allTags = ServiceLocator.getAppDataService().getTagUsage();
            
            // Get top 5 tags by usage
            List<TagUsageItem> topTags = allTags.stream()
                .sorted(Comparator.comparing(TagUsageItem::getValue).reversed())
                .limit(5)
                .collect(Collectors.toList());

            if (topTags.isEmpty()) {
                Platform.runLater(() -> {
                    tagsContainer.getChildren().clear();
                    Label emptyLabel = new Label("You haven't used any tags yet.");
                    emptyLabel.setStyle("-fx-text-fill: #94A3B8;");
                    tagsContainer.getChildren().add(emptyLabel);
                });
                return;
            }

            int maxUsage = topTags.get(0).getValue();

            Platform.runLater(() -> {
                tagsContainer.getChildren().clear();
                int rank = 1;
                for (TagUsageItem tag : topTags) {
                    HBox row = new HBox(12);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label rankLabel = new Label(String.valueOf(rank));
                    rankLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6B7280;");

                    Label tagLabel = new Label("#" + tag.getName());
                    tagLabel.getStyleClass().add("popular-tag-badge");
                    String bgColor = tag.getColorHex() + "20"; // 12% opacity background
                    tagLabel.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + tag.getColorHex() + ";");

                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setPrefWidth(130);
                    progressBar.setProgress((double) tag.getValue() / maxUsage);
                    progressBar.setStyle("-fx-accent: " + tag.getColorHex() + ";");

                    Label countLabel = new Label(String.valueOf(tag.getValue()));
                    countLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

                    row.getChildren().addAll(rankLabel, tagLabel, progressBar, countLabel);
                    tagsContainer.getChildren().add(row);
                    rank++;
                }
            });
        });
    }
}
