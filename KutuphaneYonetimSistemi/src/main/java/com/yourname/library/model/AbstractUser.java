package com.yourname.library.model;

public abstract class AbstractUser {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;

    public AbstractUser(int id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getter/Setter metotları
    public int getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public abstract String getUserType(); // Kullanıcı tipini döndürmek için abstract metod
}
