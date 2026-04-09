package org.example;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:nebula_archive.db";

    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS files (id INTEGER PRIMARY KEY AUTOINCREMENT, fileName TEXT, category TEXT)");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void saveFileRecord(String fileName, String category) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO files(fileName, category) VALUES(?, ?)")) {
            pstmt.setString(1, fileName);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static String getVaultSummary() {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT fileName FROM files LIMIT 30")) {
            while (rs.next()) {
                sb.append("- ").append(rs.getString("fileName")).append("\n");
            }
        } catch (SQLException e) { return "Vault data inaccessible."; }
        return sb.length() > 0 ? sb.toString() : "Vault is currently empty.";
    }
}