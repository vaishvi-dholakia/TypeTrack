package com.typetrack.service;

import com.typetrack.model.User;
import com.typetrack.util.DatabaseConnection;
import com.typetrack.util.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

/**
 * Service to manage user registrations, logins, and password resets using MySQL JDBC queries.
 */
public class AuthService {

    // Minimum 8 characters, 1 uppercase, 1 lowercase, 1 number, 1 special character
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public AuthService() {
        // Try establishing connection on startup to initialize schemas
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Database initialized successfully on startup.");
            }
        } catch (SQLException e) {
            System.err.println("Database initialization failure on startup: " + e.getMessage());
        }
    }

    /**
     * Validates a password against strong security rules.
     */
    public boolean isValidPassword(String password) {
        if (password == null) return false;
        return pattern.matcher(password).matches();
    }

    /**
     * Registers a new user inside the MySQL database.
     * @return The registered User object, or null if username is already taken or registration fails.
     */
    public synchronized User register(String name, String username, String password, String securityQuestion, String securityAnswer) {
        if (name == null || username == null || password == null || securityQuestion == null || securityAnswer == null) {
            return null;
        }

        if (!isValidPassword(password)) {
            return null; // Reject weak passwords
        }

        String cleanUsername = username.trim().toLowerCase();
        
        // 1. Check if username exists
        String checkSql = "SELECT id FROM users WHERE LOWER(username) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, cleanUsername);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return null; // Username already taken
                }
            }
        } catch (SQLException e) {
            System.err.println("Database check failure during registration: " + e.getMessage());
            return null;
        }

        // 2. Insert new user and fetch auto-generated primary key
        String insertSql = "INSERT INTO users (username, password_hash, name, role, security_question, security_answer_hash) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            
            String hash = PasswordHasher.hash(password.trim());
            String answerHash = PasswordHasher.hash(securityAnswer.trim().toLowerCase());
            
            insertStmt.setString(1, cleanUsername);
            insertStmt.setString(2, hash);
            insertStmt.setString(3, name.trim());
            insertStmt.setString(4, "USER"); // Default role
            insertStmt.setString(5, securityQuestion);
            insertStmt.setString(6, answerHash);
            
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return new User(id, cleanUsername, hash, name.trim(), "USER", securityQuestion, answerHash);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database write failure during registration: " + e.getMessage());
        }

        return null;
    }

    /**
     * Authenticates a user login using MySQL records.
     * @return The authenticated User object, or null if credentials are invalid.
     */
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String cleanUsername = username.trim().toLowerCase();
        String hashToVerify = PasswordHasher.hash(password.trim());

        String sql = "SELECT id, username, password_hash, name, role, security_question, security_answer_hash FROM users WHERE LOWER(username) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cleanUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (storedHash.equals(hashToVerify)) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String role = rs.getString("role");
                        String sq = rs.getString("security_question");
                        String sa = rs.getString("security_answer_hash");
                        return new User(id, cleanUsername, storedHash, name, role, sq, sa);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database query failure during login: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves the security question for a given username.
     * @return The security question string, or null if username not found.
     */
    public String getSecurityQuestion(String username) {
        if (username == null) return null;
        String sql = "SELECT security_question FROM users WHERE LOWER(username) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("security_question");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database query failure retrieving security question: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifies the answer to a security question for password recovery.
     */
    public boolean verifySecurityAnswer(String username, String answer) {
        if (username == null || answer == null) return false;
        String sql = "SELECT security_answer_hash FROM users WHERE LOWER(username) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("security_answer_hash");
                    String answerHash = PasswordHasher.hash(answer.trim().toLowerCase());
                    return storedHash != null && storedHash.equals(answerHash);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database query failure verifying security answer: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates the user's password after successful recovery.
     */
    public boolean resetPassword(String username, String newPassword) {
        if (username == null || newPassword == null || !isValidPassword(newPassword)) return false;
        
        String sql = "UPDATE users SET password_hash = ? WHERE LOWER(username) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordHasher.hash(newPassword.trim()));
            stmt.setString(2, username.trim().toLowerCase());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database update failure resetting password: " + e.getMessage());
        }
        return false;
    }
}
