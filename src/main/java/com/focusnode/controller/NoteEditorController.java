package com.focusnode.controller;

import com.focusnode.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.stage.FileChooser;
import javafx.scene.web.WebView;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import com.focusnode.model.FileResource;

import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NoteEditorController {

    @FXML private TextField titleField;
    @FXML private TextField subjectField;
    @FXML private TextArea contentArea;
    @FXML private WebView markdownPreview;
    @FXML private TextField tagsField;
    @FXML private Button deleteButton;
    @FXML private Button attachButton;
    @FXML private ListView<FileResource> filesListView;

    private Note currentNote;
    private Runnable onSaveCallback;
    private Runnable onCancelCallback;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @FXML
    public void initialize() {
        contentArea.textProperty().addListener((obs, oldVal, newVal) -> renderMarkdown());
        
        filesListView.setCellFactory(param -> new ListCell<FileResource>() {
            @Override
            protected void updateItem(FileResource item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("📎 " + item.getFileName() + " (" + (item.getSizeBytes() / 1024) + " KB)");
                }
            }
        });
        
        filesListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                FileResource selected = filesListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        Desktop.getDesktop().open(new File(selected.getFilePath()));
                    } catch (IOException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Cannot open file: " + ex.getMessage());
                        alert.show();
                    }
                }
            }
        });
    }

    private void renderMarkdown() {
        String markdown = contentArea.getText();
        if (markdown == null) markdown = "";
        
        String htmlBody = renderer.render(parser.parse(markdown));
        
        // Add basic CSS styling
        String html = "<!DOCTYPE html><html><head><style>" +
                      "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 10px; color: #333; line-height: 1.6; }" +
                      "h1, h2, h3 { color: #1F2937; margin-top: 1em; margin-bottom: 0.5em; }" +
                      "code { background-color: #F1F5F9; padding: 2px 4px; border-radius: 4px; font-family: Consolas, monospace; }" +
                      "pre { background-color: #F8FAFC; padding: 10px; border-radius: 5px; overflow-x: auto; }" +
                      "blockquote { border-left: 4px solid #CBD5E1; margin: 0; padding-left: 10px; color: #64748B; }" +
                      "img { max-width: 100%; }" +
                      "</style></head><body>" +
                      htmlBody +
                      "</body></html>";
                      
        markdownPreview.getEngine().loadContent(html);
    }

    public void setNote(Note note, Runnable onSave, Runnable onCancel) {
        this.currentNote = note;
        this.onSaveCallback = onSave;
        this.onCancelCallback = onCancel;
        if (note != null) {
            deleteButton.setVisible(true);
            deleteButton.setManaged(true);
            titleField.setText(note.getTitle());
            subjectField.setText(note.getSubjectName());
            contentArea.setText(note.getContent() != null ? note.getContent() : "");
            renderMarkdown();
            tagsField.setText(String.join(", ", note.getTags()));
            filesListView.setItems(note.getAttachedFiles());
        } else {
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
            clearFields();
        }
    }

    @FXML
    private void handleSave() {
        if (titleField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Title cannot be empty!");
            alert.show();
            return;
        }

        if (currentNote == null) {
            // Note creation logic should happen in parent or service.
            // Using dummy userId 1 for now (will be handled by repository later).
            currentNote = new Note(
                    1, // dummy userId
                    titleField.getText(),
                    contentArea.getText(),
                    null, // subjectId will be resolved in repository based on subjectName
                    null // folderId
            );
            currentNote.getTags().setAll(Arrays.stream(tagsField.getText().split(",")).map(String::trim).collect(Collectors.toList()));
            currentNote.setSubjectName(subjectField.getText());
        } else {
            currentNote.setTitle(titleField.getText());
            currentNote.setContent(contentArea.getText());
            currentNote.getTags().setAll(Arrays.stream(tagsField.getText().split(",")).map(String::trim).collect(Collectors.toList()));
            currentNote.setUpdatedAt(LocalDateTime.now());
            currentNote.setSubjectName(subjectField.getText());
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }
    
    @FXML
    private void handleAttachFile() {
        if (currentNote == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please save the note once before attaching files.");
            alert.show();
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach File");
        File file = fileChooser.showOpenDialog(contentArea.getScene().getWindow());
        if (file != null) {
            FileResource newResource = new FileResource(
                    currentNote.getUserId(),
                    null, // folderId
                    file.getName(),
                    file.getAbsolutePath(),
                    1, // file type (1=general)
                    file.length()
            );
            currentNote.getAttachedFiles().add(newResource);
        }
    }

    @FXML
    private void handleDelete() {
        if (currentNote != null) {
            com.focusnode.service.ServiceLocator.getAppDataService().deleteNote(currentNote);
            if (onSaveCallback != null) {
                onSaveCallback.run(); // Refresh and close
            }
        }
    }

    @FXML
    private void handleCancel() {
        if (onCancelCallback != null) {
            onCancelCallback.run();
        }
    }

    private void clearFields() {
        titleField.clear();
        subjectField.clear();
        contentArea.clear();
        tagsField.clear();
    }

    public Note getCurrentNote() {
        return currentNote;
    }
}
