package com.typetrack.ui;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Screen handling the multi-step password recovery workflow via Security Questions.
 */
public class ForgotPasswordPanel extends JPanel {
    private final MainFrame mainFrame;
    private final CardLayout wizardLayout;
    private final JPanel wizardPanel;
    
    // State variables
    private String recoveredUsername = "";

    // Step 1 UI
    private JTextField txtUsername;
    
    // Step 2 UI
    private JLabel lblSecurityQuestion;
    private JTextField txtSecurityAnswer;
    
    // Step 3 UI
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    public ForgotPasswordPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.COLOR_BG);
        
        wizardLayout = new CardLayout();
        wizardPanel = new JPanel(wizardLayout);
        wizardPanel.setBackground(UIStyle.COLOR_BG);

        wizardPanel.add(createStep1Panel(), "Step1");
        wizardPanel.add(createStep2Panel(), "Step2");
        wizardPanel.add(createStep3Panel(), "Step3");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        add(wizardPanel, gbc);
    }

    private JPanel createStep1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.COLOR_BG);
        GridBagConstraints gbc = createGBC();

        JLabel lblTitle = ComponentsHelper.createTitleLabel("Password Recovery");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        JLabel lblUsername = ComponentsHelper.createBodyLabel("Enter your Username");
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        panel.add(lblUsername, gbc);

        txtUsername = ComponentsHelper.createTextField(20);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        JButton btnNext = ComponentsHelper.createButton("Next");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(btnNext, gbc);

        JButton btnCancel = ComponentsHelper.createSecondaryButton("Cancel");
        gbc.gridy = 3; gbc.insets = new Insets(10, 8, 8, 8);
        panel.add(btnCancel, gbc);

        btnNext.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String question = mainFrame.getAuthService().getSecurityQuestion(username);
            if (question == null || question.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username not found or no security question configured.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            recoveredUsername = username;
            lblSecurityQuestion.setText(question);
            wizardLayout.show(wizardPanel, "Step2");
        });

        btnCancel.addActionListener(e -> resetAndGoBack());
        return panel;
    }

    private JPanel createStep2Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.COLOR_BG);
        GridBagConstraints gbc = createGBC();

        JLabel lblTitle = ComponentsHelper.createTitleLabel("Answer Security Question");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        lblSecurityQuestion = ComponentsHelper.createBodyLabel("Loading question...");
        lblSecurityQuestion.setFont(UIStyle.FONT_BODY_BOLD);
        gbc.gridy = 1;
        panel.add(lblSecurityQuestion, gbc);

        txtSecurityAnswer = ComponentsHelper.createTextField(20);
        gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(txtSecurityAnswer, gbc);

        JButton btnVerify = ComponentsHelper.createButton("Verify Answer");
        gbc.gridy = 3; gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(btnVerify, gbc);

        JButton btnCancel = ComponentsHelper.createSecondaryButton("Cancel");
        gbc.gridy = 4; gbc.insets = new Insets(10, 8, 8, 8);
        panel.add(btnCancel, gbc);

        btnVerify.addActionListener(e -> {
            String answer = txtSecurityAnswer.getText().trim();
            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an answer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean valid = mainFrame.getAuthService().verifySecurityAnswer(recoveredUsername, answer);
            if (!valid) {
                JOptionPane.showMessageDialog(this, "Incorrect answer. Please try again.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            wizardLayout.show(wizardPanel, "Step3");
        });

        btnCancel.addActionListener(e -> resetAndGoBack());
        return panel;
    }

    private JPanel createStep3Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.COLOR_BG);
        GridBagConstraints gbc = createGBC();

        JLabel lblTitle = ComponentsHelper.createTitleLabel("Set New Password");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        JLabel lblNewPass = ComponentsHelper.createBodyLabel("New Password");
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        panel.add(lblNewPass, gbc);

        txtNewPassword = ComponentsHelper.createPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtNewPassword, gbc);

        JLabel lblConfirmPass = ComponentsHelper.createBodyLabel("Confirm Password");
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(lblConfirmPass, gbc);

        txtConfirmPassword = ComponentsHelper.createPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtConfirmPassword, gbc);

        JButton btnReset = ComponentsHelper.createButton("Reset Password");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(btnReset, gbc);

        JButton btnCancel = ComponentsHelper.createSecondaryButton("Cancel");
        gbc.gridy = 4; gbc.insets = new Insets(10, 8, 8, 8);
        panel.add(btnCancel, gbc);

        btnReset.addActionListener(e -> {
            String password = new String(txtNewPassword.getPassword()).trim();
            String confirm = new String(txtConfirmPassword.getPassword()).trim();
            
            if (password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!mainFrame.getAuthService().isValidPassword(password)) {
                String msg = "Weak Password! Must contain 8+ chars, upper, lower, number, and special character.";
                JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            boolean success = mainFrame.getAuthService().resetPassword(recoveredUsername, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password successfully reset! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetAndGoBack();
            } else {
                JOptionPane.showMessageDialog(this, "An error occurred while resetting the password.", "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> resetAndGoBack());
        return panel;
    }

    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void resetAndGoBack() {
        recoveredUsername = "";
        txtUsername.setText("");
        txtSecurityAnswer.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");
        wizardLayout.show(wizardPanel, "Step1");
        mainFrame.showPanel("Login");
    }
}
