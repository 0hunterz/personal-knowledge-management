package com.focusnode.controller;

import com.focusnode.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

public class NoteCardController {
    
    @FXML private Label titleLabel;
    @FXML private Label previewLabel;
    @FXML private Region iconBackground;
    @FXML private ImageView iconImageView;
    @FXML private HBox tagsContainer;
    @FXML private Label updatedAtLabel;
    
    public void setNote(Note note) {
        boolean isEmptyNote = (note.getTitle() == null || note.getTitle().isEmpty()) && (note.getContent() == null || note.getContent().isEmpty());
        
        String displayTitle = "Untitled Note";
        if (note.getTitle() != null && !note.getTitle().isEmpty()) {
            displayTitle = note.getTitle();
        } else if (isEmptyNote && note.getTags() != null && !note.getTags().isEmpty()) {
            displayTitle = note.getTags().get(0) + " Note"; // E.g., "Project Note"
        }
        
        titleLabel.setText(displayTitle);
        
        if (isEmptyNote) {
            previewLabel.setText("No recent notes found. Click to create one.");
            previewLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic;");
        } else {
            previewLabel.setText(note.getContent() != null ? note.getContent() : "");
            previewLabel.setStyle("-fx-text-fill: #6B7280;");
        }

        // Dynamic icon and background color based on title or tags
        String imagePath = "/images/link.png"; // Default
        String bgColor = "#F3E8FF"; // Purple light

        String textToMatch = note.getTitle() != null ? note.getTitle().toLowerCase() : "";
        if (note.getTags() != null && !note.getTags().isEmpty()) {
            textToMatch += " " + note.getTags().get(0).toLowerCase();
        }
        
        if (textToMatch.contains("project") || textToMatch.contains("plan")) {
            imagePath = "/images/work-order.png";
            bgColor = "#E0F2FE"; // Blue light
        } else if (textToMatch.contains("study") || textToMatch.contains("java") || textToMatch.contains("code")) {
            imagePath = "/images/study.png";
            bgColor = "#DCFCE7"; // Green light
        } else if (textToMatch.contains("idea") || textToMatch.contains("tip") || textToMatch.contains("mind")) {
            imagePath = "/images/idea.png";
            bgColor = "#FEF3C7"; // Orange light
        } else if (textToMatch.contains("review") || textToMatch.contains("ai")) {
            imagePath = "/images/growth.png"; // assuming growth.png exists for review
            bgColor = "#F3E8FF"; // Purple light
        }
        
        if (iconBackground != null) {
            iconBackground.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8; -fx-pref-width: 32; -fx-pref-height: 32;");
        }
        
        if (iconImageView != null) {
            try {
                iconImageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
            } catch(Exception e) {}
        }

        // Tags
        if (tagsContainer != null) {
            tagsContainer.getChildren().clear();
            if (note.getTags() != null) {
                for (String tag : note.getTags()) {
                    Label tagLabel = new Label("#" + tag);
                    tagLabel.getStyleClass().add("tag-badge");
                    if (tag.equalsIgnoreCase("project") || tag.equalsIgnoreCase("plan")) {
                        tagLabel.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0284C7;");
                    } else if (tag.equalsIgnoreCase("study") || tag.equalsIgnoreCase("java") || tag.equalsIgnoreCase("code")) {
                        tagLabel.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A;");
                    } else if (tag.equalsIgnoreCase("review") || tag.equalsIgnoreCase("AI")) {
                        tagLabel.setStyle("-fx-background-color: #F3E8FF; -fx-text-fill: #9333EA;");
                    } else if (tag.equalsIgnoreCase("idea")) {
                        tagLabel.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706;");
                    } else {
                        tagLabel.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4F46E5;");
                    }
                    tagsContainer.getChildren().add(tagLabel);
                }
            }
        }

        // Updated at
        if (updatedAtLabel != null) {
            if (isEmptyNote) {
                updatedAtLabel.setText("Not started");
            } else if (note.getUpdatedAt() != null) {
                long days = ChronoUnit.DAYS.between(note.getUpdatedAt(), LocalDate.now());
                if (days == 0) {
                    updatedAtLabel.setText("Updated today");
                } else if (days == 1) {
                    updatedAtLabel.setText("Updated yesterday");
                } else {
                    updatedAtLabel.setText("Updated " + days + " days ago");
                }
            }
        }
    }
}
