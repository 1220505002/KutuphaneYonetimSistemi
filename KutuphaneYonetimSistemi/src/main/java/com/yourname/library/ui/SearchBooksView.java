package com.yourname.library.ui;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.model.Book;
import com.yourname.library.pattern.strategy.AuthorSearchStrategy;
import com.yourname.library.pattern.strategy.SearchContext;
import com.yourname.library.pattern.strategy.TitleSearchStrategy;
import com.yourname.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;




public class SearchBooksView {
    private JFrame frame;
    private JTable bookTable;
    private JTextField queryField;
    private JComboBox<String> criteriaBox;
    private JButton searchButton;
    private BookService bookService;

    public SearchBooksView(BookService bookService) {
        this.bookService = bookService;

        frame = new JFrame("Kitap Arama");
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        bookTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBounds(20,20,400,200);
        frame.add(scrollPane);

        queryField = new JTextField();
        queryField.setBounds(20,240,120,25);
        frame.add(queryField);

        criteriaBox = new JComboBox<>(new String[]{"Başlığa Göre", "Yazara Göre"});
        criteriaBox.setBounds(150,240,100,25);
        frame.add(criteriaBox);

        searchButton = new JButton("Ara");
        searchButton.setBounds(270,240,100,25);
        frame.add(searchButton);

        searchButton.addActionListener(e -> {
            String query = queryField.getText();
            String criteria = (String) criteriaBox.getSelectedItem();
            if(!query.isEmpty()) {
                SearchContext ctx;
                if(criteria.equals("Başlığa Göre")) {
                    ctx = new SearchContext(new TitleSearchStrategy());
                } else {
                    ctx = new SearchContext(new AuthorSearchStrategy());
                }

                List<Book> allBooks = bookService.getAllBooks();
                List<Book> results = ctx.executeSearch(allBooks, query);
                fillTable(results);
            }
        });

        fillTable(bookService.getAllBooks());
        frame.setVisible(true);
    }

    private void fillTable(List<Book> books) {
        String[] columnNames = {"ID", "Başlık", "Yazar", "Konu"};
        DefaultTableModel model = new DefaultTableModel(columnNames,0);
        for(Book b : books) {
            Object[] row = {b.getId(), b.getTitle(), b.getAuthor(), b.getSubject()};
            model.addRow(row);
        }
        bookTable.setModel(model);
    }
}
