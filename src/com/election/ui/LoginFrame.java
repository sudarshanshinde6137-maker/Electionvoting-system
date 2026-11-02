package com.election.ui;

import com.election.dao.UserDAO;
import com.election.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Election Voting System - Login");
        setSize(420, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        // Main panel with gradient feel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 42, 86));

        // ── Header ──
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 33, 70));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel("🗳  ELECTION VOTING SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        // ── Form Panel ──
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(25, 42, 86));
        formPanel.setBorder(new EmptyBorder(30, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(new Color(180, 200, 255));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField();
        styleTextField(txtUsername);
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridy = 2;
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(new Color(180, 200, 255));
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(lblPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        formPanel.add(txtPassword, gbc);

        // Buttons
        gbc.gridy = 4; gbc.insets = new Insets(20, 5, 5, 5);
        JButton btnLogin = createButton("LOGIN", new Color(52, 152, 219));
        formPanel.add(btnLogin, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(5, 5, 5, 5);
        JButton btnRegister = createButton("REGISTER AS VOTER", new Color(39, 174, 96));
        formPanel.add(btnRegister, gbc);

        // Footer
        JLabel lblFooter = new JLabel("Secure • Transparent • Fair", SwingConstants.CENTER);
        lblFooter.setForeground(new Color(100, 130, 180));
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setBorder(new EmptyBorder(0, 0, 15, 0));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(lblFooter, BorderLayout.SOUTH);
        add(mainPanel);

        // ── Actions ──
        btnLogin.addActionListener(e -> handleLogin());
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
        });

        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            showError("Invalid username or password.");
            txtPassword.setText("");
            return;
        }

        dispose();
        if (user.isAdmin()) {
            new AdminDashboard(user).setVisible(true);
        } else {
            new VoterDashboard(user).setVisible(true);
        }
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(300, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 110, 180), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}
