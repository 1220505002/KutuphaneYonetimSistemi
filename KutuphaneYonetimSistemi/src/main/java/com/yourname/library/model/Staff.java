package com.yourname.library.model;

public class Staff extends AbstractUser {
    private String staffNumber;

    public Staff(int id, String firstName, String lastName, String email, String password, String staffNumber) {
        super(id, firstName, lastName, email, password);
        this.staffNumber = staffNumber;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    @Override
    public String getUserType() {
        return "Staff"; // Bu sınıfın kullanıcı tipi "Staff" olarak tanımlanıyor
    }
}
