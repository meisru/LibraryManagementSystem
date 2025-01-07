package com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.library.dao.DatabaseConnection;
import com.library.entities.User;

public class Library extends JFrame {
    public static final Font H1 = new Font("Serif", Font.BOLD, 36);
    public static final Font H2 = new Font("Serif", Font.PLAIN, 24);
    public static final Font H3 = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font body = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Color gray = Color.decode("#F5F5F5");
    public static final Color darkGray = Color.decode("#2C2C2C");

    private JPanel mainPanel; 
    private CardLayout cardLayout;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel emailLabel;
    private JTextField usernameField1;
    private JPasswordField passwordField1;
    private JTextField usernameField2;
    private JPasswordField passwordField2;
    private JTextField emailField;
    private JButton loginButton;
    private JLabel registerLabel;

    public Library() {
        setTitle("Library Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("assets/Book.png");
        setIconImage(icon.getImage());

        // Main panel 
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Login and Register panels
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registerPanel, "Register");

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10); 

        titleLabel = new JLabel("Welcome, Reader! 📚");
        titleLabel.setFont(H1);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2; 
        c.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, c);

        JPanel secondPanel = new JPanel();
        secondPanel.setBackground(Color.WHITE);
        secondPanel.setLayout(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(5, 5, 5, 5);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(H2);
        c2.gridx = 0;
        c2.gridy = 0;
        secondPanel.add(usernameLabel, c2);

        usernameField1 = new JTextField(20);
        styleTextField(usernameField1);
        c2.gridx = 1;
        c2.gridy = 0;
        secondPanel.add(usernameField1, c2);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(H2);
        c2.gridx = 0;
        c2.gridy = 1;
        secondPanel.add(passwordLabel, c2);

        passwordField1 = new JPasswordField(20);
        styleTextField(passwordField1);
        c2.gridx = 1;
        c2.gridy = 1;
        secondPanel.add(passwordField1, c2);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        panel.add(secondPanel, c);

        JPanel thirdPanel = new JPanel();
        thirdPanel.setBackground(Color.WHITE);
        thirdPanel.setLayout(new BoxLayout(thirdPanel, BoxLayout.Y_AXIS));
        thirdPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginListener());
        styleButton(loginButton);

        JButton registerButton = new JButton("New User?");
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "Register"));
        styleButton(registerButton);
        registerButton.setFont(body);
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(darkGray);

        thirdPanel.add(loginButton);
        thirdPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        thirdPanel.add(registerButton);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(thirdPanel, c);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        registerLabel = new JLabel("Create a new account");
        registerLabel.setFont(H1);
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(registerLabel, c);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(H2);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel.add(usernameLabel, c);

        usernameField2 = new JTextField(20);
        styleTextField(usernameField2);
        c.gridx = 1;
        c.gridy = 1;
        panel.add(usernameField2, c);

        emailLabel = new JLabel("Email:");
        emailLabel.setFont(H2);
        c.gridx = 0;
        c.gridy = 2;
        panel.add(emailLabel, c);

        emailField = new JTextField(20);
        styleTextField(emailField);
        c.gridx = 1;
        c.gridy = 2;
        panel.add(emailField, c);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(H2);
        c.gridx = 0;
        c.gridy = 3;
        panel.add(passwordLabel, c);

        passwordField2 = new JPasswordField(20);
        styleTextField(passwordField2);
        c.gridx = 1;
        c.gridy = 3;
        panel.add(passwordField2, c);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new registerListener());
        styleButton(registerButton);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        panel.add(registerButton, c);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        styleButton(backButton);
        backButton.setFont(body);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(darkGray);
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        panel.add(backButton, c);

        return panel;
    }

    public static void styleButton(JButton button) {
        button.setFont(H3);
        button.setBackground(darkGray);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void styleTextField(JTextField textField) {
        textField.setFont(H2);
        textField.setBackground(gray);
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void styleTextField(JPasswordField textField) {
        textField.setFont(H2);
        textField.setBackground(gray);
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField1.getText();
            String password = new String(passwordField1.getPassword());
            // Check if the user exists in the database
            if (DatabaseConnection.validateUser(username, password)) {
                int userID = DatabaseConnection.getUserId(username);
                String role = DatabaseConnection.getUserRole(username);
                User loggedInUser = new User(userID, username, password, role);
                JOptionPane.showMessageDialog(null, "Login successful!");
                dispose();
                if (role.equals("admin")) {
                    new AdminDashboard(loggedInUser);
                } else {
                    new UserDashboard(loggedInUser);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
            }
        }
    }

    class registerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField2.getText();
            String password = new String(passwordField2.getPassword());
            String email = emailField.getText();
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }
            if (DatabaseConnection.userExists(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Try another one.");
                return;
            }
            if (password.length() < 8) {
                JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long.");
                return;
            }
            if (!email.matches("^(.+)@(.+)$")) {
                JOptionPane.showMessageDialog(null, "Invalid email address.");
                return;
            }
            // Save user to the database
            User user = new User(username, password, email);
            user.setRole("user");
            if (DatabaseConnection.createUser(user)) {
                JOptionPane.showMessageDialog(null, "User registered successfully!");
                dispose();
                    if (user.getRole().equals("admin")) {
                        new AdminDashboard(user);
                    } else {
                        new UserDashboard(user);
                    }
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed. Try again.");
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Library());
    }
}
