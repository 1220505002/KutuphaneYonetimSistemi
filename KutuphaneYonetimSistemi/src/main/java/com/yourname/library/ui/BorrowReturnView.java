package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.LoanDAO;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Book;
import com.yourname.library.model.Loan;
import com.yourname.library.pattern.decorator.BaseBookComponent;
import com.yourname.library.pattern.decorator.RateableBookDecorator;
import com.yourname.library.service.BookService;
import com.yourname.library.service.LoanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;



public class BorrowReturnView {
    private JFrame frame;
    private JTable bookTable;
    private JButton borrowButton, returnButton;
    private BookService bookService;
    private LoanService loanService;
    private AbstractUser currentUser;

    public BorrowReturnView(BookDAO bookDAO, AbstractUser currentUser, LoanDAO loanDAO) {
        this.bookService = new BookService(bookDAO);
        this.loanService = new LoanService(loanDAO);
        this.currentUser = currentUser;

        frame = new JFrame("Ödünç Al / İade Et");
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        bookTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBounds(20,20,400,200);
        frame.add(scrollPane);

        borrowButton = new JButton("Ödünç Al");
        borrowButton.setBounds(20,240,120,25);
        frame.add(borrowButton);

        returnButton = new JButton("İade Et");
        returnButton.setBounds(160,240,120,25);
        frame.add(returnButton);

        JButton rateButton = new JButton("Kitabı Puanla");
        rateButton.setBounds(300,240,120,25);
        frame.add(rateButton);

        rateButton.addActionListener(e -> {
            int selected = bookTable.getSelectedRow();
            if(selected != -1) {
                int bookId = (int) bookTable.getValueAt(selected, 0);
                Book book = bookService.getAllBooks().stream()
                        .filter(b -> b.getId() == bookId)
                        .findFirst().orElse(null);
                if(book != null) {
                    BaseBookComponent baseComp = new BaseBookComponent(book);
                    RateableBookDecorator rateable = new RateableBookDecorator(baseComp);

                    String ratingStr = JOptionPane.showInputDialog(frame, "Puanı giriniz (0-5):");
                    if(ratingStr != null && !ratingStr.isEmpty()) {
                        double ratingVal = Double.parseDouble(ratingStr);
                        rateable.addRating(ratingVal);
                        JOptionPane.showMessageDialog(frame, "Güncellenmiş Kitap Bilgisi: " + rateable.getDescription());
                    }
                }
            }
        });

        fillTable();

        borrowButton.addActionListener(e -> {
            int selected = bookTable.getSelectedRow();
            if(selected != -1) {
                int bookId = (int) bookTable.getValueAt(selected, 0);
                Book book = bookService.getAllBooks().stream()
                        .filter(b -> b.getId() == bookId)
                        .findFirst().orElse(null);
                if(book != null) {
                    Loan loan = loanService.borrowBook(currentUser, book);
                    JOptionPane.showMessageDialog(frame, "Kitap ödünç alındı! Son iade tarihi: " + loan.getDueDate());
                    fillTable();
                }
            }
        });

        returnButton.addActionListener(e -> {
            int selected = bookTable.getSelectedRow();
            if(selected != -1) {
                int bookId = (int) bookTable.getValueAt(selected, 0);

                Loan loan = loanService.getAllLoans().stream()
                        .filter(l -> l.getBook().getId() == bookId && l.getUser().getId() == currentUser.getId())
                        .findFirst().orElse(null);

                if(loan != null) {
                    loanService.returnBook(loan.getId());
                    if(loan.getFine() > 0) {
                        JOptionPane.showMessageDialog(frame, "Kitap iade edildi. Gecikme cezası: " + loan.getFine() + " TL");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Kitap iade edildi. Ceza yok.");
                    }
                    fillTable();
                }
            }
        });

        frame.setVisible(true);
    }

    private void fillTable() {
        List<Book> books = bookService.getAllBooks();
        String[] columnNames = {"ID", "Başlık", "Yazar", "Konu", "Durum"};
        DefaultTableModel model = new DefaultTableModel(columnNames,0);
        for(Book b : books) {
            String stateName = b.getState().getClass().getSimpleName();
            Object[] row = {b.getId(), b.getTitle(), b.getAuthor(), b.getSubject(), stateName};
            model.addRow(row);
        }
        bookTable.setModel(model);
    }
}
