package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.UserDAO;
import com.yourname.library.service.BookService;
import com.yourname.library.model.AbstractUser;

import javax.swing.*;

public class StaffView {
    private JFrame frame;
    private AbstractUser user;
    private BookDAO bookDAO;
    private BookService bookService;
    private UserDAO userDAO;

    public StaffView(AbstractUser user, BookDAO bookDAO) {
        this.user = user;
        this.bookDAO = bookDAO;
        this.bookService = new BookService(bookDAO);
        this.userDAO = new UserDAO(); // Kullanıcı Yönetimi için gerekli DAO
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Personel Paneli");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Hoş Geldiniz Etiketi
        JLabel welcomeLabel = new JLabel("Hoş Geldiniz, " + user.getFirstName());
        welcomeLabel.setBounds(100, 20, 200, 25);
        panel.add(welcomeLabel);

        // Kitap Yönetimi Butonu
        JButton manageBooksButton = new JButton("Kitap Yönetimi");
        manageBooksButton.setBounds(100, 70, 200, 30);
        panel.add(manageBooksButton);

        // Kullanıcı Yönetimi Butonu
        JButton manageUsersButton = new JButton("Kullanıcı Yönetimi");
        manageUsersButton.setBounds(100, 120, 200, 30);
        panel.add(manageUsersButton);

        // Kitap Yönetimi Butonu İşlevi
        manageBooksButton.addActionListener(e -> {
            new ManageBooksView(bookDAO, bookService); // Kitap Yönetimi ekranını aç
        });

        // Kullanıcı Yönetimi Butonu İşlevi
        manageUsersButton.addActionListener(e -> {
            new ManageUsersView(userDAO); // Kullanıcı Yönetimi ekranını aç
        });
    }
}
