package com.yourname.library.service;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.model.Book;

import java.util.List;

public class BookService {
    private BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void borrowBook(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            book.getState().borrow(book);
            bookDAO.updateBook(book); // Kitap durumunu güncelle
        }
    }

    public void returnBook(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            book.getState().returnBook(book);
            bookDAO.updateBook(book); // Kitap durumunu güncelle
        }
    }

    public void reportBookLost(int bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            book.getState().reportLost(book);
            bookDAO.updateBook(book); // Kitap durumunu güncelle
        }
    }

    public void addBook(Book book) {
        bookDAO.addBook(book);
    }

    public void updateBook(Book book) {
        bookDAO.updateBook(book);
    }

    public void deleteBook(Book book) {
        bookDAO.deleteBook(book);
    }

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
}
