package com.yourname.library.dao;

import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Student;
import com.yourname.library.model.Staff;
import com.yourname.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private AbstractUser createUserFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("id");
        String fName = rs.getString("first_name");
        String lName = rs.getString("last_name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String userType = rs.getString("user_type").toLowerCase();
        String number = rs.getString("number");

        if (userType.equals("öğrenci") || userType.equals("student")) {
            return new Student(userId, fName, lName, email, password, number);
        } else if (userType.equals("personel") || userType.equals("staff")) {
            return new Staff(userId, fName, lName, email, password, number);
        } else {
            throw new IllegalArgumentException("Geçersiz kullanıcı tipi: " + userType);
        }
    }

    public AbstractUser getUserById(int id) {
        String sql = "SELECT id, first_name, last_name, email, password, user_type, number FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AbstractUser getUserByEmail(String email) {
        String sql = "SELECT id, first_name, last_name, email, password, user_type, number FROM users WHERE email=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addUser(AbstractUser user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, user_type, number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            if (user instanceof Student) {
                ps.setString(5, "öğrenci");
                ps.setString(6, ((Student) user).getStudentNumber());
            } else if (user instanceof Staff) {
                ps.setString(5, "personel");
                ps.setString(6, ((Staff) user).getStaffNumber());
            } else {
                throw new IllegalArgumentException("Geçersiz kullanıcı tipi");
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(AbstractUser user) {
        String sql = "UPDATE users SET first_name=?, last_name=?, email=?, password=?, user_type=?, number=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            if (user instanceof Student) {
                ps.setString(5, "öğrenci");
                ps.setString(6, ((Student) user).getStudentNumber());
            } else if (user instanceof Staff) {
                ps.setString(5, "personel");
                ps.setString(6, ((Staff) user).getStaffNumber());
            } else {
                throw new IllegalArgumentException("Geçersiz kullanıcı tipi");
            }
            ps.setInt(7, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AbstractUser> getAllUsers() {
        List<AbstractUser> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, password, user_type, number FROM users";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<AbstractUser> searchUsers(String keyword) {
        List<AbstractUser> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, password, user_type, number FROM users " +
                "WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = "%" + keyword.trim() + "%";
            ps.setString(1, searchKeyword);
            ps.setString(2, searchKeyword);
            ps.setString(3, searchKeyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(createUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
