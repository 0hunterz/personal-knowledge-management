package com.focusnode.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class VaultManager {
    // Tương lai có thể đưa đường dẫn này vào cấu hình Settings (Cơ sở dữ liệu hoặc properties)
    private static final String DEFAULT_VAULT_DIR = System.getProperty("user.home") + File.separator + ".focusnode" + File.separator + "vault";

    public static Path getVaultPath() {
        Path vaultPath = Paths.get(DEFAULT_VAULT_DIR);
        if (!Files.exists(vaultPath)) {
            try {
                Files.createDirectories(vaultPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return vaultPath;
    }

    /**
     * Copy file vào kho Vault. Nếu trùng tên sẽ tự thêm (1), (2)...
     */
    public static Path importFile(File originalFile) throws IOException {
        Path vaultPath = getVaultPath();
        String originalName = originalFile.getName();
        String baseName = originalName;
        String extension = "";

        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        Path targetPath = vaultPath.resolve(originalName);
        int counter = 1;

        // Xử lý trùng tên
        while (Files.exists(targetPath)) {
            targetPath = vaultPath.resolve(baseName + " (" + counter + ")" + extension);
            counter++;
        }

        Files.copy(originalFile.toPath(), targetPath, StandardCopyOption.COPY_ATTRIBUTES);
        return targetPath;
    }
}
