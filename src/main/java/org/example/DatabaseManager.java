package org.example;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:nebula_archive.db";

    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS files (id INTEGER PRIMARY KEY AUTOINCREMENT, fileName TEXT, category TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFileRecord(String fileName, String category) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO files(fileName, category) VALUES(?, ?)")) {
            pstmt.setString(1, fileName);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getVaultSummary() {
        StringBuilder sb = new StringBuilder();
        // Using a direct connection string to ensure we refresh the link
        String url = "jdbc:sqlite:nebula_archive.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT fileName FROM files LIMIT 20")) {

            while (rs.next()) {
                sb.append("- ").append(rs.getString("fileName")).append("\n");
            }
        } catch (Exception e) {
            // This is what Nebula sees if the file is locked or the table is missing
            return "System Note: Vault metadata is currently being synchronized.";
        }

        return sb.length() > 0 ? sb.toString() : "The vault is currently empty. Waiting for sync.";
    }
}