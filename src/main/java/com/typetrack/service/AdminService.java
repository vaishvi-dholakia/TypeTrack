package com.typetrack.service;

import com.typetrack.model.Paragraph;
import com.typetrack.model.User;
import com.typetrack.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to manage administrative operations such as user management,
 * paragraph management, and system-wide statistics aggregation.
 */
public class AdminService {

    /**
     * Aggregates system-wide statistics for the Admin Dashboard overview.
     */
    public Map<String, String> getSystemStats() {
        Map<String, String> stats = new HashMap<>();
        stats.put("totalUsers", "0");
        stats.put("totalTests", "0");
        stats.put("totalParagraphs", "0");
        stats.put("avgWpm", "0 WPM");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next()) stats.put("totalUsers", String.valueOf(rs.getInt(1)));
            }
            
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM results")) {
                if (rs.next()) stats.put("totalTests", String.valueOf(rs.getInt(1)));
            }
            
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM paragraphs")) {
                if (rs.next()) stats.put("totalParagraphs", String.valueOf(rs.getInt(1)));
            }
            
            try (ResultSet rs = stmt.executeQuery("SELECT AVG(wpm) FROM results")) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    stats.put("avgWpm", String.format("%.1f WPM", avg));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching system stats: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Retrieves all registered users in the system.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, name, role, security_question, security_answer_hash FROM users ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("security_question"),
                    rs.getString("security_answer_hash")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Deletes a user and cascades their results.
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Resets a user's password.
     */
    public boolean forcePasswordReset(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error resetting password via Admin: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all paragraphs.
     */
    public List<Paragraph> getAllParagraphs() {
        List<Paragraph> paragraphs = new ArrayList<>();
        String sql = "SELECT id, content, category, difficulty FROM paragraphs ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                paragraphs.add(new Paragraph(
                    rs.getInt("id"),
                    rs.getString("content"),
                    rs.getString("category"),
                    rs.getString("difficulty")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all paragraphs: " + e.getMessage());
        }
        return paragraphs;
    }

    /**
     * Adds a new paragraph.
     */
    public boolean addParagraph(String content, String category, String difficulty) {
        String sql = "INSERT INTO paragraphs (content, category, difficulty) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, content.trim());
            stmt.setString(2, category.trim());
            stmt.setString(3, difficulty.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding paragraph: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a paragraph.
     */
    public boolean deleteParagraph(int id) {
        String sql = "DELETE FROM paragraphs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting paragraph: " + e.getMessage());
        }
        return false;
    }
}
