package com.yourname.library.ui;

import com.yourname.library.dao.UserDAO;
import com.yourname.library.model.AbstractUser;
import com.yourname.library.model.Student;
import com.yourname.library.model.Staff;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersView {
    private JFrame frame;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    public ManageUsersView(UserDAO userDAO) {
        this.userDAO = userDAO;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Kullanıcı Yönetimi");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        // Araç Çubuğu
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addUserButton = new JButton("Kullanıcı Ekle");
        JButton updateUserButton = new JButton("Kullanıcı Güncelle");
        JButton deleteUserButton = new JButton("Kullanıcı Sil");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Ara");

        toolBar.add(addUserButton);
        toolBar.add(updateUserButton);
        toolBar.add(deleteUserButton);
        toolBar.addSeparator();
        toolBar.add(searchField);
        toolBar.add(searchButton);

        panel.add(toolBar, BorderLayout.NORTH);

        // Kullanıcı Tablosu
        String[] columnNames = {"ID", "Ad", "Soyad", "E-posta", "Kullanıcı Türü", "Numara"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Düzenlenemez
            }
        };

        usersTable = new JTable(tableModel);

        // Tablo Hücrelerini Ortala
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Tüm sütunlara hücre render'ını uygula
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        updateUsersTable(userDAO.getAllUsers());

        // Düğme İşlevleri
        addUserButton.addActionListener(e -> showAddUserDialog());
        updateUserButton.addActionListener(e -> showUpdateUserDialog());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            updateUsersTable(userDAO.searchUsers(keyword));
        });
    }

    private void updateUsersTable(List<AbstractUser> users) {
        tableModel.setRowCount(0);
        for (AbstractUser user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getUserType(),
                    user instanceof Student ? ((Student) user).getStudentNumber() : ((Staff) user).getStaffNumber()
            });
        }
    }

    private void showAddUserDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField numberField = new JTextField();
        String[] userTypes = {"ÖĞRENCİ", "PERSONEL"};
        JComboBox<String> userTypeBox = new JComboBox<>(userTypes);

        Object[] message = {
                "Ad:", firstNameField,
                "Soyad:", lastNameField,
                "E-posta:", emailField,
                "Numara:", numberField,
                "Kullanıcı Türü:", userTypeBox
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Kullanıcı Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                AbstractUser newUser;
                if ("ÖĞRENCİ".equalsIgnoreCase((String) userTypeBox.getSelectedItem())) {
                    newUser = new Student(0, firstNameField.getText(), lastNameField.getText(), emailField.getText(), "default123", numberField.getText());
                } else {
                    newUser = new Staff(0, firstNameField.getText(), lastNameField.getText(), emailField.getText(), "default123", numberField.getText());
                }
                boolean success = userDAO.addUser(newUser);
                if (success) {
                    updateUsersTable(userDAO.getAllUsers());
                    JOptionPane.showMessageDialog(frame, "Kullanıcı başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Kullanıcı eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Lütfen bir kullanıcı seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) usersTable.getValueAt(selectedRow, 0);
        AbstractUser userToUpdate = userDAO.getUserById(userId);
        if (userToUpdate == null) {
            JOptionPane.showMessageDialog(frame, "Kullanıcı bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField firstNameField = new JTextField(userToUpdate.getFirstName());
        JTextField lastNameField = new JTextField(userToUpdate.getLastName());
        JTextField emailField = new JTextField(userToUpdate.getEmail());
        JTextField numberField = new JTextField(userToUpdate instanceof Student ?
                ((Student) userToUpdate).getStudentNumber() : ((Staff) userToUpdate).getStaffNumber());
        JComboBox<String> userTypeBox = new JComboBox<>(new String[]{"ÖĞRENCİ", "PERSONEL"});
        userTypeBox.setSelectedItem(userToUpdate.getUserType().toUpperCase());

        Object[] message = {
                "Ad:", firstNameField,
                "Soyad:", lastNameField,
                "E-posta:", emailField,
                "Numara:", numberField,
                "Kullanıcı Türü:", userTypeBox
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Kullanıcı Güncelle", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                if ("ÖĞRENCİ".equalsIgnoreCase((String) userTypeBox.getSelectedItem())) {
                    userToUpdate = new Student(userToUpdate.getId(), firstNameField.getText(), lastNameField.getText(),
                            emailField.getText(), userToUpdate.getPassword(), numberField.getText());
                } else {
                    userToUpdate = new Staff(userToUpdate.getId(), firstNameField.getText(), lastNameField.getText(),
                            emailField.getText(), userToUpdate.getPassword(), numberField.getText());
                }
                boolean success = userDAO.updateUser(userToUpdate);
                if (success) {
                    updateUsersTable(userDAO.getAllUsers());
                    JOptionPane.showMessageDialog(frame, "Kullanıcı başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Kullanıcı güncellenemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Lütfen bir kullanıcı seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) usersTable.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(frame, "Bu kullanıcıyı silmek istediğinizden emin misiniz?", "Silme Onayı", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                updateUsersTable(userDAO.getAllUsers());
                JOptionPane.showMessageDialog(frame, "Kullanıcı başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Kullanıcı silinemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
