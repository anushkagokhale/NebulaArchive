package org.example;

import org.apache.tika.Tika;
import java.io.File;
import java.io.IOException;

public class FileReader {

    private final Tika tika = new Tika();

    public String extractText(String filePath) throws IOException, Exception {        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        String text = tika.parseToString(file);
        return text;
    }

    public static void main(String[] args) throws Exception {
        FileReader reader = new FileReader();
        NLPClassifier brain = new NLPClassifier(); // 1. Wake up the brain

        String testFilePath = "test.pdf";
        System.out.println("Reading file: " + testFilePath);

        // 2. Extract the text like before
        String extractedText = reader.extractText(testFilePath);

        // 3. The "Intelligence" step: Classify the text
        String category = brain.classify(extractedText);

        System.out.println("Extracted Content: " + extractedText.trim());
        System.out.println("AI Predicted Folder: " + category);
        System.out.println("Done");
    }
    }
