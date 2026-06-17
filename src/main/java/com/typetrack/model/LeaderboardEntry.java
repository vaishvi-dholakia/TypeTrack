package com.typetrack.model;

/**
 * Model representing a single user entry on the global leaderboard.
 */
public class LeaderboardEntry {
    private final String name;
    private final String username;
    private final int maxWpm;
    private final double avgAccuracy;

    public LeaderboardEntry(String name, String username, int maxWpm, double avgAccuracy) {
        this.name = name;
        this.username = username;
        this.maxWpm = maxWpm;
        this.avgAccuracy = avgAccuracy;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public int getMaxWpm() {
        return maxWpm;
    }

    public double getAvgAccuracy() {
        return avgAccuracy;
    }
}
