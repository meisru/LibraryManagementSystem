package com.library.ui;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.library.dao.DatabaseConnection;
import com.library.entities.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AdminDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"ID", "Title", "Author", "Genre", "Year"};
    private JComboBox<String> genreComboBox;
    private JComboBox<String> authorComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTextField searchField;

    public AdminDashboard(User user) {
        setTitle("Library Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(cardLayout);

        // home
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        JLabel welcomeLabel = new JLabel("Welcome to the Library, " + user.getUsername() + "!");
        welcomeLabel.setFont(Library.H2);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        homePanel.add(welcomeLabel);

        // categorization
        JPanel categorizationPanel = new JPanel();
        
        JLabel genreLabel = new JLabel("Genre: ");
        String[] genres = DatabaseConnection.getGenres();
        genreComboBox = new JComboBox<>(genres);
        genreComboBox.insertItemAt(null, 0);
        genreComboBox.setSelectedIndex(0);
        genreComboBox.addActionListener(new comboBoxListener());

        JLabel authorLabel = new JLabel("Author: ");
        String[] authors = DatabaseConnection.getAuthors();
        authorComboBox = new JComboBox<>(authors);
        authorComboBox.insertItemAt(null, 0);
        authorComboBox.setSelectedIndex(0);
        authorComboBox.addActionListener(new comboBoxListener());

        JLabel yearLabel = new JLabel("Year: ");
        Integer[] years = DatabaseConnection.getYears();
        yearComboBox = new JComboBox<>(years);
        yearComboBox.insertItemAt(null, 0);
        yearComboBox.setSelectedIndex(0);
        yearComboBox.addActionListener(new comboBoxListener());
        
        categorizationPanel.add(genreLabel);
        categorizationPanel.add(genreComboBox);
        categorizationPanel.add(authorLabel);
        categorizationPanel.add(authorComboBox);
        categorizationPanel.add(yearLabel);
        categorizationPanel.add(yearComboBox);

        // search
        searchField = new JTextField(30);
        searchField.setText("Search for a book by title, author, or genre:");
        searchField.setBackground(Library.gray);
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                searchField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search for a book by title, author, or genre:");
                }
            }
        });
        searchField.addActionListener(new searchListener());
        categorizationPanel.add(searchField);

        homePanel.add(categorizationPanel);

        // books table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
            // disable table editing
            return false;
            }
        };

        Object[][] bookData = DatabaseConnection.displayBooks(null, null, null);
        tableModel.setDataVector(bookData, columnNames);
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        homePanel.add(scrollPane);

        // add a book button
        JButton addBookButton = new JButton("Add a Book");
        addBookButton.setFont(Library.body);
        addBookButton.setBackground(Library.darkGray);
        addBookButton.setForeground(Color.WHITE);
        addBookButton.setFocusable(false);
        addBookButton.setBorderPainted(false);
        addBookButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter the title of the book:");
            String author = JOptionPane.showInputDialog("Enter the author of the book:");
            String genre = JOptionPane.showInputDialog("Enter the genre of the book:");
            int year = Integer.parseInt(JOptionPane.showInputDialog("Enter the year of publication:"));
            int copies = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of copies:"));
            int available = Integer.parseInt(JOptionPane.showInputDialog("Is the book available? (1 for yes, 0 for no)"));
            if (DatabaseConnection.addBook(title, author, genre, year, copies, available)) {
                JOptionPane.showMessageDialog(null, "Book added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error adding book.");
            }
        });

        // remove book button
        JButton removeBookButton = new JButton("Remove a Book"); 
        removeBookButton.setFont(Library.body);
        removeBookButton.setBackground(Library.darkGray);
        removeBookButton.setForeground(Color.WHITE);
        removeBookButton.setFocusable(false);
        removeBookButton.setBorderPainted(false);
        removeBookButton.addActionListener(e -> {
            int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter the ID of the book you want to remove:"));
            if (DatabaseConnection.deleteBook(bookId)) {
                JOptionPane.showMessageDialog(null, "Book removed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error removing book.");
            }
        });      

        // update book
        JButton updateBookButton = new JButton("Update a Book");
        updateBookButton.setFont(Library.body);
        updateBookButton.setBackground(Library.darkGray);
        updateBookButton.setForeground(Color.WHITE);
        updateBookButton.setFocusable(false);
        updateBookButton.setBorderPainted(false);
        updateBookButton.addActionListener(e -> {
            int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter the ID of the book you want to update:"));
            String title = JOptionPane.showInputDialog("Enter the new title of the book:");
            String author = JOptionPane.showInputDialog("Enter the new author of the book:");
            String genre = JOptionPane.showInputDialog("Enter the new genre of the book:");
            int year = Integer.parseInt(JOptionPane.showInputDialog("Enter the new year of publication:"));
            int copies = Integer.parseInt(JOptionPane.showInputDialog("Enter the new number of copies:"));
            int available = Integer.parseInt(JOptionPane.showInputDialog("Is the book available? (1 for yes, 0 for no)"));
            if (DatabaseConnection.updateBook(bookId, title, author, genre, year, copies, available)) {
                JOptionPane.showMessageDialog(null, "Book updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error updating book.");
            }
        }); 

        // report button
        JButton reportButton = new JButton("Generate Report For Library Statistics");
        reportButton.setFont(Library.body);
        reportButton.setBackground(Library.darkGray);
        reportButton.setForeground(Color.WHITE);
        reportButton.setFocusable(false);
        reportButton.setBorderPainted(false);
        reportButton.addActionListener(e -> { DatabaseConnection.generateReport();});

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBookButton);
        buttonPanel.add(updateBookButton);
        buttonPanel.add(removeBookButton);
        buttonPanel.add(reportButton);

        homePanel.add(buttonPanel);

        // notifications
        JPanel notficationPanel = new JPanel();

        JTextArea notificationArea = new JTextArea(10, 30);
        notificationArea.setEditable(false);

        notificationArea.append("BORROW HISTORY:\n");
        Object[][] history = DatabaseConnection.borrowHistory();
        for (Object[] row : history) {
            notificationArea.append("User ID: " + row[0] + " | Borrowed: " + row[1] + 
                                    " | on: " + row[2] + " | Due Date: " + row[3] + "\n");
        }

        notificationArea.append("\nOVERDUE BOOKS:\n");
        Object[][] overdue = DatabaseConnection.overdueBooks();
        for (Object[] row : overdue) {
            notificationArea.append("User ID: " + row[0] + " | Borrowed Book: " 
                                    + row[1] + " | Is OverDue: " + row[2] + "\n");
        }

        notficationPanel.add(notificationArea);

        // users
        JPanel usersPanel = new JPanel();

        JTable userTable = new JTable();
        DefaultTableModel userTableModel = new DefaultTableModel();
        String[] userColumnNames = {"Username", "Email", "Role"};   
        Object[][] userData = DatabaseConnection.displayUsers();
        userTableModel.setDataVector(userData, userColumnNames);
        userTable.setModel(userTableModel);
        JScrollPane userScrollPane = new JScrollPane(userTable);

        JButton addUser = new JButton("Add User");
        addUser.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter the username:");
            String password = JOptionPane.showInputDialog("Enter the password:");
            String email = JOptionPane.showInputDialog("Enter the email:");
            String role = JOptionPane.showInputDialog("Enter the role:");
            if (DatabaseConnection.addUser(username, password, email, role)) {
                JOptionPane.showMessageDialog(null, "User added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error adding user.");
            }
        });

        JButton deleteUser = new JButton("Delete User");
        deleteUser.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter the username of the user you want to delete:");
            if (DatabaseConnection.deleteUser(username)) {
                JOptionPane.showMessageDialog(null, "User deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error deleting user.");
            }
        });

        JButton updateUser = new JButton("Change Role");
        updateUser.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a user from the table.");
                return;
            }
            String username = (String) userTable.getValueAt(selectedRow, 0);  
            String role = JOptionPane.showInputDialog("Enter the new role:");
            if (DatabaseConnection.updateUser(username, role)) {
                JOptionPane.showMessageDialog(null, "User role updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error updating user role.");
            }
        });

        usersPanel.add(userScrollPane);
        usersPanel.add(addUser);
        usersPanel.add(deleteUser);
        usersPanel.add(updateUser);

        topPanel.add(homePanel, "Home");
        topPanel.add(notficationPanel, "Notifications");
        topPanel.add(usersPanel, "Users");

        // navigation panel
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new GridLayout(1, 3));

        ImageIcon homeIcon = new ImageIcon(new ImageIcon("assets/Home.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        ImageIcon notificationIcon = new ImageIcon(new ImageIcon("assets/notifications.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        ImageIcon usersIcon = new ImageIcon(new ImageIcon("assets/user.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        JButton homeButton = new JButton("", homeIcon);
        JButton notificationButton = new JButton("", notificationIcon);
        JButton usersButton = new JButton("", usersIcon);

        homeButton.addActionListener(e -> cardLayout.show(topPanel, "Home"));
        notificationButton.addActionListener(e -> cardLayout.show(topPanel, "Notifications"));
        usersButton.addActionListener(e -> cardLayout.show(topPanel, "Users"));
        secondPanel.add(homeButton);
        secondPanel.add(notificationButton);
        secondPanel.add(usersButton);

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(secondPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

        class comboBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String genre = (String) genreComboBox.getSelectedItem();
            String author = (String) authorComboBox.getSelectedItem();
            Integer year = (Integer) yearComboBox.getSelectedItem();
            Object[][] bookData = DatabaseConnection.displayBooks(genre, author, year);
            tableModel.setDataVector(bookData, columnNames);
        }
    }

    class searchListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchQuery = searchField.getText();
            Object[][] bookData = DatabaseConnection.searchBooks(searchQuery);
            tableModel.setDataVector(bookData, columnNames);
        }
    }
}
