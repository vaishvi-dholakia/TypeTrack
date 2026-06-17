package com.typetrack.model;

import java.util.Map;

/**
 * Model representing the result of a single typing test.
 */
public class Result {
    private final double timeTakenSeconds;
    private final int mistakes;
    private final double accuracy;
    private final int wpm;
    private final Map<Character, Integer> typoFrequency;

    public Result(double timeTakenSeconds, int mistakes, double accuracy, int wpm, Map<Character, Integer> typoFrequency) {
        this.timeTakenSeconds = timeTakenSeconds;
        this.mistakes = mistakes;
        this.accuracy = accuracy;
        this.wpm = wpm;
        this.typoFrequency = typoFrequency;
    }

    public double getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public int getMistakes() {
        return mistakes;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getWpm() {
        return wpm;
    }

    public Map<Character, Integer> getTypoFrequency() {
        return typoFrequency;
    }

    /**
     * Helper to print this result nicely in the console.
     */
    public void displaySummary() {
        System.out.println("============= PERFORMANCE SUMMARY =============");
        System.out.printf("Time Taken   : %.2f seconds\n", timeTakenSeconds);
        System.out.printf("Mistakes     : %d characters\n", mistakes);
        System.out.printf("Accuracy     : %.2f%%\n", accuracy);
        System.out.printf("Typing Speed : %d WPM (Words Per Minute)\n", wpm);
        System.out.println("===============================================");
    }
}
