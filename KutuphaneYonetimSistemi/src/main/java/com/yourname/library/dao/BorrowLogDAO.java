package com.yourname.library.dao;

import com.yourname.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowLogDAO {

    /**
     * Kullanıcının ödünç aldığı ve henüz iade etmediği kitapları getirir.
     *
     * @param userId Kullanıcının ID'si.
     * @return Kullanıcının ödünç aldığı kitapların listesi.
     */
    public List<String[]> getBorrowedBooksByUser(int userId) {
        List<String[]> borrowedBooks = new ArrayList<>();
        String sql = "SELECT b.title, l.borrow_date FROM borrow_logs l " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.user_id = ? AND l.return_date IS NULL";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowedBooks.add(new String[]{
                            rs.getString("title"),        // Kitap Başlığı
                            rs.getString("borrow_date")   // Ödünç Alma Tarihi
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowedBooks;
    }

    /**
     * Ödünç alma kaydı ekler.
     *
     * @param userId Kullanıcının ID'si.
     * @param bookId Kitabın ID'si.
     */
    public void addBorrowLog(int userId, int bookId) {
        String sql = "INSERT INTO borrow_logs (user_id, book_id, borrow_date) VALUES (?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Kitabın iade kaydını günceller.
     *
     * @param userId Kullanıcının ID'si.
     * @param bookId Kitabın ID'si.
     */
    public void updateReturnLog(int userId, int bookId) {
        String sql = "UPDATE borrow_logs SET return_date = NOW() " +
                "WHERE user_id = ? AND book_id = ? AND return_date IS NULL";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("HATA: İade işlemi yapılamadı. Log bulunamadı.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
