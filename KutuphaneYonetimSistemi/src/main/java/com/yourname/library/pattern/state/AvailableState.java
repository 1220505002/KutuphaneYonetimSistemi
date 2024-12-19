package com.yourname.library.pattern.state;

import com.yourname.library.model.Book;

public class AvailableState implements BookState {
    @Override
    public void borrow(Book book) {
        System.out.println("Kitap ödünç alındı. Durum BorrowedState’e geçiyor.");
        book.setState(new BorrowedState()); // Durumu BorrowedState olarak güncelle
    }

    @Override
    public void returnBook(Book book) {
        System.out.println("Kitap zaten rafta.");
    }

    @Override
    public void reportLost(Book book) {
        System.out.println("Kitap kayıp olarak bildirildi. Durum LostState’e geçiyor.");
        book.setState(new LostState()); // Durumu LostState olarak güncelle
    }
}
