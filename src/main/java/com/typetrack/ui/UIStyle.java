package com.typetrack.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Styling tokens and constants. Supports dynamic switching between Dark and Light themes.
 */
public class UIStyle {
    private static boolean darkMode = true;

    // Mutable Theme Colors (referenced identically, but values update on theme toggle)
    public static Color COLOR_BG = new Color(0x0f, 0x17, 0x2a);       // Slate 900
    public static Color COLOR_CARD = new Color(0x1e, 0x29, 0x3b);     // Slate 800
    public static Color COLOR_TEXT = new Color(0xf8, 0xfa, 0xfc);     // Slate 50
    public static Color COLOR_MUTED = new Color(0x94, 0xa3, 0xb8);    // Slate 400
    
    // Constant Accent Colors
    public static final Color COLOR_ACCENT = new Color(0x63, 0x66, 0xf1);   // Indigo 500
    public static final Color COLOR_ACCENT_HOVER = new Color(0x4f, 0x46, 0xe5); // Indigo 600
    public static final Color COLOR_SUCCESS = new Color(0x10, 0xb9, 0x81);  // Emerald 500
    public static final Color COLOR_DANGER = new Color(0xef, 0x44, 0x44);   // Red 500
    
    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 16);

    // Common Borders
    public static final Border BORDER_PADDING = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    public static final Border BORDER_INPUT = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0x47, 0x55, 0x69), 1), // Slate 600
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );

    public static boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Toggles between Dark and Light palettes, updating color pointers.
     */
    public static void toggleTheme() {
        darkMode = !darkMode;
        if (darkMode) {
            // Dark Mode palette (Slate-indigo)
            COLOR_BG = new Color(0x0f, 0x17, 0x2a);
            COLOR_CARD = new Color(0x1e, 0x29, 0x3b);
            COLOR_TEXT = new Color(0xf8, 0xfa, 0xfc);
            COLOR_MUTED = new Color(0x94, 0xa3, 0xb8);
        } else {
            // Light Mode palette (Clean light gray-blue)
            COLOR_BG = new Color(0xf1, 0xf5, 0xf9);     // Slate 100
            COLOR_CARD = new Color(0xff, 0xff, 0xff);   // Pure white
            COLOR_TEXT = new Color(0x0f, 0x17, 0x2a);   // Slate 900
            COLOR_MUTED = new Color(0x47, 0x55, 0x69);  // Slate 600
        }
    }
}
