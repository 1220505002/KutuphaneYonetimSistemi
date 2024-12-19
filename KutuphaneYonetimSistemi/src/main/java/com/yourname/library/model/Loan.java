package com.yourname.library.model;

import java.util.Date;

public class Loan {
    private int id;
    private AbstractUser user;
    private Book book;
    private Date loanDate;
    private Date dueDate; // Son iade tarihi
    private double fine;  // Ceza miktarı, gecikme durumunda hesaplanacak

    public Loan(int id, AbstractUser user, Book book, Date loanDate, Date dueDate) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.fine = 0.0;
    }

    public int getId() {
        return id;
    }

    public AbstractUser getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }
}
