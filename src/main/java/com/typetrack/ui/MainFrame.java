package com.typetrack.ui;

import com.typetrack.engine.ParagraphManager;
import com.typetrack.model.User;
import com.typetrack.service.AdminService;
import com.typetrack.service.AuthService;
import com.typetrack.service.StatisticsManager;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The main container window for the TypeTrack GUI application.
 * Manages view transitions using a CardLayout, stores in-memory session data,
 * and handles theme swapping.
 */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    
    // In-memory states (persistent across views)
    private User currentUser;
    private final StatisticsManager statsManager;
    private final ParagraphManager paragraphManager;
    private final AuthService authService;
    private final AdminService adminService;

    // View Panel references
    private DashboardPanel dashboardPanel;
    private HistoryPanel historyPanel;
    private LeaderboardPanel leaderboardPanel;
    private AdminDashboardPanel adminDashboardPanel;

    public MainFrame() {
        setTitle("TypeTrack - Typing Performance Analysis System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null); // Center window
        
        // Initialize managers
        this.statsManager = new StatisticsManager();
        this.paragraphManager = new ParagraphManager();
        this.authService = new AuthService();
        this.adminService = new AdminService();
        
        // Setup CardLayout Panel
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(UIStyle.COLOR_BG);
        
        // Initialize panels and add them to card container
        initializePanels();
        
        add(cardsPanel);
        
        // Start on Login view
        showPanel("Login");
    }

    private void initializePanels() {
        cardsPanel.add(new LoginPanel(this), "Login");
        cardsPanel.add(new RegisterPanel(this), "Register");
        cardsPanel.add(new ForgotPasswordPanel(this), "ForgotPassword");
        
        // These will be refreshed dynamically when the user signs in
        this.dashboardPanel = new DashboardPanel(this);
        this.historyPanel = new HistoryPanel(this);
        this.leaderboardPanel = new LeaderboardPanel(this);
        this.adminDashboardPanel = new AdminDashboardPanel(this);
        
        cardsPanel.add(dashboardPanel, "Dashboard");
        cardsPanel.add(historyPanel, "History");
        cardsPanel.add(leaderboardPanel, "Leaderboard");
        cardsPanel.add(adminDashboardPanel, "AdminDashboard");
    }

    /**
     * Switch the displayed panel.
     */
    public void showPanel(String panelName) {
        if ("Dashboard".equals(panelName)) {
            dashboardPanel.refresh();
        } else if ("History".equals(panelName)) {
            historyPanel.refresh();
        } else if ("Leaderboard".equals(panelName)) {
            leaderboardPanel.refresh();
        } else if ("AdminDashboard".equals(panelName)) {
            adminDashboardPanel.refresh();
        }
        cardLayout.show(cardsPanel, panelName);
    }

    /**
     * Add a panel to the CardLayout dynamically.
     */
    public void registerDynamicPanel(JPanel panel, String name) {
        cardsPanel.add(panel, name);
    }

    /**
     * Mutates color scheme and rebuilds all views.
     */
    public void toggleTheme() {
        UIStyle.toggleTheme();
        
        // Recreate the UI panels
        cardsPanel.removeAll();
        initializePanels();
        
        // Propagate theme updates across the frame tree
        SwingUtilities.updateComponentTreeUI(this);
        
        if (currentUser != null) {
            showPanel("Dashboard");
        } else {
            showPanel("Login");
        }
    }

    public void loginUser(User user) {
        this.currentUser = user;
        if ("ADMIN".equals(user.getRole())) {
            showPanel("AdminDashboard");
        } else {
            this.statsManager.loadUserResults(user.getUserId());
            showPanel("Dashboard");
        }
    }

    public void logoutUser() {
        this.currentUser = null;
        showPanel("Login");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public StatisticsManager getStatsManager() {
        return statsManager;
    }

    public ParagraphManager getParagraphManager() {
        return paragraphManager;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public static void startApp() {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
