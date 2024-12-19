package com.yourname.library.pattern.factory;

import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Student;
import com.yourname.library.model.Staff;

public class UserFactory {

    public static AbstractUser createUser(String userType, int id, String firstName, String lastName, String email, String password, String number) {
        if ("Student".equalsIgnoreCase(userType)) {
            // Öğrenci türünde kullanıcı oluştur
            return new Student(id, firstName, lastName, email, password, number);
        } else if ("Staff".equalsIgnoreCase(userType)) {
            // Personel türünde kullanıcı oluştur
            return new Staff(id, firstName, lastName, email, password, number);
        } else {
            throw new IllegalArgumentException("Geçersiz kullanıcı tipi: " + userType);
        }
    }
}
