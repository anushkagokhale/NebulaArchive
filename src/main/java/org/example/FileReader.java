package org.example;

import org.apache.tika.Tika;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.Scanner;

public class FileReader {
    private final Tika tika = new Tika();
    private final NLPClassifier brain = new NLPClassifier();
    public final DatabaseManager db = new DatabaseManager();
    private final MinioManager vault = new MinioManager();
    private static final String URL = "jdbc:sqlite:nebula_archive.db";
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
            if (file.isFile()) processFile(file.getPath());
        }
    }

    public void processFile(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        try (InputStream stream = new FileInputStream(file)) {
            String text = tika.parseToString(stream);
            String category = brain.classify(text);
            db.saveFileRecord(fileName, category);
            vault.uploadFile(category, filePath, fileName);
            System.out.println("Sorted: " + fileName + " -> [" + category + "]");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        try {
            System.gc(); Thread.sleep(500);
            Path destDir = Paths.get("processed_vault");
            if (!Files.exists(destDir)) Files.createDirectories(destDir);
            Files.move(Paths.get(filePath), destDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved to processed_vault.");
        } catch (Exception ignored) {}
    }
    public void showAllFiles() {
        String sql = "SELECT * FROM file_archive";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nFULL ARCHIVE LOG");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("ID: " + rs.getInt("id") +
                        " | File: " + rs.getString("file_name") +
                        " | Category: " + rs.getString("category") +
                        " | Date: " + rs.getString("upload_date"));
            }
            if (!hasData) System.out.println("The archive is currently empty. Run Option 1 first!");

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        FileReader app = new FileReader();
        app.startSystem();
        Scanner scanner = new Scanner(System.in);
        GeminiChatbot chatbot = new GeminiChatbot();

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Process 'to_be_sorted' folder");
            System.out.println("2. Search Archive by Category");
            System.out.println("3. Ask Nebula AI (Chatbot)");
            System.out.println("4. Exit");
            System.out.println("5. DEBUG: Show All Database Logs"); // <--- Add this line!
            System.out.print("Select: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                app.processDirectory("to_be_sorted");
            } else if (choice.equals("2")) {
                System.out.print("Enter category: ");
                app.db.searchByCategory(scanner.nextLine());
            } else if (choice.equals("3")) {
                System.out.print("\n🤖 Ask Nebula AI: ");
                String question = scanner.nextLine();
                System.out.println("Thinking...");
                System.out.println("\n✨ Gemini says: " + chatbot.askGemini(question));
            } else if (choice.equals("4")) {
                System.out.println("System shutting down. Goodbye!");
                break;
            } else if (choice.equals("5")) {
                app.showAllFiles(); // <--- Make sure this matches your method name!
            }
        }
        scanner.close();
    }

}