// LostState.java
package com.yourname.library.pattern.state;

import com.yourname.library.model.Book;

public class LostState implements BookState {
    @Override
    public void borrow(Book book) {
        System.out.println("Kayıp durumdaki bir kitap ödünç alınamaz.");
    }

    @Override
    public void returnBook(Book book) {
        System.out.println("Kayıp durumdaki bir kitap iade edilemez.");
    }

    @Override
    public void reportLost(Book book) {
        System.out.println("Kitap zaten kayıp olarak işaretli.");
    }
}
