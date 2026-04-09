package org.example;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:nebula_archive.db";

    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS files (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "file_name TEXT NOT NULL," +
                        "category TEXT NOT NULL," +
                        "upload_date DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ");";
                stmt.execute(sql);
                System.out.println("Database Status: Online (nebula_archive.db)");
            }
        } catch (Exception e) {
            System.err.println("Database Init Error: " + e.getMessage());
        }
    }

    public void saveFileRecord(String fileName, String category) {
        String sql = "INSERT INTO files(file_name, category) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileName);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
            System.out.println("Memory Logged: " + fileName + " -> [" + category + "]");
        } catch (Exception e) {
            System.err.println("Database Save Error: " + e.getMessage());
        }
    }

    public void getFilesByCategory(String categoryName) {
        String sql = "SELECT file_name, upload_date FROM files WHERE category = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nSearch Results: " + categoryName);
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("📄 " + rs.getString("file_name") + " | Date: " + rs.getString("upload_date"));
            }
            if (!found) System.out.println("No records found for this category.");
        } catch (Exception e) {
            System.err.println("Search Error: " + e.getMessage());
        }
    }
}