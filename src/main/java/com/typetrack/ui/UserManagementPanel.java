package com.typetrack.ui;

import com.typetrack.model.User;
import com.typetrack.service.AdminService;
import com.typetrack.util.PasswordHasher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final AdminService adminService;
    private DefaultTableModel tableModel;
    private JTable userTable;

    public UserManagementPanel() {
        this.adminService = new AdminService();
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyle.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        // Title
        JLabel titleLabel = ComponentsHelper.createTitleLabel("User Management");
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Username", "Name", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setBackground(UIStyle.COLOR_CARD);
        userTable.setForeground(UIStyle.COLOR_TEXT);
        userTable.setRowHeight(30);
        userTable.getTableHeader().setBackground(UIStyle.COLOR_BG);
        userTable.getTableHeader().setForeground(UIStyle.COLOR_TEXT);
        userTable.getTableHeader().setFont(UIStyle.FONT_BODY_BOLD);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0x33, 0x41, 0x55)));
        scrollPane.getViewport().setBackground(UIStyle.COLOR_BG);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(UIStyle.COLOR_BG);

        JButton btnResetPassword = ComponentsHelper.createSecondaryButton("Reset Password");
        JButton btnDeleteUser = ComponentsHelper.createButton("Delete User");
        btnDeleteUser.setBackground(new Color(0xEF, 0x44, 0x44)); // Red color for delete
        btnDeleteUser.setForeground(Color.WHITE);

        btnResetPassword.addActionListener(e -> resetPassword());
        btnDeleteUser.addActionListener(e -> deleteUser());

        actionPanel.add(btnResetPassword);
        actionPanel.add(btnDeleteUser);
        
        JButton btnRefresh = ComponentsHelper.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        actionPanel.add(btnRefresh);

        add(actionPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<User> users = adminService.getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getRole()
            });
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String role = (String) tableModel.getValueAt(selectedRow, 3);

        if ("ADMIN".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(this, "Cannot delete an administrator account.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete user '" + username + "'? This will also delete their test results.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminService.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        String newPassword = JOptionPane.showInputDialog(this, "Enter new password for '" + username + "':", "Reset Password", JOptionPane.PLAIN_MESSAGE);
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String hashed = PasswordHasher.hash(newPassword);
            if (adminService.forcePasswordReset(userId, hashed)) {
                JOptionPane.showMessageDialog(this, "Password reset successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
