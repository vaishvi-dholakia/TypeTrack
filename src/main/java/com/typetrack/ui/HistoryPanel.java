package com.typetrack.ui;

import com.typetrack.model.Result;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Screen presenting an interactive table of previous test results.
 * Supports sorting, category filtering, and live metric aggregations.
 */
public class HistoryPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    // Filter component
    private JComboBox<String> comboSpeedFilter;

    // Live Aggregation Labels
    private JLabel lblBestSpeed;
    private JLabel lblAvgSpeed;
    private JLabel lblAvgAcc;

    public HistoryPanel(MainFrame mainFrame) {
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
        
        JLabel lblTitle = ComponentsHelper.createTitleLabel("Performance History");
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setBackground(UIStyle.COLOR_BG);
        rightHeader.add(ComponentsHelper.createThemeToggleButton(mainFrame));
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN WORKSPACE PANEL (Center) ---
        JPanel workspacePanel = new JPanel(new BorderLayout(0, 15));
        workspacePanel.setBackground(UIStyle.COLOR_BG);

        // Row 0: Filters Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setBackground(UIStyle.COLOR_BG);

        JLabel lblFilter = new JLabel("Filter Speed:");
        lblFilter.setFont(UIStyle.FONT_BODY_BOLD);
        lblFilter.setForeground(UIStyle.COLOR_TEXT);
        filterBar.add(lblFilter);

        String[] filters = {"All Speeds", "Slow (< 30 WPM)", "Medium (30 - 60 WPM)", "Fast (> 60 WPM)"};
        comboSpeedFilter = new JComboBox<>(filters);
        comboSpeedFilter.setFont(UIStyle.FONT_BODY);
        comboSpeedFilter.setBackground(UIStyle.COLOR_CARD);
        comboSpeedFilter.setForeground(UIStyle.COLOR_TEXT);
        comboSpeedFilter.addActionListener(e -> applyFilterAndMetrics());
        filterBar.add(comboSpeedFilter);

        workspacePanel.add(filterBar, BorderLayout.NORTH);

        // Row 1: Table Setup
        String[] columnNames = {"Test #", "Speed (WPM)", "Accuracy", "Mistakes", "Time Taken"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 3) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(tableSorter);
        
        // Dark-Mode Table Styling
        table.setBackground(UIStyle.COLOR_CARD);
        table.setForeground(UIStyle.COLOR_TEXT);
        table.setGridColor(new Color(0x33, 0x41, 0x55)); // Slate 700
        table.setFont(UIStyle.FONT_BODY);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(UIStyle.COLOR_ACCENT);
        table.setSelectionForeground(UIStyle.COLOR_TEXT);

        table.getTableHeader().setBackground(UIStyle.COLOR_BG);
        table.getTableHeader().setForeground(UIStyle.COLOR_TEXT);
        table.getTableHeader().setFont(UIStyle.FONT_BODY_BOLD);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x33, 0x41, 0x55)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1));
        scrollPane.getViewport().setBackground(UIStyle.COLOR_BG);
        workspacePanel.add(scrollPane, BorderLayout.CENTER);

        // Row 2: Live Metrics Aggregation Panel
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        metricsPanel.setBackground(UIStyle.COLOR_BG);
        
        metricsPanel.add(createMiniStatCard("Filtered Best Speed", lblBestSpeed = new JLabel("0 WPM")));
        metricsPanel.add(createMiniStatCard("Filtered Avg Speed", lblAvgSpeed = new JLabel("0.0 WPM")));
        metricsPanel.add(createMiniStatCard("Filtered Avg Accuracy", lblAvgAcc = new JLabel("0.0%")));
        
        workspacePanel.add(metricsPanel, BorderLayout.SOUTH);

        add(workspacePanel, BorderLayout.CENTER);

        // --- FOOTER PANEL (South) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UIStyle.COLOR_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnBack = ComponentsHelper.createSecondaryButton("Back to Dashboard");
        btnBack.setPreferredSize(new Dimension(200, 45));
        btnBack.addActionListener(e -> mainFrame.showPanel("Dashboard"));
        bottomPanel.add(btnBack, BorderLayout.WEST);

        JButton btnExport = ComponentsHelper.createButton("Save Records");
        btnExport.setPreferredSize(new Dimension(200, 45));
        
        javax.swing.JPopupMenu exportMenu = new javax.swing.JPopupMenu();
        
        javax.swing.JMenuItem itemCSV = new javax.swing.JMenuItem("Export as CSV");
        itemCSV.addActionListener(e -> exportToCSV());
        
        javax.swing.JMenuItem itemTXT = new javax.swing.JMenuItem("Export as TXT");
        itemTXT.addActionListener(e -> exportToTXT());
        
        javax.swing.JMenuItem itemPDF = new javax.swing.JMenuItem("Export as PDF (Print)");
        itemPDF.addActionListener(e -> exportToPDF());
        
        exportMenu.add(itemCSV);
        exportMenu.add(itemTXT);
        exportMenu.add(itemPDF);

        btnExport.addActionListener(e -> {
            exportMenu.show(btnExport, 0, btnExport.getHeight());
        });
        bottomPanel.add(btnExport, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createMiniStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIStyle.COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIStyle.FONT_SMALL);
        lblTitle.setForeground(UIStyle.COLOR_MUTED);
        card.add(lblTitle, BorderLayout.NORTH);

        valueLabel.setFont(UIStyle.FONT_BODY_BOLD);
        valueLabel.setForeground(UIStyle.COLOR_TEXT);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void exportToPDF() {
        try {
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, 
                new java.text.MessageFormat("TypeTracker - Performance History"), 
                new java.text.MessageFormat("Page - {0}"));
            if (complete) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Export completed successfully!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.awt.print.PrinterException pe) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Export failed: " + pe.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToCSV() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        fileChooser.setSelectedFile(new java.io.File("typetracker_history.csv"));
        if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.print(tableModel.getValueAt(i, j));
                        if (j < tableModel.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }
                javax.swing.JOptionPane.showMessageDialog(this, "Successfully saved as CSV!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToTXT() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save as TXT");
        fileChooser.setSelectedFile(new java.io.File("typetracker_history.txt"));
        if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                writer.println("TypeTracker - Performance History");
                writer.println("--------------------------------------------------------------------------------");
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.printf("%-15s", tableModel.getColumnName(i));
                }
                writer.println();
                writer.println("--------------------------------------------------------------------------------");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.printf("%-15s", tableModel.getValueAt(i, j));
                    }
                    writer.println();
                }
                javax.swing.JOptionPane.showMessageDialog(this, "Successfully saved as TXT!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyFilterAndMetrics() {
        int filterIndex = comboSpeedFilter.getSelectedIndex();
        tableModel.setRowCount(0);

        List<Result> allResults = mainFrame.getStatsManager().getResults();
        List<Result> filteredResults = new ArrayList<>();

        int displayIndex = 1;
        for (Result r : allResults) {
            boolean matches = false;
            int wpm = r.getWpm();
            
            switch (filterIndex) {
                case 0: // All Speeds
                    matches = true;
                    break;
                case 1: // Slow (< 30 WPM)
                    matches = (wpm < 30);
                    break;
                case 2: // Medium (30 - 60 WPM)
                    matches = (wpm >= 30 && wpm <= 60);
                    break;
                case 3: // Fast (> 60 WPM)
                    matches = (wpm > 60);
                    break;
            }

            if (matches) {
                filteredResults.add(r);
                tableModel.addRow(new Object[]{
                    displayIndex++,
                    r.getWpm(),
                    String.format("%.1f%%", r.getAccuracy()),
                    r.getMistakes(),
                    String.format("%.2fs", r.getTimeTakenSeconds())
                });
            }
        }

        // Calculate and render filtered metrics
        if (filteredResults.isEmpty()) {
            lblBestSpeed.setText("0 WPM");
            lblAvgSpeed.setText("0.0 WPM");
            lblAvgAcc.setText("0.0%");
        } else {
            int maxWpm = 0;
            double sumWpm = 0;
            double sumAcc = 0;
            
            for (Result r : filteredResults) {
                if (r.getWpm() > maxWpm) {
                    maxWpm = r.getWpm();
                }
                sumWpm += r.getWpm();
                sumAcc += r.getAccuracy();
            }

            double avgWpm = sumWpm / filteredResults.size();
            double avgAcc = sumAcc / filteredResults.size();

            lblBestSpeed.setText(maxWpm + " WPM");
            lblAvgSpeed.setText(String.format("%.1f WPM", avgWpm));
            lblAvgAcc.setText(String.format("%.1f%%", avgAcc));
        }
    }

    /**
     * Refreshes the table rows from StatisticsManager.
     */
    public void refresh() {
        comboSpeedFilter.setSelectedIndex(0); // Reset filter to All
        applyFilterAndMetrics();
    }
}
