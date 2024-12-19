package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.model.Book;
import com.yourname.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageBooksView {
    private JFrame frame;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private BookService bookService;

    public ManageBooksView(BookDAO bookDAO, BookService bookService) {
        this.bookDAO = bookDAO;
        this.bookService = bookService;
        initialize();
    }

    private void initialize() {
        // Frame Ayarları
        frame = new JFrame("Kitap Yönetimi");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        // Araç Çubuğu
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addBookButton = new JButton("Kitap Ekle");
        JButton updateBookButton = new JButton("Kitap Güncelle");
        JButton deleteBookButton = new JButton("Kitap Sil");
        JButton searchBookButton = new JButton("Kitap Ara");
        JButton updateStatusButton = new JButton("Durum Güncelle");
        JButton backButton = new JButton("Geri");

        toolBar.add(addBookButton);
        toolBar.add(updateBookButton);
        toolBar.add(deleteBookButton);
        toolBar.addSeparator();
        toolBar.add(searchBookButton);
        toolBar.add(updateStatusButton);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(backButton);

        panel.add(toolBar, BorderLayout.NORTH);

        // Geri Butonuna İşlev Ekle
        backButton.addActionListener(e -> frame.dispose());

        // Kitap Tablosu
        String[] columnNames = {"ID", "Kitap Adı", "Yazar", "Konu", "Durum"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < booksTable.getColumnCount(); i++) {
            booksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        // Tabloyu Güncelle
        List<Book> books = bookDAO.getAllBooks();
        if (books != null) {
            updateBooksTable(books);
        } else {
            JOptionPane.showMessageDialog(frame, "Kitap listesi alınamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        // Buton İşlevleri
        addBookButton.addActionListener(e -> showAddBookDialog());
        updateBookButton.addActionListener(e -> showUpdateBookDialog());
        deleteBookButton.addActionListener(e -> deleteSelectedBook());
        searchBookButton.addActionListener(e -> showSearchBookDialog());
        updateStatusButton.addActionListener(e -> showUpdateStatusDialog());
    }

    private void showUpdateBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Lütfen güncellemek için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) booksTable.getValueAt(selectedRow, 0);
        Book selectedBook = bookDAO.getBookById(bookId);

        if (selectedBook == null) {
            JOptionPane.showMessageDialog(frame, "Seçilen kitap bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField titleField = new JTextField(selectedBook.getTitle());
        JTextField authorField = new JTextField(selectedBook.getAuthor());
        JTextField subjectField = new JTextField(selectedBook.getSubject());

        Object[] message = {
                "Kitap Adı:", titleField,
                "Yazar:", authorField,
                "Konu:", subjectField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Kitap Güncelle", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            selectedBook.setTitle(titleField.getText().trim());
            selectedBook.setAuthor(authorField.getText().trim());
            selectedBook.setSubject(subjectField.getText().trim());

            bookDAO.updateBook(selectedBook);
            updateBooksTable(bookDAO.getAllBooks());
            JOptionPane.showMessageDialog(frame, "Kitap başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAddBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField subjectField = new JTextField();

        Object[] message = {
                "Kitap Adı:", titleField,
                "Yazar:", authorField,
                "Konu:", subjectField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Kitap Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String subject = subjectField.getText().trim();

            if (!title.isEmpty() && !author.isEmpty() && !subject.isEmpty()) {
                Book newBook = new Book(0, title, author, subject, "Mevcut");
                if (bookDAO.addBook(newBook)) {
                    updateBooksTable(bookDAO.getAllBooks());
                    JOptionPane.showMessageDialog(frame, "Kitap başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Kitap eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Lütfen silmek için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) booksTable.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(frame, "Bu kitabı silmek istediğinizden emin misiniz?", "Kitap Sil", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            Book bookToDelete = bookDAO.getBookById(bookId);
            if (bookToDelete != null && bookDAO.deleteBook(bookToDelete)) {
                updateBooksTable(bookDAO.getAllBooks());
                JOptionPane.showMessageDialog(frame, "Kitap başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Kitap silinemedi. Lütfen tekrar deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBooksTable(List<Book> books) {
        if (books == null) {
            JOptionPane.showMessageDialog(frame, "Kitap listesi alınamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getSubject(), book.getStatus()});
        }
    }

    private void showSearchBookDialog() {
        String keyword = JOptionPane.showInputDialog(frame, "Aramak istediğiniz kitap başlığı, yazarı veya konuyu girin:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Book> books = bookDAO.searchBooks(keyword);
            if (books != null) {
                updateBooksTable(books);
            } else {
                JOptionPane.showMessageDialog(frame, "Arama sonuçları bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Lütfen geçerli bir anahtar kelime girin!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUpdateStatusDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Lütfen durumunu güncellemek için bir kitap seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) booksTable.getValueAt(selectedRow, 0);
        String[] statuses = {"Mevcut", "Ödünç Alındı", "Kayıp"};
        String newStatus = (String) JOptionPane.showInputDialog(frame, "Yeni durumu seçin:", "Durum Güncelle", JOptionPane.PLAIN_MESSAGE, null, statuses, statuses[0]);

        if (newStatus != null) {
            bookDAO.updateBookStatus(bookId, newStatus);
            updateBooksTable(bookDAO.getAllBooks());
            JOptionPane.showMessageDialog(frame, "Durum başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
