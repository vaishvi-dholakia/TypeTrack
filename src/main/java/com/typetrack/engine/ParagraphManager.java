package com.typetrack.engine;

import com.typetrack.model.Paragraph;
import com.typetrack.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Manages typing paragraphs stored in the MySQL database.
 * Supports filtering by category and difficulty.
 */
public class ParagraphManager {
    private final List<Paragraph> paragraphs;
    private final Random random;

    public ParagraphManager() {
        this.paragraphs = new ArrayList<>();
        this.random = new Random();
        loadParagraphsFromDb();
    }

    private void loadParagraphsFromDb() {
        paragraphs.clear();
        String sql = "SELECT id, content, category, difficulty FROM paragraphs";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                String category = rs.getString("category");
                String difficulty = rs.getString("difficulty");
                paragraphs.add(new Paragraph(id, content, category, difficulty));
            }
        } catch (SQLException e) {
            System.err.println("Error loading paragraphs from database: " + e.getMessage());
        }
    }

    /**
     * Returns a random paragraph with no filters.
     */
    public String getRandomParagraph() {
        return getRandomParagraph("All", "All");
    }

    /**
     * Returns a random paragraph matching the chosen category and difficulty.
     * "All" acts as a wildcard parameter.
     */
    public String getRandomParagraph(String category, String difficulty) {
        List<Paragraph> filtered = new ArrayList<>();
        for (Paragraph p : paragraphs) {
            boolean matchesCategory = "All".equalsIgnoreCase(category) || p.getCategory().equalsIgnoreCase(category);
            boolean matchesDifficulty = "All".equalsIgnoreCase(difficulty) || p.getDifficulty().equalsIgnoreCase(difficulty);
            if (matchesCategory && matchesDifficulty) {
                filtered.add(p);
            }
        }

        if (filtered.isEmpty()) {
            if (paragraphs.isEmpty()) {
                return "The quick brown fox jumps over the lazy dog.";
            }
            return paragraphs.get(random.nextInt(paragraphs.size())).getText();
        }

        return filtered.get(random.nextInt(filtered.size())).getText();
    }

    /**
     * Returns a list of unique categories available.
     */
    public List<String> getCategories() {
        Set<String> cats = new HashSet<>();
        cats.add("All");
        for (Paragraph p : paragraphs) {
            cats.add(p.getCategory());
        }
        return new ArrayList<>(cats);
    }

    /**
     * Returns a list of unique difficulties available.
     */
    public List<String> getDifficulties() {
        Set<String> diffs = new HashSet<>();
        diffs.add("All");
        for (Paragraph p : paragraphs) {
            diffs.add(p.getDifficulty());
        }
        return new ArrayList<>(diffs);
    }
}
