package org.example;

import org.apache.tika.Tika;
import java.io.File;
import java.io.IOException;

public class FileReader {

    private final Tika tika = new Tika();

    /*Phase 1: The "Eyes" - Extracts text from the PDF.*/

    public String extractText(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return tika.parseToString(file);
    }

    public static void main(String[] args) throws Exception {
        // 1. Initialize the Database Manager (The Memory)
        DatabaseManager db = new DatabaseManager();
        db.initializeDatabase();

        // 2. Initialize the Core Components
        FileReader reader = new FileReader();
        NLPClassifier brain = new NLPClassifier();

        // Target File
        String testFilePath = "test.pdf";
        System.out.println("Processing: " + testFilePath);

        try {
            // STEP A: Extract text using Apache Tika
            String extractedText = reader.extractText(testFilePath);

            // STEP B: Run the text through our NLP Brain
            String category = brain.classify(extractedText);

            // STEP C: Display the results
            System.out.println("\nNebulaArchive Analysis ");
            System.out.println("Extracted Content: " + extractedText.trim());
            System.out.println("AI Predicted Folder: " + category);

            // STEP D: Save this interaction to SQLite
            db.saveFileRecord(testFilePath, category);

            System.out.println("Phase 2 & Database Logic: SUCCESS");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}