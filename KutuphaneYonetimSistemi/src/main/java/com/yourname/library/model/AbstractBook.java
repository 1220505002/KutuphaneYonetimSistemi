package com.yourname.library.model;

public abstract class AbstractBook {
    protected int id;
    protected String title;
    protected String author;
    protected String subject;

    public AbstractBook(int id, String title, String author, String subject) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.subject = subject;
    }

    // Getter/Setter
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getSubject() {
        return subject;
    }

    public abstract String getBookType();
}
