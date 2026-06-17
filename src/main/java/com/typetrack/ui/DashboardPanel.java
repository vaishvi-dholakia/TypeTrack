package com.typetrack.ui;

import com.typetrack.model.User;
import com.typetrack.service.StatisticsManager;
import com.typetrack.model.Achievement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Main dashboard displaying user performance stats, action shortcuts,
 * visual progress chart, and unlocked achievement badges.
 */
public class DashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private JLabel lblWelcome;
    
    // Stats labels
    private JLabel lblTotalTests;
    private JLabel lblHighWpm;
    private JLabel lblAvgWpm;
    private JLabel lblAvgAccuracy;

    // Chart component
    private WpmTrendChart chartWpm;

    // Achievements panel and badges container
    private JPanel achievementsPanel;
    private JPanel badgesPanel;

    // Filter selectors
    private JComboBox<String> comboCategory;
    private JComboBox<String> comboDifficulty;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.COLOR_BG);
        setLayout(new BorderLayout());
        setBorder(UIStyle.BORDER_PADDING);

        initializeUI();
    }

    private void initializeUI() {
        // Welcome and Header Area
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.COLOR_BG);
        
        lblWelcome = ComponentsHelper.createTitleLabel("Welcome back!");
        headerPanel.add(lblWelcome, BorderLayout.WEST);
        
        // Right Header with Theme Toggle, Logout, and Exit
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setBackground(UIStyle.COLOR_BG);
        
        rightHeader.add(ComponentsHelper.createThemeToggleButton(mainFrame));
        
        JButton btnLogout = ComponentsHelper.createSecondaryButton("Log Out");
        btnLogout.addActionListener(e -> mainFrame.logoutUser());
        rightHeader.add(btnLogout);

        JButton btnExit = ComponentsHelper.createSecondaryButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        rightHeader.add(btnExit);
        
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Stats Dashboard Grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setBackground(UIStyle.COLOR_BG);

        statsGrid.add(createStatCard("Total Tests Taken", lblTotalTests = new JLabel("0")));
        statsGrid.add(createStatCard("Highest Speed", lblHighWpm = new JLabel("0 WPM")));
        statsGrid.add(createStatCard("Average Speed", lblAvgWpm = new JLabel("0.0 WPM")));
        statsGrid.add(createStatCard("Average Accuracy", lblAvgAccuracy = new JLabel("0.0%")));

        // Split center panel for stats grid and custom chart
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setBackground(UIStyle.COLOR_BG);
        
        splitPanel.add(statsGrid);
        
        chartWpm = new WpmTrendChart();
        splitPanel.add(chartWpm);

        // Achievements & Badges Panel (Outer BorderLayout to keep header and badges aligned)
        achievementsPanel = new JPanel(new BorderLayout(0, 10)); // 10px vertical gap
        achievementsPanel.setBackground(UIStyle.COLOR_CARD);
        achievementsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblSectionTitle = new JLabel("Badges & Achievements");
        lblSectionTitle.setFont(UIStyle.FONT_BODY_BOLD);
        lblSectionTitle.setForeground(UIStyle.COLOR_TEXT);
        achievementsPanel.add(lblSectionTitle, BorderLayout.NORTH); // Title on top

        // Grid layout of 1 row, 5 columns stretching the full width of the panel
        badgesPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        badgesPanel.setOpaque(false);
        achievementsPanel.add(badgesPanel, BorderLayout.CENTER); // Badges take the full center width

        // Assemble into a center container panel
        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setBackground(UIStyle.COLOR_BG);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        centerContainer.add(splitPanel, BorderLayout.CENTER);
        centerContainer.add(achievementsPanel, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);

        // Navigation Actions & Filters Panel
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(UIStyle.COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Row 0: Dropdown Selectors Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        filterPanel.setBackground(UIStyle.COLOR_BG);

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(UIStyle.FONT_BODY_BOLD);
        lblCat.setForeground(UIStyle.COLOR_TEXT);
        filterPanel.add(lblCat);

        comboCategory = new JComboBox<>(mainFrame.getParagraphManager().getCategories().toArray(new String[0]));
        comboCategory.setFont(UIStyle.FONT_BODY);
        comboCategory.setBackground(UIStyle.COLOR_CARD);
        comboCategory.setForeground(UIStyle.COLOR_TEXT);
        filterPanel.add(comboCategory);

        JLabel lblDiff = new JLabel("Difficulty:");
        lblDiff.setFont(UIStyle.FONT_BODY_BOLD);
        lblDiff.setForeground(UIStyle.COLOR_TEXT);
        filterPanel.add(lblDiff);

        comboDifficulty = new JComboBox<>(mainFrame.getParagraphManager().getDifficulties().toArray(new String[0]));
        comboDifficulty.setFont(UIStyle.FONT_BODY);
        comboDifficulty.setBackground(UIStyle.COLOR_CARD);
        comboDifficulty.setForeground(UIStyle.COLOR_TEXT);
        filterPanel.add(comboDifficulty);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        actionPanel.add(filterPanel, gbc);

        // Row 1: Action Buttons
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;

        JButton btnStartTest = ComponentsHelper.createButton("Start Typing Test");
        btnStartTest.setPreferredSize(new Dimension(200, 45));
        btnStartTest.addActionListener(e -> startTestFlow());
        gbc.gridx = 0;
        actionPanel.add(btnStartTest, gbc);

        JButton btnViewHistory = ComponentsHelper.createSecondaryButton("View Performance History");
        btnViewHistory.setPreferredSize(new Dimension(200, 45));
        btnViewHistory.addActionListener(e -> mainFrame.showPanel("History"));
        gbc.gridx = 1;
        actionPanel.add(btnViewHistory, gbc);

        JButton btnViewLeaderboard = ComponentsHelper.createSecondaryButton("View Leaderboard");
        btnViewLeaderboard.setPreferredSize(new Dimension(200, 45));
        btnViewLeaderboard.addActionListener(e -> mainFrame.showPanel("Leaderboard"));
        gbc.gridx = 2;
        actionPanel.add(btnViewLeaderboard, gbc);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyle.COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1), // Slate 700
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIStyle.FONT_SMALL);
        lblTitle.setForeground(UIStyle.COLOR_MUTED);
        card.add(lblTitle, BorderLayout.NORTH);

        valueLabel.setFont(UIStyle.FONT_SUBTITLE);
        valueLabel.setForeground(UIStyle.COLOR_TEXT);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Refreshes stats, achievements, and chart displayed when navigating back.
     */
    public void refresh() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            lblWelcome.setText("Welcome back, " + user.getName() + "!");
        }

        StatisticsManager stats = mainFrame.getStatsManager();
        lblTotalTests.setText(String.valueOf(stats.getTotalTests()));
        lblHighWpm.setText(stats.getHighestWpm() + " WPM");
        lblAvgWpm.setText(String.format("%.1f WPM", stats.getAverageWpm()));
        lblAvgAccuracy.setText(String.format("%.1f%%", stats.getAverageAccuracy()));

        // Update progress chart
        chartWpm.setResults(stats.getResults());

        // Update achievements badges
        badgesPanel.removeAll();

        for (Achievement ach : stats.getAchievements()) {
            boolean unlocked = ach.isUnlocked(stats);
            
            JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            badge.setOpaque(false);
            
            JLabel lblIcon = new JLabel(unlocked ? ach.getIcon() : "🔒");
            lblIcon.setFont(UIStyle.FONT_BODY_BOLD);
            
            JLabel lblName = new JLabel(ach.getName());
            lblName.setFont(UIStyle.FONT_SMALL);
            
            if (unlocked) {
                // Vibrant gold/amber color for unlocked achievements to make them pop!
                Color goldColor = UIStyle.isDarkMode() ? new Color(250, 204, 21) : new Color(217, 119, 6);
                lblIcon.setForeground(goldColor);
                lblName.setForeground(goldColor);
            } else {
                // Muted gray for locked
                lblIcon.setForeground(UIStyle.COLOR_MUTED);
                lblName.setForeground(UIStyle.COLOR_MUTED);
            }
            
            badge.add(lblIcon);
            badge.add(lblName);
            
            // Set tooltips detailing requirements
            badge.setToolTipText(ach.getDescription() + (unlocked ? " (Unlocked!)" : " (Locked)"));
            
            badgesPanel.add(badge);
        }
        
        achievementsPanel.revalidate();
        achievementsPanel.repaint();
    }

    private void startTestFlow() {
        String category = (String) comboCategory.getSelectedItem();
        String difficulty = (String) comboDifficulty.getSelectedItem();
        
        // Retrieve filtered paragraph
        String paragraph = mainFrame.getParagraphManager().getRandomParagraph(category, difficulty);
        
        // Instantiate the typing test panel dynamically to avoid old states
        TypingTestPanel testPanel = new TypingTestPanel(mainFrame, paragraph);
        mainFrame.registerDynamicPanel(testPanel, "TypingTest");
        mainFrame.showPanel("TypingTest");
    }
}
