package com.typetrack.ui;

import com.typetrack.model.User;
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
 * Screen presenting the user login form.
 */
public class LoginPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(UIStyle.COLOR_BG);
        setLayout(new GridBagLayout());
        
        initializeUI();
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Row
        JLabel lblTitle = ComponentsHelper.createTitleLabel("TypeTrack");
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(lblTitle, gbc);

        // Subtitle Row
        JLabel lblSubtitle = ComponentsHelper.createBodyLabel("Sign in to analyze your typing speed");
        lblSubtitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(lblSubtitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

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

        // Buttons Container
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        
        JButton btnLogin = ComponentsHelper.createButton("Log In");
        add(btnLogin, gbc);

        // Forgot Password navigation
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 8, 8, 8);
        JButton btnForgotLink = ComponentsHelper.createSecondaryButton("Forgot Password?");
        add(btnForgotLink, gbc);

        // Register navigation
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 8, 8, 8);
        JButton btnRegisterLink = ComponentsHelper.createSecondaryButton("Need an Account? Register");
        add(btnRegisterLink, gbc);

        // Theme toggle button
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 8, 8, 8);
        JButton btnThemeToggle = ComponentsHelper.createThemeToggleButton(mainFrame);
        add(btnThemeToggle, gbc);

        // Add Listeners
        btnLogin.addActionListener(e -> handleLogin());
        btnForgotLink.addActionListener(e -> mainFrame.showPanel("ForgotPassword"));
        btnRegisterLink.addActionListener(e -> mainFrame.showPanel("Register"));
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = mainFrame.getAuthService().login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Reset fields
        txtUsername.setText("");
        txtPassword.setText("");
        
        mainFrame.loginUser(user);
    }
}
