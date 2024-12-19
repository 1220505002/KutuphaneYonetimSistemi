package com.yourname.library.model;

public class Student extends AbstractUser {
    private String studentNumber;

    public Student(int id, String firstName, String lastName, String email, String password, String studentNumber) {
        super(id, firstName, lastName, email, password);
        this.studentNumber = studentNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Override
    public String getUserType() {
        return "Student"; // Bu sınıfın kullanıcı tipi "Student" olarak tanımlanıyor
    }
}
