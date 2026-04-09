package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:nebula_archive.db";

    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                // Create a table to store file info
                String sql = "CREATE TABLE IF NOT EXISTS files (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "file_name TEXT NOT NULL," +
                        "category TEXT NOT NULL," +
                        "upload_date DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");";
                stmt.execute(sql);
                System.out.println("Database initialized: nebula_archive.db created.");
            }
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // This method now sits outside the initialization method, where it belongs!
    public void saveFileRecord(String fileName, String category) {
        String sql = "INSERT INTO files(file_name, category) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fileName);
            pstmt.setString(2, category);
            pstmt.executeUpdate();

            System.out.println("Record saved to database: " + fileName + " -> " + category);

        } catch (Exception e) {
            System.out.println("Save Error: " + e.getMessage());
        }
    }
}