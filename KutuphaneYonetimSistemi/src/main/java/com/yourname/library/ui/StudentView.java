package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.BorrowLogDAO;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentView {
    private JFrame frame;
    private AbstractUser currentUser;
    private BookDAO bookDAO;
    private BorrowLogDAO borrowLogDAO;
    private JTable booksTable;
    private JTable borrowedBooksTable;

    public StudentView(AbstractUser user, BookDAO bookDAO, BorrowLogDAO borrowLogDAO) {
        this.currentUser = user;
        this.bookDAO = bookDAO;
        this.borrowLogDAO = borrowLogDAO;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Öğrenci Paneli");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        JLabel welcomeLabel = new JLabel("Hoş Geldiniz, " + currentUser.getFirstName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Menü Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(800, 40));

        JMenu menu = new JMenu("Menü");
        menu.setFont(new Font("Arial", Font.BOLD, 14));

        JMenuItem searchBooksItem = new JMenuItem("Kitap Ara");
        JMenuItem borrowedBooksItem = new JMenuItem("Ödünç Aldıklarım");
        searchBooksItem.addActionListener(e -> showSearchDialog());
        borrowedBooksItem.addActionListener(e -> showBorrowedBooksDialog());

        menu.add(searchBooksItem);
        menu.add(borrowedBooksItem);
        menuBar.add(menu);

        // Geri Düğmesi
        JButton backButton = new JButton("Geri");
        backButton.setPreferredSize(new Dimension(80, 40));
        backButton.addActionListener(e -> {
            frame.dispose();
            new LoginView(null, bookDAO, borrowLogDAO);
        });
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(backButton);

        frame.setJMenuBar(menuBar);

        // Kitap Tablosu
        booksTable = new JTable();
        updateBooksTable(bookDAO.getAllBooksWithRatings());
        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Kitap Ödünç Al Butonu
        JButton borrowBookButton = new JButton("Kitap Ödünç Al");
        borrowBookButton.addActionListener(e -> borrowSelectedBook());
        panel.add(borrowBookButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void updateBooksTable(List<Book> books) {
        String[] columnNames = {"Başlık", "Yazar", "Konu", "Durum", "Puan"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getTitle(),
                    book.getAuthor(),
                    book.getSubject(),
                    book.getStatus(),
                    book.getAvgRating() // Puan sütununa ortalama puan eklendi
            });
        }

        booksTable.setModel(tableModel);

        // Tüm sütunlar için hücre içeriğini ortala
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < booksTable.getColumnCount(); i++) {
            booksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void showBorrowedBooksDialog() {
        List<String[]> borrowedBooks = borrowLogDAO.getBorrowedBooksByUser(currentUser.getId());

        JDialog dialog = new JDialog(frame, "Ödünç Alınan Kitaplar", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        dialog.add(panel);

        borrowedBooksTable = new JTable();
        updateBorrowedBooksTable(borrowedBooks);
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        // Kitap iade etme düğmesi
        JButton returnBookButton = new JButton("Kitabı İade Et");
        returnBookButton.addActionListener(e -> {
            int selectedRow = borrowedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Lütfen iade etmek için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedBookTitle = (String) borrowedBooksTable.getValueAt(selectedRow, 0);

            Book book = bookDAO.getBookByTitle(selectedBookTitle);
            if (book == null) {
                JOptionPane.showMessageDialog(dialog, "Seçilen kitap bulunamadı. Lütfen tekrar deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmation = JOptionPane.showConfirmDialog(dialog, "Bu kitabı iade etmek istiyor musunuz?", "İade Onayı", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                bookDAO.returnBook(currentUser.getId(), book.getId());
                JOptionPane.showMessageDialog(dialog, "Kitap başarıyla iade edildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                updateBorrowedBooksTable(borrowLogDAO.getBorrowedBooksByUser(currentUser.getId()));
                updateBooksTable(bookDAO.getAllBooksWithRatings());
            }
        });

        // Kitap puanlama düğmesi
        JButton rateBookButton = new JButton("Puanla");
        rateBookButton.addActionListener(e -> {
            int selectedRow = borrowedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Lütfen puanlamak için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedBookTitle = (String) borrowedBooksTable.getValueAt(selectedRow, 0);

            Book book = bookDAO.getBookByTitle(selectedBookTitle);
            if (book == null) {
                JOptionPane.showMessageDialog(dialog, "Seçilen kitap bulunamadı. Lütfen tekrar deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String ratingInput = JOptionPane.showInputDialog(dialog, "Kitaba 0-10 arasında bir puan verin:");
            try {
                int rating = Integer.parseInt(ratingInput);
                if (rating < 0 || rating > 10) {
                    JOptionPane.showMessageDialog(dialog, "Geçerli bir puan girin (0-10 arası).", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                bookDAO.rateBook(currentUser.getId(), book.getId(), rating);
                JOptionPane.showMessageDialog(dialog, "Kitap başarıyla puanlandı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                updateBooksTable(bookDAO.getAllBooksWithRatings());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Geçerli bir sayı girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(returnBookButton);
        buttonPanel.add(rateBookButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateBorrowedBooksTable(List<String[]> borrowedBooks) {
        String[] columnNames = {"Başlık", "Tarih"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String[] book : borrowedBooks) {
            tableModel.addRow(new Object[]{book[0], book[1]});
        }

        borrowedBooksTable.setModel(tableModel);

        // Tablodaki verileri ortala
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < borrowedBooksTable.getColumnCount(); i++) {
            borrowedBooksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void borrowSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Lütfen ödünç almak için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedBookTitle = (String) booksTable.getValueAt(selectedRow, 0);
        Book book = bookDAO.getBookByTitle(selectedBookTitle);

        if (book != null && "Mevcut".equalsIgnoreCase(book.getStatus())) {
            bookDAO.borrowBook(currentUser.getId(), book.getId());
            JOptionPane.showMessageDialog(frame, "Kitap başarıyla ödünç alındı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            updateBooksTable(bookDAO.getAllBooksWithRatings());
        } else {
            JOptionPane.showMessageDialog(frame, "Bu kitap ödünç alınamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSearchDialog() {
        String keyword = JOptionPane.showInputDialog(frame, "Aramak istediğiniz kitabın başlığını, yazarını veya konusunu girin:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Book> books = bookDAO.searchBooks(keyword);
            updateBooksTable(books);
        } else {
            JOptionPane.showMessageDialog(frame, "Lütfen geçerli bir anahtar kelime girin.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}
