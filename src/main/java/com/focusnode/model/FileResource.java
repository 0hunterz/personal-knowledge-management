package com.focusnode.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class FileResource implements KnowledgeNode {
    private final IntegerProperty fileId;
    private final IntegerProperty userId;
    private final ObjectProperty<Integer> folderId;
    private final StringProperty fileName;
    private final StringProperty filePath;
    private final IntegerProperty fileTypeId;
    private final LongProperty sizeBytes;
    private final ObjectProperty<LocalDateTime> uploadedAt;
    private final BooleanProperty isDeleted;

    public FileResource(int fileId, int userId, Integer folderId, String fileName, String filePath, int fileTypeId, long sizeBytes, LocalDateTime uploadedAt, boolean isDeleted) {
        this.fileId = new SimpleIntegerProperty(fileId);
        this.userId = new SimpleIntegerProperty(userId);
        this.folderId = new SimpleObjectProperty<>(folderId);
        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.fileTypeId = new SimpleIntegerProperty(fileTypeId);
        this.sizeBytes = new SimpleLongProperty(sizeBytes);
        this.uploadedAt = new SimpleObjectProperty<>(uploadedAt);
        this.isDeleted = new SimpleBooleanProperty(isDeleted);
    }

    public FileResource(int userId, Integer folderId, String fileName, String filePath, int fileTypeId, long sizeBytes) {
        this(-1, userId, folderId, fileName, filePath, fileTypeId, sizeBytes, LocalDateTime.now(), false);
    }

    public int getFileId() { return fileId.get(); }
    public void setFileId(int value) { fileId.set(value); }
    public IntegerProperty fileIdProperty() { return fileId; }

    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public IntegerProperty userIdProperty() { return userId; }

    public Integer getFolderId() { return folderId.get(); }
    public void setFolderId(Integer value) { folderId.set(value); }
    public ObjectProperty<Integer> folderIdProperty() { return folderId; }

    public String getFileName() { return fileName.get(); }
    public void setFileName(String value) { fileName.set(value); }
    public StringProperty fileNameProperty() { return fileName; }

    public String getFilePath() { return filePath.get(); }
    public void setFilePath(String value) { filePath.set(value); }
    public StringProperty filePathProperty() { return filePath; }

    public int getFileTypeId() { return fileTypeId.get(); }
    public void setFileTypeId(int value) { fileTypeId.set(value); }
    public IntegerProperty fileTypeIdProperty() { return fileTypeId; }

    public long getSizeBytes() { return sizeBytes.get(); }
    public void setSizeBytes(long value) { sizeBytes.set(value); }
    public LongProperty sizeBytesProperty() { return sizeBytes; }

    public LocalDateTime getUploadedAt() { return uploadedAt.get(); }
    public void setUploadedAt(LocalDateTime value) { uploadedAt.set(value); }
    public ObjectProperty<LocalDateTime> uploadedAtProperty() { return uploadedAt; }

    public boolean isDeleted() { return isDeleted.get(); }
    public void setDeleted(boolean value) { isDeleted.set(value); }
    public BooleanProperty isDeletedProperty() { return isDeleted; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileResource that = (FileResource) o;
        return getFileId() == that.getFileId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileId());
    }

    @Override
    public int getId() { return getFileId(); }

    @Override
    public String getName() { return getFileName(); }

    @Override
    public Integer getParentFolderId() { return getFolderId(); }

    @Override
    public LocalDateTime getUpdatedAt() { return getUploadedAt(); }

    @Override
    public String getType() { return "File"; }
}
