package com.election.ui;

import com.election.dao.UserDAO;
import com.election.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField txtUsername, txtFullName, txtEmail;
    private JPasswordField txtPassword, txtConfirm;
    private UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("Register as Voter");
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(25, 42, 86));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(39, 174, 96));
        headerPanel.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel lblTitle = new JLabel("📝  VOTER REGISTRATION", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(25, 42, 86));
        form.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        String[] labels = {"Full Name", "Username", "Email", "Password", "Confirm Password"};
        JComponent[] fields = {
            txtFullName   = new JTextField(),
            txtUsername   = new JTextField(),
            txtEmail      = new JTextField(),
            txtPassword   = new JPasswordField(),
            txtConfirm    = new JPasswordField()
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i * 2;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(new Color(180, 200, 255));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            form.add(lbl, gbc);

            gbc.gridy = i * 2 + 1;
            styleField(fields[i]);
            form.add(fields[i], gbc);
        }

        gbc.gridy = 10; gbc.insets = new Insets(18, 5, 5, 5);
        JButton btnRegister = new JButton("CREATE ACCOUNT");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(new Color(39, 174, 96));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnRegister, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        add(mainPanel);

        btnRegister.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        String fullName  = txtFullName.getText().trim();
        String username  = txtUsername.getText().trim();
        String email     = txtEmail.getText().trim();
        String password  = new String(txtPassword.getPassword()).trim();
        String confirm   = new String(txtConfirm.getPassword()).trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMsg("All fields are required.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            showMsg("Passwords do not match.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            showMsg("Password must be at least 6 characters.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.usernameExists(username)) {
            showMsg("Username already exists.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(0, username, password, fullName, email, "VOTER", false);
        if (userDAO.registerVoter(user)) {
            showMsg("Registration successful! You can now log in.", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showMsg("Registration failed. Please try again.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleField(JComponent f) {
        ((JTextField) f).setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(300, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 110, 180), 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    private void showMsg(String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, "Registration", type);
    }
}
