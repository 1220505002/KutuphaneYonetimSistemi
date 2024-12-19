package com.yourname.library.model;

import com.yourname.library.pattern.state.AvailableState;
import com.yourname.library.pattern.state.BookState;
import com.yourname.library.pattern.state.BorrowedState;
import com.yourname.library.pattern.state.LostState;

public class Book {
    private int id;
    private String title;
    private String author;
    private String subject;
    private String status;
    private String avgRating; // Ortalama puan

    // Yeni Constructor (Ortalama puanı da kabul eder)
    public Book(int id, String title, String author, String subject, String status, String avgRating) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.status = status;
        this.avgRating = avgRating != null ? avgRating : "Puan Yok"; // Eğer null ise varsayılan değer
    }

    // Var olan Constructor'lar
    public Book(int id, String title, String author, String subject, String status) {
        this(id, title, author, subject, status, "Puan Yok"); // Ortalama puanı varsayılan olarak ayarla
    }

    public Book(int id, String title, String author, String subject) {
        this(id, title, author, subject, "Mevcut", "Puan Yok"); // Varsayılan durum ve puan
    }

    // Getter ve Setter'lar
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating != null ? avgRating : "Puan Yok";
    }

    // State Deseni İlgili Metotlar
    public BookState getState() {
        return null;
    }

    public void setState(BorrowedState borrowedState) {
    }

    public void setState(AvailableState availableState) {
    }

    public void setState(LostState lostState) {
    }

    public void borrow() {
    }

    // Override: Ortalamayı string olarak döndürmek için
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", subject='" + subject + '\'' +
                ", status='" + status + '\'' +
                ", avgRating='" + avgRating + '\'' +
                '}';
    }

    public Object getRating() {
    return null;}
}
