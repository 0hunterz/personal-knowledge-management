package com.focusnode.controller;

import com.focusnode.model.Note;
import com.focusnode.service.ServiceLocator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class KnowledgeViewController {

    @FXML private ScrollPane notesScrollPane;
    @FXML private FlowPane notesFlowPane;
    @FXML private StackPane editorContainer;
    @FXML private VBox rightSidebar;

    private NoteEditorController noteEditorController;
    private Node noteEditorNode;

    @FXML
    public void initialize() {
        loadEditor();
        loadNotes();
    }

    private void loadEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/NoteEditor.fxml"));
            noteEditorNode = loader.load();
            noteEditorController = loader.getController();
            editorContainer.getChildren().add(noteEditorNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNotes() {
        notesFlowPane.getChildren().clear();
        ServiceLocator.getAsyncExecutor().execute(() -> {
            List<Note> notes = ServiceLocator.getAppDataService().getNotes();
            Platform.runLater(() -> {
                for (Note note : notes) {
                    notesFlowPane.getChildren().add(createNoteCard(note));
                }
            });
        });
    }

    private Node createNoteCard(Note note) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        card.setPrefWidth(200);

        Label title = new Label(note.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label preview = new Label(note.getPreview());
        preview.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
        preview.setWrapText(true);

        Label category = new Label(note.getCategory());
        category.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 11px;");

        card.getChildren().addAll(title, category, preview);
        
        card.setOnMouseClicked(e -> openEditor(note));
        
        return card;
    }

    private void openEditor(Note note) {
        noteEditorController.setNote(note, () -> {
            closeEditor();
            loadNotes(); // Refresh
        }, this::closeEditor);
        editorContainer.setVisible(true);
    }

    @FXML
    private void closeEditor() {
        if (editorContainer != null) {
            editorContainer.setVisible(false);
        }
    }

    // Called from FXML when user clicks "+ New Note"
    @FXML
    public void createNewNote() {
        openEditor(null);
    }
}
