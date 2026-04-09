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

        // Test it — drop any PDF or DOCX on your desktop and put the path here
        String testFilePath = "test.pdf";
        System.out.println("Reading file: " + testFilePath);
        String extractedText = reader.extractText(testFilePath);
        System.out.println("Extracted Text");
        System.out.println(extractedText);
        System.out.println("Done");
    }
}