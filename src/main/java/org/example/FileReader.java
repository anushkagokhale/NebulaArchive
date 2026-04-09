package org.example;

import org.apache.tika.Tika;
import java.io.File;

public class FileReader {
    private final Tika tika = new Tika();
    private final NLPClassifier brain = new NLPClassifier();
    public final DatabaseManager db = new DatabaseManager();

    public void startSystem() {
        db.initializeDatabase();
        System.out.println("NebulaArchive System Online");
    }

    public void processFile(String filePath) {
        try {
            String text = tika.parseToString(new File(filePath));
            String category = brain.classify(text);

            // Log to database
            db.saveFileRecord(filePath, category);

            System.out.println("Analysis: " + filePath + " is categorized as " + category);
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