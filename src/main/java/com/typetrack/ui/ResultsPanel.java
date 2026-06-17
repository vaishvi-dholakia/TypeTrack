package com.typetrack.ui;

import com.typetrack.model.Result;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Screen displaying the results of the completed typing test,
 * including a custom key mistakes advisory panel.
 */
public class ResultsPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Result result;

    public ResultsPanel(MainFrame mainFrame, Result result) {
        this.mainFrame = mainFrame;
        this.result = result;

        setBackground(UIStyle.COLOR_BG);
        setLayout(new BorderLayout());
        setBorder(UIStyle.BORDER_PADDING);

        initializeUI();
    }

    private void initializeUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.COLOR_BG);
        
        JLabel lblTitle = ComponentsHelper.createTitleLabel("Test Results & Analysis");
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        // Right side: Theme Toggle
        JPanel rightHeader = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        rightHeader.setBackground(UIStyle.COLOR_BG);
        rightHeader.add(ComponentsHelper.createThemeToggleButton(mainFrame));
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center Container Panel (Grid with 1 row, 2 columns)
        JPanel centerContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        centerContainer.setBackground(UIStyle.COLOR_BG);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        // Column 1: Grid of 4 stats cards
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        statsGrid.setBackground(UIStyle.COLOR_BG);
        statsGrid.add(createResultCard("TYPING SPEED", result.getWpm() + " WPM", UIStyle.COLOR_ACCENT));
        statsGrid.add(createResultCard("ACCURACY", String.format("%.1f%%", result.getAccuracy()), UIStyle.COLOR_SUCCESS));
        statsGrid.add(createResultCard("MISTAKES", result.getMistakes() + " Chars", UIStyle.COLOR_DANGER));
        statsGrid.add(createResultCard("TIME TAKEN", String.format("%.2fs", result.getTimeTakenSeconds()), UIStyle.COLOR_MUTED));
        centerContainer.add(statsGrid);

        // Column 2: Keys to Practice Card
        JPanel practiceCard = new JPanel(new BorderLayout());
        practiceCard.setBackground(UIStyle.COLOR_CARD);
        practiceCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblPracticeTitle = new JLabel("KEYS TO PRACTICE");
        lblPracticeTitle.setFont(UIStyle.FONT_SMALL);
        lblPracticeTitle.setForeground(UIStyle.COLOR_MUTED);
        practiceCard.add(lblPracticeTitle, BorderLayout.NORTH);

        // Process key typos
        Map<Character, Integer> typos = result.getTypoFrequency();
        StringBuilder sb = new StringBuilder();
        if (typos.isEmpty()) {
            sb.append("\n\n   ★ FLAWLESS TYPING! ★\n\n   No key typos were recorded.\n   Your finger-key alignment is perfect.");
        } else {
            // Sort by frequency descending
            List<Map.Entry<Character, Integer>> sortedTypos = new ArrayList<>(typos.entrySet());
            sortedTypos.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            sb.append("\nHere are your most frequent key typos:\n\n");
            for (Map.Entry<Character, Integer> entry : sortedTypos) {
                char key = entry.getKey();
                String keyLabel = (key == ' ') ? "Spacebar" : "'" + key + "'";
                sb.append(String.format("   Key  %s   \t->   %d typo%s\n", keyLabel, entry.getValue(), entry.getValue() > 1 ? "s" : ""));
            }
            sb.append("\nTip: Focus on slowing down slightly when typing words containing these keys.");
        }

        JTextArea areaAdvice = new JTextArea(sb.toString());
        areaAdvice.setFont(UIStyle.FONT_BODY);
        areaAdvice.setForeground(UIStyle.COLOR_TEXT);
        areaAdvice.setBackground(UIStyle.COLOR_CARD);
        areaAdvice.setEditable(false);
        areaAdvice.setLineWrap(true);
        areaAdvice.setWrapStyleWord(true);

        JScrollPane scrollAdvice = new JScrollPane(areaAdvice);
        scrollAdvice.setBorder(null);
        scrollAdvice.getViewport().setBackground(UIStyle.COLOR_CARD);
        practiceCard.add(scrollAdvice, BorderLayout.CENTER);

        centerContainer.add(practiceCard);

        add(centerContainer, BorderLayout.CENTER);

        // Actions Panel (Bottom)
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(UIStyle.COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JButton btnAgain = ComponentsHelper.createButton("Take Another Test");
        btnAgain.setPreferredSize(new Dimension(200, 45));
        btnAgain.addActionListener(e -> startNewTest());
        gbc.gridx = 0;
        gbc.gridy = 0;
        actionPanel.add(btnAgain, gbc);

        JButton btnDashboard = ComponentsHelper.createSecondaryButton("Back to Dashboard");
        btnDashboard.setPreferredSize(new Dimension(200, 45));
        btnDashboard.addActionListener(e -> mainFrame.showPanel("Dashboard"));
        gbc.gridx = 1;
        actionPanel.add(btnDashboard, gbc);

        JButton btnHistory = ComponentsHelper.createSecondaryButton("View History");
        btnHistory.setPreferredSize(new Dimension(200, 45));
        btnHistory.addActionListener(e -> mainFrame.showPanel("History"));
        gbc.gridx = 2;
        actionPanel.add(btnHistory, gbc);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createResultCard(String label, String value, java.awt.Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyle.COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(UIStyle.FONT_SMALL);
        lblTitle.setForeground(UIStyle.COLOR_MUTED);
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(UIStyle.FONT_SUBTITLE);
        lblValue.setForeground(valueColor);
        lblValue.setHorizontalAlignment(JLabel.CENTER);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private void startNewTest() {
        String paragraph = mainFrame.getParagraphManager().getRandomParagraph();
        TypingTestPanel testPanel = new TypingTestPanel(mainFrame, paragraph);
        mainFrame.registerDynamicPanel(testPanel, "TypingTest");
        mainFrame.showPanel("TypingTest");
    }
}
