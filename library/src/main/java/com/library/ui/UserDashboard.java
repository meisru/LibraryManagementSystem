package com.library.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.library.dao.DatabaseConnection;
import java.awt.event.*;
import com.library.entities.User;

public class UserDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"ID", "Title", "Author", "Genre", "Year"};
    private JComboBox<String> genreComboBox;
    private JComboBox<String> authorComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTextField searchField;
    private JTextArea notificationArea;

    public UserDashboard(User user) {
        setTitle("Library Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("assets/Book.png");
        setIconImage(icon.getImage());

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

        // borrow button
        JButton borrowButton = new JButton("Borrow Book");
        Library.styleButton(borrowButton);
        borrowButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to borrow.");
                return;
            }
            String bookTitle = (String) booksTable.getValueAt(selectedRow, 1);
            int bookId = DatabaseConnection.getBookId(bookTitle);
            if(DatabaseConnection.borrowBook(user.getUserId(), bookId)) {
                JOptionPane.showMessageDialog(null, "Book borrowed successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "The book is not available for borrowing.");
            }
        });

        // return button
        JButton returnButton = new JButton("Return Book");
        Library.styleButton(returnButton);
        returnButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a book to return.");
                return;
            }
            String bookTitle = (String) booksTable.getValueAt(selectedRow, 1);
            int bookId = DatabaseConnection.getBookId(bookTitle);
            String borrowId = "SELECT borrow_id FROM borrowing WHERE user_id = ? AND book_id = ?;";
            try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(borrowId)) {
                preparedStatement.setInt(1, user.getUserId());
                preparedStatement.setInt(2, bookId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    int borrowIdValue = rs.getInt("borrow_id");
                    if(DatabaseConnection.returnBook(borrowIdValue, bookId)) {
                        JOptionPane.showMessageDialog(null, "Book returned successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error returning book.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You have not borrowed this book.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // report button
        JButton reportButton = new JButton("Generate Report For Library Statistics");
        Library.styleButton(reportButton);
        reportButton.addActionListener(e -> { DatabaseConnection.generateReport();});

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(borrowButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(reportButton);

        homePanel.add(buttonPanel);

        // notifications
        JPanel notficationPanel = new JPanel();

        notificationArea = new JTextArea(10, 30);
        notificationArea.setEditable(false);
        notificationArea.setFont(Library.body);

        updateBorrowHistory(user);

        notificationArea.append("\nOVERDUE BOOKS:\n");
        Object[][] overdue = DatabaseConnection.overdueBooks(user.getUserId());
        for (Object[] row : overdue) {
            notificationArea.append("Book: " + row[0] + " | Due date: " + row[1] + "\n");
        }
        notficationPanel.add(notificationArea);

        topPanel.add(homePanel, "Home");
        topPanel.add(notficationPanel, "Notifications");

        // navigation panel
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new GridLayout(1, 3));

        ImageIcon homeIcon = new ImageIcon(new ImageIcon("assets/Home.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        ImageIcon notificationIcon = new ImageIcon(new ImageIcon("assets/notifications.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        JButton homeButton = new JButton("", homeIcon);
        JButton notificationButton = new JButton("", notificationIcon);

        homeButton.addActionListener(e -> cardLayout.show(topPanel, "Home"));
        notificationButton.addActionListener(e -> cardLayout.show(topPanel, "Notifications"));
        secondPanel.add(homeButton);
        secondPanel.add(notificationButton);

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

    private void updateBorrowHistory(User user) {
        notificationArea.setText("BORROW HISTORY:\n");
        Object[][] history = DatabaseConnection.borrowHistory(user.getUserId());
        for (Object[] row : history) {
            notificationArea.append("Book: " + row[0] + " | Borrowed on: " + row[1] + " | Due date: " 
                                    + row[2] + "| Status: " + row[3] + "\n");
        }
    }
}
