package org.example;

import org.apache.tika.Tika;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileReader {
    private final Tika tika = new Tika();
    private final NLPClassifier brain = new NLPClassifier();
    public final DatabaseManager db = new DatabaseManager();
    private final MinioManager vault = new MinioManager();

    public void startSystem() {
        db.initializeDatabase();
        System.out.println("NebulaArchive Intelligence System Online");
    }

    public void processDirectory(String folderName) {
        File folder = new File(folderName);
        File[] fileList = folder.listFiles();

        if (fileList == null || fileList.length == 0) {
            System.out.println("The Drop Zone is empty!");
            return;
        }

        for (File file : fileList) {
            if (file.isFile()) {
                processFile(file.getPath());
            }
        }
    }

    public void processFile(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();

        // Use try-with-resources to FORCE the file to close immediately after reading
        try (InputStream stream = new FileInputStream(file)) {
            String text = tika.parseToString(stream);
            String category = brain.classify(text);

            db.saveFileRecord(fileName, category);
            vault.uploadFile(category, filePath, fileName);

            System.out.println("Sorted: " + fileName + " -> [" + category + "]");

        } catch (Exception e) {
            System.err.println("Critical Error during processing: " + e.getMessage());
            return; // Stop here if we can't even read the file
        }

        // Now that the 'try' block is over, the stream is officially CLOSED.
        // We can safely move the file.
        try {
            Path source = Paths.get(filePath);
            Path destDir = Paths.get("processed_vault");
            if (!Files.exists(destDir)) Files.createDirectories(destDir);

            Path target = destDir.resolve(fileName);

            // Move the file, overwriting if it already exists in the vault
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Successfully moved to processed_vault.");

        } catch (Exception e) {
            System.err.println("Windows Lock persist: " + fileName + ". Manual move required.");
        }
    }

    public static void main(String[] args) {
        FileReader app = new FileReader();
        app.startSystem();
        app.processDirectory("to_be_sorted");
    }
}