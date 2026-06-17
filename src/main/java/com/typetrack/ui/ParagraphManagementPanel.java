package com.typetrack.ui;

import com.typetrack.model.Paragraph;
import com.typetrack.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ParagraphManagementPanel extends JPanel {
    private final AdminService adminService;
    private DefaultTableModel tableModel;
    private JTable paragraphTable;

    public ParagraphManagementPanel() {
        this.adminService = new AdminService();
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        // Title
        JLabel titleLabel = ComponentsHelper.createTitleLabel("Paragraph Management");
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Category", "Difficulty", "Content Snippet"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paragraphTable = new JTable(tableModel);
        paragraphTable.setFillsViewportHeight(true);
        paragraphTable.setBackground(UIStyle.COLOR_CARD);
        paragraphTable.setForeground(UIStyle.COLOR_TEXT);
        paragraphTable.setRowHeight(30);
        paragraphTable.getTableHeader().setBackground(UIStyle.COLOR_BG);
        paragraphTable.getTableHeader().setForeground(UIStyle.COLOR_TEXT);
        paragraphTable.getTableHeader().setFont(UIStyle.FONT_BODY_BOLD);

        // Set column widths
        paragraphTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        paragraphTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        paragraphTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        paragraphTable.getColumnModel().getColumn(3).setPreferredWidth(400);

        JScrollPane scrollPane = new JScrollPane(paragraphTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)));
        scrollPane.getViewport().setBackground(UIStyle.COLOR_BG);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(UIStyle.COLOR_BG);

        JButton btnAdd = ComponentsHelper.createButton("Add Paragraph");
        JButton btnDelete = ComponentsHelper.createSecondaryButton("Delete Paragraph");
        btnDelete.setBackground(new Color(0xEF, 0x44, 0x44)); // Optional red tint if desired, but using secondary button style
        btnDelete.setForeground(Color.WHITE); // Just a quick override if we want it to look destructive

        btnAdd.addActionListener(e -> addParagraph());
        btnDelete.addActionListener(e -> deleteParagraph());

        actionPanel.add(btnAdd);
        actionPanel.add(btnDelete);
        
        JButton btnRefresh = ComponentsHelper.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        actionPanel.add(btnRefresh);

        add(actionPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Paragraph> paragraphs = adminService.getAllParagraphs();
        for (Paragraph p : paragraphs) {
            String snippet = p.getText().length() > 50 
                ? p.getText().substring(0, 50) + "..." 
                : p.getText();
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getCategory(),
                p.getDifficulty(),
                snippet
            });
        }
    }

    private void addParagraph() {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        formPanel.add(new JLabel("Category:"));
        JTextField categoryField = new JTextField();
        formPanel.add(categoryField);
        
        formPanel.add(new JLabel("Difficulty (EASY/MEDIUM/HARD):"));
        JComboBox<String> difficultyCombo = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        formPanel.add(difficultyCombo);
        
        formPanel.add(new JLabel("Content:"));
        JTextArea contentArea = new JTextArea(5, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        formPanel.add(scrollPane);
        
        // Use a JDialog or JOptionPane
        // Since content can be long, adjust size
        formPanel.setPreferredSize(new Dimension(400, 200));
        
        int result = JOptionPane.showConfirmDialog(this, formPanel, "Add New Paragraph", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String category = categoryField.getText().trim();
            String difficulty = (String) difficultyCombo.getSelectedItem();
            String content = contentArea.getText().trim();
            
            if (category.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category and Content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (adminService.addParagraph(content, category, difficulty)) {
                JOptionPane.showMessageDialog(this, "Paragraph added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add paragraph.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteParagraph() {
        int selectedRow = paragraphTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a paragraph to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this paragraph?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminService.deleteParagraph(id)) {
                JOptionPane.showMessageDialog(this, "Paragraph deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete paragraph.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
