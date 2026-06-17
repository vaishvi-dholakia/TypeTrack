package com.typetrack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Handles connection configuration loading and connection pooling setup for
 * MySQL.
 * Includes automated database schema initialization.
 */
public class DatabaseConnection {
    private static final String CONFIG_FILE = "data" + File.separator + "database.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        File file = new File(CONFIG_FILE);
        System.out.println("Loading config from: " + file.getAbsolutePath());

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                dbUrl = props.getProperty("db.url");
                dbUser = props.getProperty("db.user");
                dbPassword = props.getProperty("db.password");
            } catch (IOException e) {
                System.err.println("Failed to read database.properties: " + e.getMessage());
            }
        } else {
            System.err.println("database.properties configuration file not found in data directory!");
        }
    }

    /**
     * Establishes and returns a connection to the database.
     */
    public static Connection getConnection() throws SQLException {
        if (dbUrl == null) {
            throw new SQLException("Database connection details not loaded. Check data/database.properties.");
        }

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC Driver not found in classpath. Ensure mysql-connector-java dependency is added.", e);
        }

        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        System.out.println("Database connected successfully.");

        initializeSchema(conn);
        return conn;
    }
    /*
     * public static Connection getConnection() throws SQLException {
     * if (dbUrl == null) {
     * throw new
     * SQLException("Database connection details not loaded. Check data/database.properties."
     * );
     * }
     * 
     * try {
     * // Load MySQL Driver
     * Class.forName("com.mysql.cj.jdbc.Driver");
     * } catch (ClassNotFoundException e) {
     * throw new
     * SQLException("MySQL JDBC Driver not found in classpath. Ensure mysql-connector-java dependency is added."
     * , e);
     * }
     * 
     * Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
     * initializeSchema(conn);
     * return conn;
     * }
     */

    /**
     * Automatically creates schemas for users, paragraphs, and results tables if
     * they don't exist.
     */
    private static void initializeSchema(Connection conn) {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(50) UNIQUE NOT NULL,"
                + "password_hash VARCHAR(64) NOT NULL,"
                + "name VARCHAR(100) NOT NULL"
                + ");";

        String createParagraphsTable = "CREATE TABLE IF NOT EXISTS paragraphs ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "content TEXT NOT NULL,"
                + "category VARCHAR(50) NOT NULL,"
                + "difficulty VARCHAR(50) NOT NULL"
                + ");";

        String createResultsTable = "CREATE TABLE IF NOT EXISTS results ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "user_id INT NOT NULL,"
                + "wpm INT NOT NULL,"
                + "accuracy DECIMAL(5,2) NOT NULL,"
                + "mistakes INT NOT NULL,"
                + "time_taken DECIMAL(6,2) NOT NULL,"
                + "test_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createParagraphsTable);
            stmt.executeUpdate(createResultsTable);

            // Phase 1 Schema Upgrades
            String[] alterStatements = {
                    "ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER'",
                    "ALTER TABLE users ADD COLUMN security_question VARCHAR(255)",
                    "ALTER TABLE users ADD COLUMN security_answer_hash VARCHAR(64)"
            };
            for (String alterSql : alterStatements) {
                try {
                    stmt.executeUpdate(alterSql);
                } catch (SQLException ignored) {
                    // Column already exists
                }
            }

            // Seed default paragraphs if table is empty
            String checkParagraphs = "SELECT COUNT(*) FROM paragraphs";
            try (var rs = stmt.executeQuery(checkParagraphs)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    seedDefaultParagraphs(stmt);
                }
            }

            // Phase 2: Seed default Admin if not exists
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
            try (var rs = stmt.executeQuery(checkAdmin)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    seedDefaultAdmin(stmt);
                }
            } catch (SQLException ignored) {
                // Ignore if role column was just added and not fully flushed
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database tables: " + e.getMessage());
        }
    }

    private static void seedDefaultParagraphs(Statement stmt) throws SQLException {
        String[] seeds = {
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('The quick brown fox jumps over the lazy dog.', 'Standard', 'Easy')",
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('Java is a class-based, object-oriented programming language designed to have as few implementation dependencies as possible.', 'Coding', 'Medium')",
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('Object-oriented programming utilizes classes and objects to structure applications for high reuse and modularity.', 'Coding', 'Easy')",
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('Multithreading in Java allows multiple threads to run concurrently to achieve maximum CPU utilization.', 'Coding', 'Hard')",
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('Swing is a GUI widget toolkit for Java which provides a rich set of buttons, lists, tables, and text inputs.', 'Coding', 'Medium')",
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('To be, or not to be, that is the question: Whether \\'tis nobler in the mind to suffer the slings and arrows of outrageous fortune.', 'Literature', 'Hard')",
                "INSERT INTO paragraphs (content, category, difficulty) VALUES ('All that glitters is not gold; often have you heard that told. Many a man his life hath sold but my outside to behold.', 'Literature', 'Medium')"
        };

        for (String sql : seeds) {
            stmt.executeUpdate(sql);
        }
        System.out.println("Default paragraphs seeded into database successfully.");
    }

    private static void seedDefaultAdmin(Statement stmt) throws SQLException {
        // Hardcoded secure initial admin credentials
        String hash = com.typetrack.util.PasswordHasher.hash("Admin@123");
        String sql = "INSERT INTO users (username, password_hash, name, role, security_question, security_answer_hash) "
                + "VALUES ('admin', '" + hash + "', 'System Administrator', 'ADMIN', 'N/A', 'N/A')";
        stmt.executeUpdate(sql);
        System.out.println("Default Administrator account provisioned (username: admin, password: Admin@123).");
    }
}
