package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.LoanDAO;
import com.yourname.library.dao.UserDAO;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.service.BookService;

import javax.swing.*;

public class MainView {
    private JFrame frame;
    private JButton manageBooksButton;
    private JButton searchBooksButton;
    private JButton borrowReturnButton;
    private AbstractUser currentUser;
    private BookDAO bookDAO;
    private BookService bookService;
    private LoanDAO loanDAO;
    private UserDAO userDAO;

    public MainView(AbstractUser user, BookDAO bookDAO) {
        this.currentUser = user;
        this.bookDAO = bookDAO;
        this.bookService = new BookService(bookDAO);

        this.userDAO = new UserDAO();
        this.loanDAO = new LoanDAO(userDAO, bookDAO);

        initialize();
    }

    private void initialize() {
        frame = new JFrame("Ana Menü");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Ortalamak için

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        // Butonlar
        searchBooksButton = new JButton("Kitap Ara");
        searchBooksButton.setBounds(100, 20, 200, 25);
        panel.add(searchBooksButton);

        manageBooksButton = new JButton("Kitap Yönetimi");
        manageBooksButton.setBounds(100, 60, 200, 25);
        panel.add(manageBooksButton);

        borrowReturnButton = new JButton("Ödünç Al / İade Et");
        borrowReturnButton.setBounds(100, 100, 200, 25);
        panel.add(borrowReturnButton);

        // Kullanıcı tipine göre butonları etkinleştir veya devre dışı bırak
        configureButtonsForUserType();

        // Kitap Ara Butonu
        searchBooksButton.addActionListener(e -> new SearchBooksView(bookService));

        // Kitap Yönetimi Butonu
        manageBooksButton.addActionListener(e -> {
            System.out.println("Kitap Yönetimi Butonu Tıklandı.");
            if ("PERSONEL".equalsIgnoreCase(currentUser.getUserType())) {
                new ManageBooksView(bookDAO, bookService);
            } else {
                JOptionPane.showMessageDialog(frame, "Bu özelliğe yalnızca personel erişebilir.", "Erişim Engellendi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ödünç Al / İade Et Butonu
        borrowReturnButton.addActionListener(e -> {
            System.out.println("Ödünç Al / İade Et Butonu Tıklandı.");
            if ("ÖĞRENCİ".equalsIgnoreCase(currentUser.getUserType())) {
                new BorrowReturnView(bookDAO, currentUser, loanDAO);
            } else {
                JOptionPane.showMessageDialog(frame, "Bu özelliğe yalnızca öğrenciler erişebilir.", "Erişim Engellendi", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private void configureButtonsForUserType() {
        String userType = currentUser.getUserType();

        if ("PERSONEL".equalsIgnoreCase(userType)) {
            manageBooksButton.setEnabled(true);
            borrowReturnButton.setEnabled(false);
        } else if ("ÖĞRENCİ".equalsIgnoreCase(userType)) {
            manageBooksButton.setEnabled(false);
            borrowReturnButton.setEnabled(true);
        } else {
            manageBooksButton.setEnabled(false);
            borrowReturnButton.setEnabled(false);
        }
    }
}
