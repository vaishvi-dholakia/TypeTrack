package com.typetrack.service;

import com.typetrack.model.Achievement;
import com.typetrack.model.LeaderboardEntry;
import com.typetrack.model.Result;
import com.typetrack.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores test results loaded from MySQL and handles business statistics.
 */
public class StatisticsManager {
    private final List<Result> results;
    private final List<Achievement> achievements;

    public StatisticsManager() {
        this.results = new ArrayList<>();
        this.achievements = new ArrayList<>();
        initializeAchievements();
    }

    private void initializeAchievements() {
        achievements.add(new Achievement("First Steps", "Complete your first typing test.", "🌱", 
            stats -> stats.getTotalTests() >= 1));
        
        achievements.add(new Achievement("Speed Demon", "Reach a speed of 50 WPM or higher.", "⚡", 
            stats -> stats.getHighestWpm() >= 50));
        
        achievements.add(new Achievement("Hyper Sonic", "Reach a speed of 80 WPM or higher.", "👑", 
            stats -> stats.getHighestWpm() >= 80));
        
        achievements.add(new Achievement("Laser Focus", "Complete a test with 100.0% accuracy.", "🎯", 
            stats -> {
                for (Result r : stats.getResults()) {
                    if (r.getAccuracy() >= 100.0) return true;
                }
                return false;
            }));
        
        achievements.add(new Achievement("Typing Marathon", "Complete 5 or more typing tests.", "🔥", 
            stats -> stats.getTotalTests() >= 5));
    }

    /**
     * Loads the results of a specific user from the database.
     */
    public void loadUserResults(int userId) {
        results.clear();
        String sql = "SELECT wpm, accuracy, mistakes, time_taken FROM results WHERE user_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int wpm = rs.getInt("wpm");
                    double accuracy = rs.getDouble("accuracy");
                    int mistakes = rs.getInt("mistakes");
                    double timeTaken = rs.getDouble("time_taken");
                    
                    results.add(new Result(timeTaken, mistakes, accuracy, wpm, new HashMap<>()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading user results from database: " + e.getMessage());
        }
    }

    /**
     * Records a new test result in the database and caches it.
     */
    public void addResult(Result result, int userId) {
        if (result == null) return;

        // Save to Database
        String sql = "INSERT INTO results (user_id, wpm, accuracy, mistakes, time_taken) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, result.getWpm());
            stmt.setDouble(3, result.getAccuracy());
            stmt.setInt(4, result.getMistakes());
            stmt.setDouble(5, result.getTimeTakenSeconds());
            
            stmt.executeUpdate();
            
            // Append to in-memory list
            results.add(result);
        } catch (SQLException e) {
            System.err.println("Failed to save test result to database: " + e.getMessage());
        }
    }

    /**
     * Queries the database to retrieve global rankings.
     */
    public List<LeaderboardEntry> getGlobalLeaderboard() {
        List<LeaderboardEntry> list = new ArrayList<>();
        String sql = "SELECT u.name, u.username, MAX(r.wpm) as max_wpm, AVG(r.accuracy) as avg_acc "
                   + "FROM results r "
                   + "JOIN users u ON r.user_id = u.id "
                   + "GROUP BY u.id, u.name, u.username "
                   + "ORDER BY max_wpm DESC "
                   + "LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String username = rs.getString("username");
                int maxWpm = rs.getInt("max_wpm");
                double avgAcc = rs.getDouble("avg_acc");
                list.add(new LeaderboardEntry(name, username, maxWpm, avgAcc));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching global leaderboard: " + e.getMessage());
        }
        return list;
    }

    /**
     * Calculates the highest WPM recorded.
     */
    public int getHighestWpm() {
        int highest = 0;
        for (Result r : results) {
            if (r.getWpm() > highest) {
                highest = r.getWpm();
            }
        }
        return highest;
    }

    /**
     * Calculates the average WPM.
     */
    public double getAverageWpm() {
        if (results.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Result r : results) {
            sum += r.getWpm();
        }
        return sum / results.size();
    }

    /**
     * Calculates the average accuracy.
     */
    public double getAverageAccuracy() {
        if (results.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (Result r : results) {
            sum += r.getAccuracy();
        }
        return sum / results.size();
    }

    public int getTotalTests() {
        return results.size();
    }

    public List<Result> getResults() {
        return results;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }
}
