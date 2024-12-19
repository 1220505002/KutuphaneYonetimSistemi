package com.yourname.library.service;


import com.yourname.library.dao.LoanDAO;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Book;
import com.yourname.library.model.Loan;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoanService {
    private LoanDAO loanDAO;

    public LoanService(LoanDAO loanDAO) {
        this.loanDAO = loanDAO;
    }

    public Loan borrowBook(AbstractUser user, Book book) {
        // Varsayım: Kitap rafta durumunda ve ödünç alınabilir
        // Ödünç alma tarihi: bugün
        Date now = new Date();

        // Son iade tarihi: 15 gün sonrası
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, 15);
        Date dueDate = cal.getTime();

        // Yeni Loan oluştur
        int newId = loanDAO.getAllLoans().size() + 1;
        Loan loan = new Loan(newId, user, book, now, dueDate);
        loanDAO.addLoan(loan);

        // Kitap durumunu BorrowedState'e çekmek için BookService kullanabilirsiniz.
        // Burada sadece LoanService gösteriyoruz. İleride BookService'i çağırabilirsiniz.

        return loan;
    }

    public void returnBook(int loanId) {
        Loan loan = loanDAO.getLoanById(loanId);
        if(loan != null) {
            // İade edildiğinde ceza hesapla
            calculateFine(loan);
            // Kitabın durumunu AvailableState'e çekin (BookService yardımıyla)
            // loan.getBook().setState(new AvailableState());
            // Loan'u sistemden silebilir veya iade edildi olarak işaretleyebilirsiniz.
            loanDAO.deleteLoan(loan);
        }
    }

    public void calculateFine(Loan loan) {
        Date today = new Date();
        if(today.after(loan.getDueDate())) {
            // Gecikme var
            long diff = today.getTime() - loan.getDueDate().getTime();
            long diffDays = diff / (1000 * 60 * 60 * 24);
            if(diffDays > 0) {
                double fine = diffDays * 1.0; // Her gün için 1 lira
                loan.setFine(fine);
            }
        }
    }

    public List<Loan> getAllLoans() {
        return loanDAO.getAllLoans();
    }
}
