package com.typetrack.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Core engine responsible for calculating typing test metrics.
 * Uses Levenshtein Distance (Dynamic Programming) to calculate exact mistake counts
 * and track specific character typos.
 */
public class TypingEngine {
    private final String targetText;
    private long startTime;
    private long endTime;
    private int mistakes;
    
    // Tracks character -> frequency of typos
    private final Map<Character, Integer> typoFrequency;

    public TypingEngine(String targetText) {
        if (targetText == null || targetText.trim().isEmpty()) {
            throw new IllegalArgumentException("Target text can't be empty.");
        }
        this.targetText = targetText;
        this.typoFrequency = new HashMap<>();
    }

    /**
     * Records the start time of the typing test.
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Records the end time of the typing test.
     */
    public void stop() {
        this.endTime = System.currentTimeMillis();
    }

    /**
     * Calculates mistakes and specific character typos using the Levenshtein Distance algorithm.
     * parameter - typedText : it is the text input by the user.
     */
    public void calculateResults(String typedText) {
        if (typedText == null) {
            typedText = "";
        }

        int m = targetText.length();
        int n = typedText.length();

        // dp[i][j] stores the edit distance between targetText[0..i-1] and typedText[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        // Base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Deletion cost
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Insertion cost
        }

        // Fill DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (targetText.charAt(i - 1) == typedText.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int replace = dp[i - 1][j - 1] + 1;
                    int delete = dp[i - 1][j] + 1;
                    int insert = dp[i][j - 1] + 1;
                    dp[i][j] = Math.min(replace, Math.min(delete, insert));
                }
            }
        }

        // The edit distance is our true mistake count (insertions, deletions, substitutions)
        this.mistakes = dp[m][n];

        // Backtrack through DP table to identify specific keys the user got wrong
        typoFrequency.clear();
        int i = m;
        int j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && targetText.charAt(i - 1) == typedText.charAt(j - 1)) {
                // Match - no mistake
                i--;
                j--;
            } else {
                // Mismatch identified. Determine which operation was optimal.
                if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                    // Substitution: User typed typedText[j-1] instead of targetText[i-1]
                    char targetChar = targetText.charAt(i - 1);
                    typoFrequency.put(targetChar, typoFrequency.getOrDefault(targetChar, 0) + 1);
                    i--;
                    j--;
                } else if (i > 0 && (j == 0 || dp[i][j] == dp[i - 1][j] + 1)) {
                    // Deletion: User omitted targetText[i-1]
                    char targetChar = targetText.charAt(i - 1);
                    typoFrequency.put(targetChar, typoFrequency.getOrDefault(targetChar, 0) + 1);
                    i--;
                } else if (j > 0 && (i == 0 || dp[i][j] == dp[i][j - 1] + 1)) {
                    // Insertion: User typed an extra typedText[j-1] key
                    char insertedChar = typedText.charAt(j - 1);
                    typoFrequency.put(insertedChar, typoFrequency.getOrDefault(insertedChar, 0) + 1);
                    j--;
                } else {
                    // Fallback to prevent infinite loops
                    if (i > 0) i--;
                    if (j > 0) j--;
                }
            }
        }
    }

    /**
     * Returns the time taken in seconds.
     */
    public double getTimeTakenSeconds() {
        if (startTime == 0 || endTime == 0 || endTime < startTime) {
            return 0.0;
        }
        return (endTime - startTime) / 1000.0;
    }

    /**
     * Calculates Words Per Minute (WPM) using the standard: 5 characters = 1 word.
     * Formula: (Keystrokes / 5) / (Time in Minutes)
     * @param typedText The text typed by the user.
     */
    public int getWpm(String typedText) {
        double timeSeconds = getTimeTakenSeconds();
        if (timeSeconds <= 0) {
            return 0;
        }

        int keystrokes = typedText != null ? typedText.length() : 0;
        double timeMinutes = timeSeconds / 60.0;

        double wpm = (keystrokes / 5.0) / timeMinutes;
        return (int) Math.round(wpm);
    }

    /**
     * Calculates the typing accuracy as a percentage.
     * Formula: ((Target Length - Mistakes) / Target Length) * 100
     */
    public double getAccuracy() {
        int targetLen = targetText.length();
        int correctChars = targetLen - mistakes;

        if (correctChars < 0) {
            return 0.0;
        }

        return ((double) correctChars / targetLen) * 100.0;
    }

    public int getMistakes() {
        return this.mistakes;
    }

    public String getTargetText() {
        return this.targetText;
    }

    public Map<Character, Integer> getTypoFrequency() {
        return typoFrequency;
    }
}
