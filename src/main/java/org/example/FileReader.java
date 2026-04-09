package org.example;

import org.apache.tika.Tika;
import java.io.File;

public class FileReader {
    private final Tika tika = new Tika();
    private final NLPClassifier brain = new NLPClassifier();
    public final DatabaseManager db = new DatabaseManager();
    // 1. ADD THIS: Link to your new Vault Manager
    private final MinioManager vault = new MinioManager();

    public void startSystem() {
        db.initializeDatabase();
        System.out.println("NebulaArchive System Online");
    }

    public void processFile(String filePath) {
        try {
            // A. The Eyes: Extract text
            String text = tika.parseToString(new File(filePath));

            // B. The Brain: Classify via AI logic
            String category = brain.classify(text);

            // C. The Memory: Log to database
            db.saveFileRecord(filePath, category);

            // 2. ADD THIS: The Hands - Upload/Move file to the Vault
            // This uses the AI category to decide which folder it goes into!
            vault.uploadFile(category, filePath, new File(filePath).getName());

            System.out.println("Analysis: " + filePath + " is categorized as " + category);
            System.out.println("Status: Fully processed and secured in vault.");

        } catch (Exception e) {
            System.err.println("Processing Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        FileReader app = new FileReader();
        app.startSystem();

        // Execution
        app.processFile("test.pdf");

        // Search Test: Finding all Education documents
        app.db.getFilesByCategory("Education");
    }
}