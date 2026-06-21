package com.clinic.ui;

import com.clinic.dao.UserDAO;
import com.clinic.model.User;
import com.clinic.util.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblStatus;

    public LoginForm() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Clinic Management System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 340);
        setResizable(false);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(30, 60, 114));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("CLINIC MANAGEMENT SYSTEM", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.ipady = 16;
        main.add(title, gbc);

        JLabel sub = new JLabel("Please sign in to continue", SwingConstants.CENTER);
        sub.setForeground(new Color(180, 210, 255));
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 1; gbc.ipady = 0;
        main.add(sub, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        main.add(lblUser, gbc);

        txtUsername = new JTextField(18);
        gbc.gridx = 1;
        main.add(txtUsername, gbc);

        // Password
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        main.add(lblPass, gbc);

        txtPassword = new JPasswordField(18);
        gbc.gridx = 1;
        main.add(txtPassword, gbc);

        // Status label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(new Color(255, 100, 100));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        main.add(lblStatus, gbc);

        // Login button
        btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(255, 165, 0));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true); btnLogin.setBorderPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        main.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());

        add(main);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Username and password are required.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.authenticate(username, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                new DashboardForm().setVisible(true);
                dispose();
            } else {
                lblStatus.setText("Invalid username or password.");
                txtPassword.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
