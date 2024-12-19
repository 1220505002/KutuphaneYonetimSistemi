package com.yourname.library.dao;

import com.yourname.library.model.Loan;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Book;
import com.yourname.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private UserDAO userDAO;
    private BookDAO bookDAO;

    // UserDAO ve BookDAO'yu constructor'dan enjekte ediyoruz.
    // Bu sayede user_id ve book_id üzerinden gerçek nesneleri çekebiliriz.
    public LoanDAO(UserDAO userDAO, BookDAO bookDAO) {
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }

    public LoanDAO() {

    }

    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (user_id, book_id, loan_date, due_date, fine) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loan.getUser().getId());
            ps.setInt(2, loan.getBook().getId());
            ps.setTimestamp(3, new Timestamp(loan.getLoanDate().getTime()));
            ps.setTimestamp(4, new Timestamp(loan.getDueDate().getTime()));
            ps.setDouble(5, loan.getFine());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT id, user_id, book_id, loan_date, due_date, fine FROM loans";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while(rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int bookId = rs.getInt("book_id");
                Timestamp loanDateTs = rs.getTimestamp("loan_date");
                Timestamp dueDateTs = rs.getTimestamp("due_date");
                double fine = rs.getDouble("fine");

                AbstractUser user = userDAO.getUserById(userId); // userDAO üzerinden user çekiyoruz
                Book book = bookDAO.getBookById(bookId);         // bookDAO üzerinden book çekiyoruz

                Loan loan = new Loan(id, user, book, new Date(loanDateTs.getTime()), new Date(dueDateTs.getTime()));
                loan.setFine(fine);
                loans.add(loan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public Loan getLoanById(int id) {
        String sql = "SELECT user_id, book_id, loan_date, due_date, fine FROM loans WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    int userId = rs.getInt("user_id");
                    int bookId = rs.getInt("book_id");
                    Timestamp loanDateTs = rs.getTimestamp("loan_date");
                    Timestamp dueDateTs = rs.getTimestamp("due_date");
                    double fine = rs.getDouble("fine");

                    AbstractUser user = userDAO.getUserById(userId);
                    Book book = bookDAO.getBookById(bookId);

                    Loan loan = new Loan(id, user, book, new Date(loanDateTs.getTime()), new Date(dueDateTs.getTime()));
                    loan.setFine(fine);
                    return loan;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateLoan(Loan loan) {
        String sql = "UPDATE loans SET user_id=?, book_id=?, loan_date=?, due_date=?, fine=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loan.getUser().getId());
            ps.setInt(2, loan.getBook().getId());
            ps.setTimestamp(3, new Timestamp(loan.getLoanDate().getTime()));
            ps.setTimestamp(4, new Timestamp(loan.getDueDate().getTime()));
            ps.setDouble(5, loan.getFine());
            ps.setInt(6, loan.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLoan(Loan loan) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loan.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
