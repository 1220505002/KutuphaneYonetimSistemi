package org.example;

import com.yourname.library.dao.BookDAO;
import com.yourname.library.dao.BorrowLogDAO;
import com.yourname.library.dao.UserDAO;
import com.yourname.library.service.UserService;
import com.yourname.library.ui.LoginView;

public class Main {
    public static void main(String[] args) {
        // Gerekli DAO nesnelerini oluştur
        UserDAO userDAO = new UserDAO();
        BookDAO bookDAO = new BookDAO();
        BorrowLogDAO borrowLogDAO = new BorrowLogDAO();

        // UserService'i başlat
        UserService userService = new UserService(userDAO);

        // LoginView'i başlat ve gerekli parametreleri aktar
        new LoginView(userService, bookDAO, borrowLogDAO);
    }
}
