package org.example;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:nebula_archive.db";

    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS file_archive (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "file_name TEXT NOT NULL," +
                "category TEXT," +
                "upload_date DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database Status: Online (nebula_archive.db)");
        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
        }
    }

    public void saveFileRecord(String fileName, String category) {
        String sql = "INSERT INTO file_archive(file_name, category) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileName);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
            System.out.println("Memory Logged: " + fileName + " -> [" + category + "]");
        } catch (SQLException e) {
            System.out.println("Logging Error: " + e.getMessage());
        }
    }

    public void searchByCategory(String category) {
        // We use LOWER and LIKE to be extra safe with the search!
        String sql = "SELECT file_name, upload_date FROM file_archive WHERE LOWER(category) LIKE LOWER(?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // We add % so it finds the word even if there's a hidden space!
            pstmt.setString(1, "%" + category.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nSearch Results for: " + category);
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(rs.getString("file_name") + " | Date: " + rs.getString("upload_date"));
            }

            if (!found) {
                System.out.println("Still no luck! Try running Option 5 to see the exact categories available.");
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
    public void showAllFiles() {
        String sql = "SELECT * FROM file_archive";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nALL LOGGED FILES");
            while (rs.next()) {
                System.out.println("File: " + rs.getString("file_name") + " | Category: " + rs.getString("category"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}