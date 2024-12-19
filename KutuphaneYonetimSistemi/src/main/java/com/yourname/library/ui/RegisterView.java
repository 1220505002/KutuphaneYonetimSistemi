package com.yourname.library.ui;

import com.yourname.library.service.UserService;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Student;
import com.yourname.library.model.Staff;
import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.BorrowLogDAO;

import javax.swing.*;

public class RegisterView {
    private JFrame frame;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField numberField;
    private JLabel numberLabel; // Numara etiketini dinamik değiştirebilmek için JLabel
    private JComboBox<String> userTypeComboBox;
    private JButton registerButton;
    private UserService userService;
    private BookDAO bookDAO;
    private BorrowLogDAO borrowLogDAO;

    public RegisterView(UserService userService, BookDAO bookDAO, BorrowLogDAO borrowLogDAO) {
        this.userService = userService;
        this.bookDAO = bookDAO;
        this.borrowLogDAO = borrowLogDAO;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Kayıt Ol");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel firstNameLabel = new JLabel("İsim:");
        firstNameLabel.setBounds(50, 40, 120, 25);
        panel.add(firstNameLabel);

        firstNameField = new JTextField(20);
        firstNameField.setBounds(200, 40, 220, 25);
        panel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Soyisim:");
        lastNameLabel.setBounds(50, 80, 120, 25);
        panel.add(lastNameLabel);

        lastNameField = new JTextField(20);
        lastNameField.setBounds(200, 80, 220, 25);
        panel.add(lastNameField);

        JLabel emailLabel = new JLabel("E-posta:");
        emailLabel.setBounds(50, 120, 120, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(200, 120, 220, 25);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setBounds(50, 160, 120, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(200, 160, 220, 25);
        panel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Şifre (Tekrar):");
        confirmPasswordLabel.setBounds(50, 200, 120, 25);
        panel.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBounds(200, 200, 220, 25);
        panel.add(confirmPasswordField);

        JLabel userTypeLabel = new JLabel("Kullanıcı Tipi:");
        userTypeLabel.setBounds(50, 240, 120, 25);
        panel.add(userTypeLabel);

        String[] userTypes = {"ÖĞRENCİ", "PERSONEL"};
        userTypeComboBox = new JComboBox<>(userTypes);
        userTypeComboBox.setBounds(200, 240, 220, 25);
        panel.add(userTypeComboBox);

        numberLabel = new JLabel("Numara:");
        numberLabel.setBounds(50, 280, 120, 25);
        panel.add(numberLabel);

        numberField = new JTextField(20);
        numberField.setBounds(200, 280, 220, 25);
        panel.add(numberField);

        registerButton = new JButton("Kayıt Ol");
        registerButton.setBounds(200, 320, 220, 30);
        panel.add(registerButton);

        // Kullanıcı tipi değiştiğinde numara etiketini güncelle
        userTypeComboBox.addActionListener(e -> updateNumberLabel());

        registerButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            String userType = (String) userTypeComboBox.getSelectedItem();
            String number = numberField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || number.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Tüm alanları doldurmanız gerekmektedir.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Şifreler uyuşmuyor.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AbstractUser newUser;
            if ("ÖĞRENCİ".equalsIgnoreCase(userType)) {
                newUser = new Student(0, firstName, lastName, email, password, number);
            } else {
                newUser = new Staff(0, firstName, lastName, email, password, number);
            }

            boolean registered = userService.registerUser(newUser);
            if (registered) {
                JOptionPane.showMessageDialog(frame, "Kayıt başarılı! Giriş yapabilirsiniz.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Kayıt ekranını kapat
                new LoginView(userService, bookDAO, borrowLogDAO); // Giriş ekranını aç
            } else {
                JOptionPane.showMessageDialog(frame, "Kayıt başarısız! Bu e-posta zaten kullanılıyor.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateNumberLabel() {
        String userType = (String) userTypeComboBox.getSelectedItem();
        if ("ÖĞRENCİ".equalsIgnoreCase(userType)) {
            numberLabel.setText("Öğrenci Numarası:");
        } else if ("PERSONEL".equalsIgnoreCase(userType)) {
            numberLabel.setText("Personel Numarası:");
        }
    }
}
