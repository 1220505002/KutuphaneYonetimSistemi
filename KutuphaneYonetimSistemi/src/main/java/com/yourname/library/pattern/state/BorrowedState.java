// BorrowedState.java
package com.yourname.library.pattern.state;

import com.yourname.library.model.Book;

public class BorrowedState implements BookState {
    @Override
    public void borrow(Book book) {
        System.out.println("Kitap zaten ödünç alınmış durumda.");
    }

    @Override
    public void returnBook(Book book) {
        System.out.println("Kitap iade edildi. Durum AvailableState’e geçiyor.");
        book.setState(new AvailableState());
    }

    @Override
    public void reportLost(Book book) {
        System.out.println("Kitap kayıp olarak bildirildi. Durum LostState’e geçiyor.");
        book.setState(new LostState());
    }
}
