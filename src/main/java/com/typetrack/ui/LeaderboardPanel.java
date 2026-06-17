package com.typetrack.ui;

import com.typetrack.model.LeaderboardEntry;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Screen presenting comparative rankings of top typists.
 */
public class LeaderboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;

    public LeaderboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.COLOR_BG);
        setLayout(new BorderLayout());
        setBorder(UIStyle.BORDER_PADDING);

        initializeUI();
    }

    private void initializeUI() {
        // --- HEADER PANEL (North) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.COLOR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel lblTitle = ComponentsHelper.createTitleLabel("Global Leaderboard");
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setBackground(UIStyle.COLOR_BG);
        rightHeader.add(ComponentsHelper.createThemeToggleButton(mainFrame));
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- TABLE PANEL (Center) ---
        String[] columnNames = {"Rank", "Name", "Username", "Best Speed (WPM)", "Avg Accuracy"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        
        // Dark-Mode Table Styling
        table.setBackground(UIStyle.COLOR_CARD);
        table.setForeground(UIStyle.COLOR_TEXT);
        table.setGridColor(new Color(0x33, 0x41, 0x55)); // Slate 700
        table.setFont(UIStyle.FONT_BODY);
        table.setRowHeight(35);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(UIStyle.COLOR_ACCENT);
        table.setSelectionForeground(UIStyle.COLOR_TEXT);

        table.getTableHeader().setBackground(UIStyle.COLOR_BG);
        table.getTableHeader().setForeground(UIStyle.COLOR_TEXT);
        table.getTableHeader().setFont(UIStyle.FONT_BODY_BOLD);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x33, 0x41, 0x55)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1));
        scrollPane.getViewport().setBackground(UIStyle.COLOR_BG);
        add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER PANEL (South) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIStyle.COLOR_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnBack = ComponentsHelper.createSecondaryButton("Back to Dashboard");
        btnBack.setPreferredSize(new Dimension(200, 45));
        btnBack.addActionListener(e -> mainFrame.showPanel("Dashboard"));
        bottomPanel.add(btnBack, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Refreshes the leaderboard rows.
     */
    public void refresh() {
        tableModel.setRowCount(0);

        List<LeaderboardEntry> entries = mainFrame.getStatsManager().getGlobalLeaderboard();
        int rank = 1;
        for (LeaderboardEntry entry : entries) {
            String rankLabel;
            if (rank == 1) rankLabel = "🥇 1st";
            else if (rank == 2) rankLabel = "🥈 2nd";
            else if (rank == 3) rankLabel = "🥉 3rd";
            else rankLabel = rank + "th";

            tableModel.addRow(new Object[]{
                rankLabel,
                entry.getName(),
                entry.getUsername(),
                entry.getMaxWpm() + " WPM",
                String.format("%.1f%%", entry.getAvgAccuracy())
            });
            rank++;
        }
    }
}
