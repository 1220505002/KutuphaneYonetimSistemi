package com.yourname.library.dao;

import com.yourname.library.model.Book;
import com.yourname.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Kitap ID ile getirme metodu
    public Book getBookById(int id) {
        String sql = "SELECT id, title, author, subject, state, " +
                "COALESCE((SELECT AVG(rating) FROM book_ratings WHERE book_id = books.id), 0) AS average_rating " +
                "FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createBookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kitap başlığına göre kitap getirme metodu
    public Book getBookByTitle(String title) {
        String sql = "SELECT id, title, author, subject, state, " +
                "COALESCE((SELECT AVG(rating) FROM book_ratings WHERE book_id = books.id), 0) AS average_rating " +
                "FROM books WHERE title = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createBookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tüm kitapları puanlarıyla birlikte getir
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, subject, state, " +
                "COALESCE((SELECT AVG(rating) FROM book_ratings WHERE book_id = books.id), 0) AS average_rating " +
                "FROM books";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(createBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Kitap ekleme metodu
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, subject, state) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getSubject());
            ps.setString(4, book.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kitap silme metodu
    public boolean deleteBook(Book book) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kitap güncelleme metodu
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, subject = ?, state = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getSubject());
            ps.setString(4, book.getStatus());
            ps.setInt(5, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kitap durumu güncelleme metodu
    public boolean updateBookStatus(int bookId, String newStatus) {
        String sql = "UPDATE books SET state = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Yardımcı metod: ResultSet'ten kitap nesnesi oluşturma
    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        String averageRating = rs.getString("average_rating");
        String rating = averageRating != null ? String.format("%.1f", Double.parseDouble(averageRating)) : "Puan Yok";

        String state = rs.getString("state");
        String translatedState = switch (state.toLowerCase()) {
            case "borrowed" -> "Ödünç Alındı";
            case "available" -> "Mevcut";
            default -> state;
        };

        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("subject"),
                translatedState,
                rating
        );
    }

    // Kitap arama metodu
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, subject, state, " +
                "COALESCE((SELECT AVG(rating) FROM book_ratings WHERE book_id = books.id), 0) AS average_rating " +
                "FROM books WHERE title LIKE ? OR author LIKE ? OR subject LIKE ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = "%" + keyword.trim() + "%";
            ps.setString(1, searchKeyword);
            ps.setString(2, searchKeyword);
            ps.setString(3, searchKeyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(createBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Kitap ödünç alma metodu
    public void borrowBook(int userId, int bookId) {
        String updateBookStateSql = "UPDATE books SET state = 'Borrowed' WHERE id = ?";
        String insertLogSql = "INSERT INTO borrow_logs (user_id, book_id, borrow_date) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            try (PreparedStatement psUpdate = conn.prepareStatement(updateBookStateSql)) {
                psUpdate.setInt(1, bookId);
                psUpdate.executeUpdate();
            }
            try (PreparedStatement psInsert = conn.prepareStatement(insertLogSql)) {
                psInsert.setInt(1, userId);
                psInsert.setInt(2, bookId);
                psInsert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kitap iade metodu
    public void returnBook(int userId, int bookId) {
        String updateBookStateSql = "UPDATE books SET state = 'Available' WHERE id = ?";
        String updateLogSql = "UPDATE borrow_logs SET return_date = NOW() WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            try (PreparedStatement psUpdate = conn.prepareStatement(updateBookStateSql)) {
                psUpdate.setInt(1, bookId);
                psUpdate.executeUpdate();
            }
            try (PreparedStatement psLog = conn.prepareStatement(updateLogSql)) {
                psLog.setInt(1, userId);
                psLog.setInt(2, bookId);
                psLog.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kitap puanlama metodu
    public void rateBook(int userId, int bookId, int rating) {
        String sql = "INSERT INTO book_ratings (user_id, book_id, rating) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setInt(3, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tüm kitapları puanlarıyla birlikte getir
    public List<Book> getAllBooksWithRatings() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, subject, state, " +
                "COALESCE((SELECT AVG(rating) FROM book_ratings WHERE book_id = books.id), 0) AS average_rating " +
                "FROM books";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(createBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
