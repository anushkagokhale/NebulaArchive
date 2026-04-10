package org.example;

import org.apache.tika.Tika;
import java.io.File;

public class FileReader {
    private final Tika tika = new Tika();
    private final NebulaChatbot ai = new NebulaChatbot();
    private final DatabaseManager db = new DatabaseManager();
    private final MinioManager vault = new MinioManager();

    public void startSystem() {
        db.initializeDatabase();
    }

    public void processDirectory(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) folder.mkdirs();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    processSingleFile(file);
                    file.delete();
                }
            }
        }
    }

    public void processSingleFile(File file) {
        try {
            String content = tika.parseToString(file);
            String prompt = "Classify this file into ONE word (e.g. Invoice, Resume, Personal). Content: " + content;
            String rawCategory = ai.askNebula(prompt);

            String category = "general";
            if (rawCategory != null && !rawCategory.isEmpty()) {
                category = rawCategory.trim().split("\\s+")[0].replaceAll("[^a-zA-Z]", "");
            }

            vault.uploadToCategory(file.getName(), file.getAbsolutePath(), category);
            db.saveFileRecord(file.getName(), category);
        } catch (Exception e) {
            System.err.println("Nebula failed to sort: " + file.getName());
        }
    }
}