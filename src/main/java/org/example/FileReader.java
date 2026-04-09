package org.example;

import org.apache.tika.Tika;
import java.io.File;

public class FileReader {
    private final Tika tika = new Tika();
    private final NLPClassifier brain = new NLPClassifier();
    private final DatabaseManager db = new DatabaseManager();

    public void startSystem() {
        // Initialize the memory
        db.initializeDatabase();
        System.out.println("--- NebulaArchive System Online ---");
    }

    public void processFile(String filePath) {
        System.out.println("\nProcessing: " + filePath);
        try {
            // 1. Extraction
            String text = tika.parseToString(new File(filePath));

            // 2. Classification
            String category = brain.classify(text);

            // 3. Database Logging
            db.saveFileRecord(filePath, category);

            // 4. Report
            System.out.println("Result: [" + category + "]");

        } catch (Exception e) {
            System.err.println("Failed to process " + filePath + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        FileReader app = new FileReader();

        // Setup
        app.startSystem();

        // Execution
        app.processFile("test.pdf");

        // Later, you could do:
        // app.processFile("invoice.pdf");
        // app.processFile("resume.docx");
    }
}