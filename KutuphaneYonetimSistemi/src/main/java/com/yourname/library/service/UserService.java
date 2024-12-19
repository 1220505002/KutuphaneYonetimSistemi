package com.yourname.library.service;

import com.yourname.library.dao.UserDAO;
import com.yourname.library.model.AbstractUser;

public class UserService {
    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserService() {

    }

    public boolean registerUser(AbstractUser user) {
        // Aynı e-posta var mı kontrolü
        if (userDAO.getUserByEmail(user.getEmail()) == null) {
            userDAO.addUser(user);
            System.out.println("Kullanıcı başarıyla eklendi: " + user.getEmail());
            return true;
        }
        System.out.println("Kullanıcı kaydı başarısız veya bu e-posta zaten kullanılıyor: " + user.getEmail());
        return false;
    }

    public AbstractUser login(String email, String password) {
        AbstractUser user = userDAO.getUserByEmail(email);
        if (user != null) {
            System.out.println("Giriş için bulunan kullanıcı: " + user.getEmail() + " - Şifre: " + user.getPassword());
            if (user.getPassword().trim().equals(password.trim())) { // trim ekledik
                System.out.println("Şifre eşleşti.");
                return user;
            } else {
                System.out.println("Şifre eşleşmedi.");
            }
        } else {
            System.out.println("Kullanıcı bulunamadı: " + email);
        }
        return null;
    }
}
