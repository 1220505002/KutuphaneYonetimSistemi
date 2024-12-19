package com.yourname.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/library_db?useUnicode=true&characterEncoding=utf8";
    private final String username = "root"; // Kendi kullanıcı adınızı yazın
    private final String password = "1234"; // Kendi şifrenizi yazın

    // Özel yapıcı (constructor)
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Veritabanına başarıyla bağlanıldı.");
        } catch (ClassNotFoundException e) {
            System.out.println("HATA: MySQL JDBC sürücüsü bulunamadı. Lütfen sürücüyü kontrol edin.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("HATA: Veritabanına bağlanırken bir sorun oluştu. Lütfen bağlantı bilgilerini kontrol edin.");
            System.out.println("Detaylı hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Singleton metodu
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null || instance.getConnection() == null || isConnectionClosed()) {
            System.out.println("Veritabanı bağlantısı yeniden oluşturuluyor...");
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Bağlantı nesnesini döner
    public Connection getConnection() {
        return connection;
    }

    // Bağlantının kapalı olup olmadığını kontrol eder
    private static boolean isConnectionClosed() {
        try {
            return instance == null || instance.connection == null || instance.connection.isClosed();
        } catch (SQLException e) {
            System.out.println("HATA: Bağlantı durumu kontrol edilirken hata oluştu. Bağlantı yeniden oluşturulacak.");
            e.printStackTrace();
            return true;
        }
    }

    // Veritabanı bağlantısını test etmek için bir yardımcı metod
    public static void testConnection() {
        try (Connection connection = getInstance().getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Veritabanı bağlantısı başarılı.");
            } else {
                System.out.println("Veritabanı bağlantısı başarısız.");
            }
        } catch (SQLException e) {
            System.out.println("HATA: Veritabanı bağlantısı test edilirken bir sorun oluştu.");
            e.printStackTrace();
        }
    }

    // Ana metod (Bağlantı testi için)
    public static void main(String[] args) {
        DatabaseConnection.testConnection();
    }
}
