package com.typetrack.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Screen presenting the registration form with security question and strong password validation.
 */
public class RegisterPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> comboSecurityQuestion;
    private JTextField txtSecurityAnswer;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.COLOR_BG);
        setLayout(new GridBagLayout());

        initializeUI();
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = ComponentsHelper.createTitleLabel("Create Account");
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Name
        JLabel lblName = ComponentsHelper.createBodyLabel("Full Name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblName, gbc);

        txtName = ComponentsHelper.createTextField(20);
        gbc.gridx = 1;
        add(txtName, gbc);

        // Username
        JLabel lblUsername = ComponentsHelper.createBodyLabel("Username");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblUsername, gbc);

        txtUsername = ComponentsHelper.createTextField(20);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        JLabel lblPassword = ComponentsHelper.createBodyLabel("Password");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lblPassword, gbc);

        txtPassword = ComponentsHelper.createPasswordField(20);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Confirm Password
        JLabel lblConfirmPassword = ComponentsHelper.createBodyLabel("Confirm Password");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lblConfirmPassword, gbc);

        txtConfirmPassword = ComponentsHelper.createPasswordField(20);
        gbc.gridx = 1;
        add(txtConfirmPassword, gbc);

        // Security Question
        JLabel lblSecurityQuestion = ComponentsHelper.createBodyLabel("Security Question");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(lblSecurityQuestion, gbc);

        String[] questions = {
            "What was the name of your first pet?",
            "What was your childhood nickname?",
            "What is your favorite color?",
            "What city were you born in?",
            "What is your mother's maiden name?"
        };
        comboSecurityQuestion = new JComboBox<>(questions);
        comboSecurityQuestion.setFont(UIStyle.FONT_BODY);
        comboSecurityQuestion.setBackground(UIStyle.COLOR_CARD);
        comboSecurityQuestion.setForeground(UIStyle.COLOR_TEXT);
        gbc.gridx = 1;
        add(comboSecurityQuestion, gbc);

        // Security Answer
        JLabel lblSecurityAnswer = ComponentsHelper.createBodyLabel("Answer");
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(lblSecurityAnswer, gbc);

        txtSecurityAnswer = ComponentsHelper.createTextField(20);
        gbc.gridx = 1;
        add(txtSecurityAnswer, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);

        JButton btnRegister = ComponentsHelper.createButton("Register");
        add(btnRegister, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(10, 8, 8, 8);
        JButton btnLoginLink = ComponentsHelper.createSecondaryButton("Already have an account? Log In");
        add(btnLoginLink, gbc);

        // Theme toggle button
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 8, 8, 8);
        JButton btnThemeToggle = ComponentsHelper.createThemeToggleButton(mainFrame);
        add(btnThemeToggle, gbc);

        // Add Listeners
        btnRegister.addActionListener(e -> handleRegister());
        btnLoginLink.addActionListener(e -> mainFrame.showPanel("Login"));
    }

    private void handleRegister() {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String securityQuestion = (String) comboSecurityQuestion.getSelectedItem();
        String securityAnswer = txtSecurityAnswer.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || securityAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!mainFrame.getAuthService().isValidPassword(password)) {
            String msg = "Weak Password! Password must contain:\n"
                       + "- At least 8 characters\n"
                       + "- At least 1 uppercase letter\n"
                       + "- At least 1 lowercase letter\n"
                       + "- At least 1 number\n"
                       + "- At least 1 special character (@$!%*?&)";
            JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        com.typetrack.model.User registeredUser = mainFrame.getAuthService().register(name, username, password, securityQuestion, securityAnswer);
        if (registeredUser == null) {
            JOptionPane.showMessageDialog(this, "Username is already taken or an error occurred.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Reset and redirect
        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtSecurityAnswer.setText("");
        
        mainFrame.showPanel("Login");
    }
}
