package org.example;

import org.apache.tika.Tika;
import java.io.File;

public class FileReader {
    private final Tika tika = new Tika();
    private final NLPClassifier brain = new NLPClassifier();
    private final DatabaseManager db = new DatabaseManager();
    private final MinioManager vault = new MinioManager();

    public void startSystem() {
        db.initializeDatabase();
        System.out.println("NebulaArchive Intelligence System Online");
    }

    public void processDirectory(String folderName) {
        File folder = new File(folderName);
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile()) {
                try {
                    String fileName = file.getName();
                    String content = tika.parseToString(file);
                    String category = brain.classify(content);

                    // Fixed: Matches the 2-argument method in MinioManager
                    vault.uploadFile(fileName, file.getAbsolutePath());
                    db.saveFileRecord(fileName, category);

                    file.delete(); // Removes from 'to_be_sorted'
                    System.out.println("Sorted: " + fileName);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}