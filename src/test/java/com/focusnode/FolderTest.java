package com.focusnode;

import com.focusnode.model.Folder;
import com.focusnode.repository.FolderRepository;

public class FolderTest {
    public static void main(String[] args) {
        System.out.println("Testing Folder Creation...");
        FolderRepository repo = new FolderRepository();
        Folder folder = new Folder(-1, 1, "Test Folder", null, java.time.LocalDateTime.now());
        boolean success = repo.createFolder(folder);
        if (success) {
            System.out.println("Folder created successfully! ID: " + folder.getFolderId());
        } else {
            System.out.println("Failed to create folder. See exceptions above.");
        }
    }
}
