package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;

public class MinioManager {
    // This points to the folder you created on your C: drive
    private final String mockVaultPath = "C:/nebula_vault/";

    public void uploadFile(String bucketName, String filePath, String fileName) {
        try {
            // 1. Create the sub-folder based on the AI's category (e.g., /education)
            Path bucketPath = Paths.get(mockVaultPath + bucketName.toLowerCase());
            if (!Files.exists(bucketPath)) {
                Files.createDirectories(bucketPath);
                System.out.println("Vault Directory Created: " + bucketName);
            }

            // 2. Copy the file into that folder
            Path source = Paths.get(filePath);
            Path target = bucketPath.resolve(fileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("SUCCESS: File moved to Vault -> " + bucketName + " folder.");

        } catch (Exception e) {
            System.err.println("Vault Error: " + e.getMessage());
        }
    }
}