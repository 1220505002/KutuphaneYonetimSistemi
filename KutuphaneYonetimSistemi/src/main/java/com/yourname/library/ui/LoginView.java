package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.BorrowLogDAO;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.service.UserService;

import javax.swing.*;

public class LoginView {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserService userService;
    private BookDAO bookDAO;
    private BorrowLogDAO borrowLogDAO;

    public LoginView(UserService userService, BookDAO bookDAO, BorrowLogDAO borrowLogDAO) {
        this.userService = userService;
        this.bookDAO = bookDAO;
        this.borrowLogDAO = borrowLogDAO;
        initialize();
    }

    public LoginView(BookDAO bookDAO, BorrowLogDAO borrowLogDAO) {
    }

    private void initialize() {
        frame = new JFrame("Giriş Yap");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JLabel emailLabel = new JLabel("E-posta:");
        emailLabel.setBounds(50, 50, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(150, 50, 200, 25);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setBounds(50, 100, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(150, 100, 200, 25);
        panel.add(passwordField);

        loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(150, 150, 100, 30);
        panel.add(loginButton);

        registerButton = new JButton("Kayıt Ol");
        registerButton.setBounds(260, 150, 100, 30);
        panel.add(registerButton);

        // Giriş Yap Butonu İşlevi
        loginButton.addActionListener(e -> handleLogin());

        // Kayıt Ol Butonu İşlevi
        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterView(userService, bookDAO, borrowLogDAO); // RegisterView'e doğru parametrelerle geçiş
        });

        frame.setVisible(true);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {
            AbstractUser user = userService.login(email, password);

            if (user != null) {
                JOptionPane.showMessageDialog(frame, "Giriş Başarılı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                // Kullanıcı türüne göre yönlendirme
                String userType = user.getUserType();
                frame.dispose(); // Yeni ekran açılmadan önce bu pencereyi kapat

                if ("ÖĞRENCİ".equalsIgnoreCase(userType) || "STUDENT".equalsIgnoreCase(userType)) {
                    new StudentView(user, bookDAO, borrowLogDAO); // StudentView'e geç
                } else if ("PERSONEL".equalsIgnoreCase(userType) || "STAFF".equalsIgnoreCase(userType)) {
                    new StaffView(user, bookDAO); // StaffView'e geç
                } else {
                    JOptionPane.showMessageDialog(null, "Geçersiz kullanıcı türü: " + userType, "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "E-posta veya şifre hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Hata ayıklama için konsola yazdır
        }
    }
}
