package com.yourname.library.pattern.decorator;

import com.yourname.library.model.Book;

public class BaseBookComponent implements BookComponent {
    private Book book;

    public BaseBookComponent(Book book) {
        this.book = book;
    }

    @Override
    public String getDescription() {
        return "Title: " + book.getTitle() + " | Author: " + book.getAuthor();
    }
}
