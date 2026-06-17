package com.typetrack.ui;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Utility to instantiate styled GUI components with consistent premium design traits.
 */
public class ComponentsHelper {

    /**
     * Creates a styled, modern button with hover transitions.
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIStyle.FONT_BODY_BOLD);
        button.setBackground(UIStyle.COLOR_ACCENT);
        button.setForeground(UIStyle.COLOR_TEXT);
        button.setFocusPainted(false);
        button.setBorder(UIStyle.BORDER_INPUT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIStyle.COLOR_ACCENT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIStyle.COLOR_ACCENT);
            }
        });

        return button;
    }

    /**
     * Creates a secondary styled button with a outline/slate appearance.
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIStyle.FONT_BODY_BOLD);
        button.setBackground(UIStyle.COLOR_CARD);
        button.setForeground(UIStyle.COLOR_MUTED);
        button.setFocusPainted(false);
        button.setBorder(UIStyle.BORDER_INPUT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIStyle.COLOR_BG);
                button.setForeground(UIStyle.COLOR_TEXT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIStyle.COLOR_CARD);
                button.setForeground(UIStyle.COLOR_MUTED);
            }
        });

        return button;
    }

    /**
     * Creates a standard theme toggle button that triggers theme switching.
     */
    public static JButton createThemeToggleButton(MainFrame mainFrame) {
        String label = UIStyle.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode";
        JButton button = createSecondaryButton(label);
        button.addActionListener(e -> mainFrame.toggleTheme());
        return button;
    }

    /**
     * Creates a styled JTextField for text input.
     */
    public static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(UIStyle.FONT_BODY);
        textField.setBackground(UIStyle.COLOR_CARD);
        textField.setForeground(UIStyle.COLOR_TEXT);
        textField.setCaretColor(UIStyle.COLOR_TEXT);
        textField.setBorder(UIStyle.BORDER_INPUT);
        return textField;
    }

    /**
     * Creates a styled JPasswordField for password inputs.
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(UIStyle.FONT_BODY);
        passwordField.setBackground(UIStyle.COLOR_CARD);
        passwordField.setForeground(UIStyle.COLOR_TEXT);
        passwordField.setCaretColor(UIStyle.COLOR_TEXT);
        passwordField.setBorder(UIStyle.BORDER_INPUT);
        return passwordField;
    }

    /**
     * Creates a styled header label.
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.FONT_TITLE);
        label.setForeground(UIStyle.COLOR_TEXT);
        return label;
    }

    /**
     * Creates a styled body text label.
     */
    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.FONT_BODY);
        label.setForeground(UIStyle.COLOR_MUTED);
        return label;
    }

    /**
     * Helper to wrap elements in a panel with FlowLayout.
     */
    public static JPanel flowPanel(int alignment, int hgap, int vgap, javax.swing.JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(alignment, hgap, vgap));
        panel.setBackground(UIStyle.COLOR_BG);
        for (javax.swing.JComponent c : components) {
            panel.add(c);
        }
        return panel;
    }
}
