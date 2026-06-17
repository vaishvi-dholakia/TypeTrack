package com.typetrack.ui;

import com.typetrack.model.User;
import com.typetrack.service.AdminService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * The main dashboard for Administrator users.
 * Implements a sidebar navigation layout for managing system entities.
 */
public class AdminDashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final AdminService adminService;
    private JLabel lblWelcome;
    
    // Sub-modules CardLayout
    private CardLayout adminCards;
    private JPanel adminCardsPanel;
    
    private UserManagementPanel userPanel;
    private ParagraphManagementPanel paragraphPanel;
    private JPanel statsGrid;

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.adminService = new AdminService();
        setBackground(UIStyle.COLOR_BG);
        setLayout(new BorderLayout());
        setBorder(UIStyle.BORDER_PADDING);

        initializeUI();
    }

    private void initializeUI() {
        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.COLOR_BG);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        lblWelcome = ComponentsHelper.createTitleLabel("Admin Dashboard");
        headerPanel.add(lblWelcome, BorderLayout.WEST);
        
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setBackground(UIStyle.COLOR_BG);
        rightHeader.add(ComponentsHelper.createThemeToggleButton(mainFrame));
        
        JButton btnLogout = ComponentsHelper.createSecondaryButton("Log Out");
        btnLogout.addActionListener(e -> mainFrame.logoutUser());
        rightHeader.add(btnLogout);
        
        headerPanel.add(rightHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area (Sidebar + Center Cards) ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(UIStyle.COLOR_BG);

        // Sidebar Navigation
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 0, 10));
        sidebar.setBackground(UIStyle.COLOR_CARD);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel lblNav = new JLabel("Navigation");
        lblNav.setFont(UIStyle.FONT_BODY_BOLD);
        lblNav.setForeground(UIStyle.COLOR_TEXT);
        sidebar.add(lblNav);

        JButton btnOverview = ComponentsHelper.createButton("System Overview");
        JButton btnUsers = ComponentsHelper.createSecondaryButton("Manage Users");
        JButton btnParagraphs = ComponentsHelper.createSecondaryButton("Manage Paragraphs");

        sidebar.add(btnOverview);
        sidebar.add(btnUsers);
        sidebar.add(btnParagraphs);

        contentPanel.add(sidebar, BorderLayout.WEST);

        // Center Cards
        adminCards = new CardLayout();
        adminCardsPanel = new JPanel(adminCards);
        adminCardsPanel.setBackground(UIStyle.COLOR_BG);
        
        // Setup Modules for Phase 3
        userPanel = new UserManagementPanel();
        paragraphPanel = new ParagraphManagementPanel();
        
        adminCardsPanel.add(createOverviewPanel(), "Overview");
        adminCardsPanel.add(userPanel, "Users");
        adminCardsPanel.add(paragraphPanel, "Paragraphs");
        
        contentPanel.add(adminCardsPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);

        // Navigation Actions
        btnOverview.addActionListener(e -> {
            refreshStats();
            adminCards.show(adminCardsPanel, "Overview");
        });
        btnUsers.addActionListener(e -> {
            userPanel.loadData();
            adminCards.show(adminCardsPanel, "Users");
        });
        btnParagraphs.addActionListener(e -> {
            paragraphPanel.loadData();
            adminCards.show(adminCardsPanel, "Paragraphs");
        });
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.COLOR_BG);
        
        JLabel lblTitle = new JLabel("System Overview");
        lblTitle.setFont(UIStyle.FONT_SUBTITLE);
        lblTitle.setForeground(UIStyle.COLOR_TEXT);
        panel.add(lblTitle, BorderLayout.NORTH);
        
        statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setBackground(UIStyle.COLOR_BG);
        statsGrid.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        refreshStats();
        
        panel.add(statsGrid, BorderLayout.CENTER);
        return panel;
    }

    private void refreshStats() {
        if (statsGrid != null) {
            statsGrid.removeAll();
            Map<String, String> stats = adminService.getSystemStats();
            statsGrid.add(createStatCard("Total Registered Users", stats.getOrDefault("totalUsers", "0")));
            statsGrid.add(createStatCard("Total Tests Taken", stats.getOrDefault("totalTests", "0")));
            statsGrid.add(createStatCard("Available Paragraphs", stats.getOrDefault("totalParagraphs", "0")));
            statsGrid.add(createStatCard("Global Avg WPM", stats.getOrDefault("avgWpm", "0 WPM")));
            statsGrid.revalidate();
            statsGrid.repaint();
        }
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyle.COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIStyle.FONT_SMALL);
        lblTitle.setForeground(UIStyle.COLOR_MUTED);
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(UIStyle.FONT_SUBTITLE);
        lblValue.setForeground(UIStyle.COLOR_TEXT);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JPanel createPlaceholder(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIStyle.COLOR_BG);
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(UIStyle.FONT_BODY);
        label.setForeground(UIStyle.COLOR_MUTED);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            lblWelcome.setText("Admin Dashboard: " + user.getName());
        }
        refreshStats();
        if (userPanel != null) userPanel.loadData();
        if (paragraphPanel != null) paragraphPanel.loadData();
    }
}
