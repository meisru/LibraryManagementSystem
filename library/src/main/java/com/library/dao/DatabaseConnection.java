package com.library.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.security.crypto.bcrypt.BCrypt;
import com.library.entities.User;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    // get a new database connection
    public static Connection getConnection() throws Exception {
        // Load .env file
        Dotenv dotenv = Dotenv.configure().directory("library/.env").load();
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

    // create a user
    public static boolean createUser(User user) {
        String query = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        // Try-with-resources to auto-close the connection
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            // Hash the password before storing
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // validate a user for login
    public static boolean validateUser(String username, String password) {
        String query = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username); 
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password_hash");
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // User not found or password mismatch
    }

    // check if user exist
    public static boolean userExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // get userID
    public static int getUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; 
    }

    // get the role of a user
    public static String getUserRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // display users
    public static Object[][] displayUsers() {
        String query = "SELECT username, email, role FROM users";
        Object[][] userData = new Object[10][3];
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            int i = 0;
            while (rs.next()) {
                userData[i][0] = rs.getString("username");
                userData[i][1] = rs.getString("email");
                userData[i][2] = rs.getString("role");
                i++;
            }
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    // add user
    public static boolean addUser(String username, String password, String email, String role) {
        String query = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, role);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // delete user
    public static boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // update user's role
    public static boolean updateUser(String username,String role) {
        String query = "UPDATE users SET role = ? WHERE username = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, role);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // get book id
    public static int getBookId(String bookName) {
        String query = "SELECT book_id FROM books WHERE book_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, bookName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("book_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; 
    }

    // add book
    public static boolean addBook(String bookName, String author, String genre, int publicationYear, int copiesAvailable, int availability) {
        String query = "INSERT INTO books (book_name, author, genre, publication_year, copies_available, availability) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, bookName);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);
            preparedStatement.setInt(4, publicationYear);
            preparedStatement.setInt(5, copiesAvailable);
            preparedStatement.setInt(6, availability);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // delete book
    public static boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // update book
    public static boolean updateBook(int bookId, String bookName, String author, String genre, int publicationYear, int copiesAvailable, int availability) {
        String query = "UPDATE books SET book_name = ? , author = ?, genre = ?, publication_year = ?, copies_available = ?, availability = ? WHERE book_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, bookName);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);
            preparedStatement.setInt(4, publicationYear);
            preparedStatement.setInt(5, copiesAvailable);
            preparedStatement.setInt(6, availability);
            preparedStatement.setInt(7, bookId);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // display all books 
    public static Object[][] displayBooks() {
        String query = "SELECT book_id, book_name, author, genre, publication_year FROM books"; 
        Object[][] bookData = new Object[15][5]; 
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            int i = 0;
            while (rs.next()) {
                bookData[i][0] = rs.getInt("book_id");
                bookData[i][1] = rs.getString("book_name");
                bookData[i][2] = rs.getString("author");
                bookData[i][3] = rs.getString("genre");
                bookData[i][4] = rs.getInt("publication_year");
                i++;
            }
            return bookData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null in case of an error
    }

    // Display books with optional filters for genre, author, and publication year
    public static Object[][] displayBooks(String genre, String author, Integer publicationYear) {
        // Base query without any filters
        StringBuilder queryBuilder = new StringBuilder("SELECT book_id, book_name, author, genre, publication_year FROM books");
        boolean hasConditions = false;
        if (genre != null && !genre.isEmpty()) {
            queryBuilder.append(" WHERE genre = ?");
            hasConditions = true;
        }
        if (author != null && !author.isEmpty()) {
            if (hasConditions) {
                queryBuilder.append(" AND author = ?");
            } else {
                queryBuilder.append(" WHERE author = ?");
                hasConditions = true;
            }
        }
        if (publicationYear != null) {
            if (hasConditions) {
                queryBuilder.append(" AND publication_year = ?");
            } else {
                queryBuilder.append(" WHERE publication_year = ?");
                hasConditions = true;
            }
        }
        // Convert StringBuilder to String
        String query = queryBuilder.toString();
        Object[][] bookData = new Object[15][5]; 
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            int parameterIndex = 1;
            if (genre != null && !genre.isEmpty()) {
                preparedStatement.setString(parameterIndex++, genre);
            }
            if (author != null && !author.isEmpty()) {
                preparedStatement.setString(parameterIndex++, author);
            }
            if (publicationYear != null) {
                preparedStatement.setInt(parameterIndex++, publicationYear);
            }
            ResultSet rs = preparedStatement.executeQuery();
            int i = 0;
            while (rs.next()) {
                bookData[i][0] = rs.getInt("book_id");
                bookData[i][1] = rs.getString("book_name");
                bookData[i][2] = rs.getString("author");
                bookData[i][3] = rs.getString("genre");
                bookData[i][4] = rs.getInt("publication_year");
                i++;
            }
            return bookData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    // get authors
    public static String[] getAuthors() {
        String query = "SELECT DISTINCT author FROM books";
        String[] authors = new String[10];
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            int i = 0;
            while (rs.next()) {
                authors[i] = rs.getString("author");
                i++;
            }
            return authors;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    // get genres
    public static String[] getGenres() {
        String query = "SELECT DISTINCT genre FROM books";
        String[] genres = new String[5];
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            int i = 0;
            while (rs.next()) {
                genres[i] = rs.getString("genre");
                i++;
            }
            return genres;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    // get years
    public static Integer[] getYears() {
        String query = "SELECT DISTINCT publication_year FROM books";
        Integer[] years = new Integer[5];
        try (Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            int i = 0;
            while (rs.next()) {
                years[i] = rs.getInt("publication_year");
                i++;
            }
            return years;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }

    // borrow a book
    public static boolean borrowBook(int userId, int bookId) {
        String checkAvailabilityQuery = "SELECT copies_available FROM books WHERE book_id = ?";
        String borrowBookQuery = "INSERT INTO borrowing (user_id, book_id, borrow_date, return_date) " +
                                 "VALUES (?, ?, CURRENT_DATE, CURRENT_DATE + INTERVAL '10 days');";
        String decrementCopiesQuery = "UPDATE books "
                                + "SET copies_available = copies_available - 1, "
                                + "availability = CASE WHEN copies_available - 1 < 1 THEN 0 ELSE availability END "
                                + "WHERE book_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkAvailabilityQuery);
            PreparedStatement borrowStmt = conn.prepareStatement(borrowBookQuery);
            PreparedStatement decrementStmt = conn.prepareStatement(decrementCopiesQuery)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            // Check availability
            if (rs.next()) {
                int availableCopies = rs.getInt("copies_available");
                if (availableCopies <= 0) {
                    System.out.println("Book is unavailable.");
                    return false;
                }
            }
            // Borrow the book
            borrowStmt.setInt(1, userId);
            borrowStmt.setInt(2, bookId);
            borrowStmt.executeUpdate();
            decrementStmt.setInt(1, bookId);
            decrementStmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean returnBook(int borrowId, int bookId) {
        String markAsReturnedQuery = "UPDATE borrowing SET status = 'returned', return_date = CURRENT_DATE WHERE borrow_id = ?";
        String incrementCopiesQuery = "UPDATE books SET copies_available = copies_available + 1 WHERE book_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement markStmt = conn.prepareStatement(markAsReturnedQuery);
            PreparedStatement incrementStmt = conn.prepareStatement(incrementCopiesQuery)) {
            markStmt.setInt(1, borrowId);
            markStmt.executeUpdate();
            incrementStmt.setInt(1, bookId);
            incrementStmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // search
    public static Object[][] searchBooks(String searchTerm) {
        String query = "SELECT book_id, book_name, author, genre, publication_year FROM books "
                    + "WHERE book_name ILIKE ? "
                    + "OR author ILIKE ? "
                    + "OR genre ILIKE ? "
                    + "OR publication_year::text ILIKE ?";
        List<Object[]> books = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            // (adding "%" for partial matches)
            String searchTermPattern = "%" + searchTerm + "%";
            ps.setString(1, searchTermPattern);
            ps.setString(2, searchTermPattern);
            ps.setString(3, searchTermPattern);
            ps.setString(4, searchTermPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getInt("publication_year")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books.toArray(new Object[0][0]);
    }

    // borrow history
    public static Object[][] borrowHistory(int userId) {
        String query = "SELECT b.book_name, br.borrow_date, br.return_date "
        + "FROM books b "
        + "JOIN borrowing br ON b.book_id = br.book_id WHERE br.user_id = ?;";
        List<Object[]> history = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new Object[]{
                    rs.getString("book_name"),
                    rs.getDate("borrow_date"),
                    rs.getDate("return_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history.toArray(new Object[0][0]);
    }

        public static Object[][] borrowHistory() {
        String query = "SELECT br.user_id, b.book_name, br.borrow_date, br.return_date "
        + "FROM books b "
        + "JOIN borrowing br ON b.book_id = br.book_id;";
        List<Object[]> history = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("book_name"),
                    rs.getDate("borrow_date"),
                    rs.getDate("return_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history.toArray(new Object[0][0]);
    }

    // overdue
    public static Object[][] overdueBooks(int userId) {
        String query = "SELECT b.book_name, br.return_date "
                    + "FROM books b "
                    + "JOIN borrowing br ON b.book_id = br.book_id "
                    + "WHERE br.status = 'borrowed' AND br.return_date < CURRENT_DATE AND br.user_id = ?";
        List<Object[]> overdue = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                overdue.add(new Object[]{
                    rs.getString("book_name"),
                    rs.getDate("return_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overdue.toArray(new Object[0][0]);
    }

    public static Object[][] overdueBooks() {
        String query = "SELECT br.user_id, b.book_name, br.return_date "
                    + "FROM books b "
                    + "JOIN borrowing br ON b.book_id = br.book_id "
                    + "WHERE br.status = 'borrowed' AND br.return_date < CURRENT_DATE";
        List<Object[]> overdue = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                overdue.add(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("book_name"),
                    rs.getDate("return_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overdue.toArray(new Object[0][0]);
    }

    // report
    public static void generateReport() {
                    String query = "SELECT " + 
                                "    (SELECT books.book_name " + 
                                "     FROM borrowing " + 
                                "     JOIN books ON borrowing.book_id = books.book_id " + 
                                "     GROUP BY books.book_id, books.book_name " + 
                                "     ORDER BY COUNT(borrowing.book_id) DESC " + 
                                "     LIMIT 1) AS most_popular_book, " + 
                                "    (SELECT COUNT(borrowing.book_id) " + 
                                "     FROM borrowing " + 
                                "     GROUP BY borrowing.book_id " + 
                                "     ORDER BY COUNT(borrowing.book_id) DESC " + 
                                "     LIMIT 1) AS most_popular_book_count, " +
                                "(SELECT books.genre " + 
                                "     FROM borrowing " + 
                                "     JOIN books ON borrowing.book_id = books.book_id " + 
                                "     GROUP BY books.genre " + 
                                "     ORDER BY COUNT(borrowing.book_id) DESC " + 
                                "     LIMIT 1) AS most_popular_genre," +
                                "(SELECT books.author " + 
                                "     FROM borrowing " + 
                                "     JOIN books ON borrowing.book_id = books.book_id " + 
                                "     GROUP BY books.author " + 
                                "     ORDER BY COUNT(borrowing.book_id) DESC " + 
                                "     LIMIT 1) AS most_popular_author," +
                                "    (SELECT COUNT(*) FROM users) AS total_users, " + 
                                "    (SELECT COUNT(*) FROM books) AS total_books," + 
                                "    (SELECT COUNT(*) FROM borrowing) AS total_borrows;";
            try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                String mostPopularBook = rs.getString("most_popular_book");
                int mostPopularBookCount = rs.getInt("most_popular_book_count");
                String mostPopularGenre = rs.getString("most_popular_genre");
                String mostPopularAuthor = rs.getString("most_popular_author");
                int totalUsers = rs.getInt("total_users");
                int totalBooks = rs.getInt("total_books");
                int totalBorrows = rs.getInt("total_borrows");
                BufferedWriter writer = new BufferedWriter(new FileWriter("report.txt"));
                writer.write("Library Statistics:\n");
                writer.write("=====================================\n");
                writer.write("Most popular book: " + mostPopularBook + " (" + mostPopularBookCount + " borrows)\n");
                writer.write("Most popular genre: " + mostPopularGenre + "\n");
                writer.write("Most popular author: " + mostPopularAuthor + "\n");
                writer.write("Total users: " + totalUsers + "\n");
                writer.write("Total books: " + totalBooks + "\n");
                writer.write("Total borrows: " + totalBorrows + "\n");
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
}