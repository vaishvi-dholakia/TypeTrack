package com.typetrack.ui;

import com.typetrack.model.Result;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Custom 2D Graphics panel rendering WPM speed progression charts dynamically.
 */
public class WpmTrendChart extends JPanel {
    private List<Result> results;

    public WpmTrendChart() {
        setBackground(UIStyle.COLOR_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    public void setResults(List<Result> results) {
        this.results = results;
        repaint(); // Force repaint with new data points
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smooth vector strokes and text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 35;

        // Render Panel Title
        g2d.setColor(UIStyle.COLOR_TEXT);
        g2d.setFont(UIStyle.FONT_BODY_BOLD);
        g2d.drawString("WPM Speed Progression", padding, padding - 10);

        if (results == null || results.isEmpty()) {
            g2d.setColor(UIStyle.COLOR_MUTED);
            g2d.setFont(UIStyle.FONT_BODY);
            String message = "Take some typing tests to render progress charts!";
            int messageWidth = g2d.getFontMetrics().stringWidth(message);
            g2d.drawString(message, (width - messageWidth) / 2, height / 2);
            return;
        }

        // 1. Calculate maximum WPM value to scale the Y axis
        int maxWpm = 60; // Minimum default ceiling
        for (Result r : results) {
            if (r.getWpm() > maxWpm) {
                maxWpm = r.getWpm();
            }
        }
        // Round max up to nearest 10 for clean axis grid labeling
        maxWpm = ((maxWpm + 9) / 10) * 10;

        int chartWidth = width - (2 * padding);
        int chartHeight = height - (2 * padding) - 20;
        int chartYStart = padding + 15;

        // 2. Draw horizontal grid lines and speed labels
        g2d.setFont(UIStyle.FONT_SMALL);
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            int y = chartYStart + chartHeight - (i * chartHeight / gridLines);
            
            // Draw grid line
            g2d.setColor(new Color(0x33, 0x41, 0x55, 100)); // semi-transparent grid line
            g2d.drawLine(padding, y, padding + chartWidth, y);

            // Draw Y-axis text
            int val = (i * maxWpm / gridLines);
            g2d.setColor(UIStyle.COLOR_MUTED);
            g2d.drawString(val + " WPM", padding - 30, y + 4);
        }

        // 3. Compute coordinates for each result
        int pointsCount = results.size();
        int[] xCoords = new int[pointsCount];
        int[] yCoords = new int[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            // Map X to step spacing
            if (pointsCount == 1) {
                xCoords[i] = padding + (chartWidth / 2);
            } else {
                xCoords[i] = padding + (i * chartWidth / (pointsCount - 1));
            }
            
            // Map Y to speed height
            int wpm = results.get(i).getWpm();
            yCoords[i] = chartYStart + chartHeight - (wpm * chartHeight / maxWpm);
        }

        // 4. Draw Connective Trend Line
        g2d.setColor(UIStyle.COLOR_ACCENT);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < pointsCount - 1; i++) {
            g2d.drawLine(xCoords[i], yCoords[i], xCoords[i + 1], yCoords[i + 1]);
        }

        // 5. DrawGlowing Node Markers
        int radius = 5;
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < pointsCount; i++) {
            // Draw background fill circle
            g2d.setColor(UIStyle.COLOR_BG);
            g2d.fillOval(xCoords[i] - radius, yCoords[i] - radius, radius * 2, radius * 2);
            
            // Draw accent outline border
            g2d.setColor(UIStyle.COLOR_ACCENT);
            g2d.drawOval(xCoords[i] - radius, yCoords[i] - radius, radius * 2, radius * 2);
        }
    }
}
