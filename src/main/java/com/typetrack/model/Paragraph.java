package com.typetrack.model;

/**
 * Model representing a typing test paragraph with metadata.
 */
public class Paragraph {
    private final int id;
    private final String text;
    private final String category;
    private final String difficulty;

    public Paragraph(int id, String text, String category, String difficulty) {
        this.id = id;
        this.text = text;
        this.category = category;
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
