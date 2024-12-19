package com.yourname.library.pattern.state;

import com.yourname.library.model.Book;

public interface BookState {
    void borrow(Book book);
    void returnBook(Book book);
    void reportLost(Book book);
    // Duruma göre yapılabilecek aksiyonlar
}
