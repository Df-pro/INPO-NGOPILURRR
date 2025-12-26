/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;
import DAO.UserDAO;
import Model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 *
 * @author dwife
 */
public class AdminDashboard extends javax.swing.JFrame {

    private User currentUser;
    private DefaultTableModel tableModel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Admin Dashboard - " + user.getUsername());
        loadUserData();
        displayWelcomeMessage();
    }
    

    public AdminDashboard() {
        this.currentUser = null;
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void displayWelcomeMessage() {
        if (currentUser != null) {
            jLabel2.setText("Selamat Datang, " + currentUser.getUsername() + "!");
        }
    }
    
    private void loadUserData() {
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);
        

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        

        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
            };
            tableModel.addRow(row);
        }
    }
    
    private void editUser() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih user yang ingin diedit!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentUsername = (String) tableModel.getValueAt(selectedRow, 1);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 2);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Create edit dialog
        JDialog editDialog = new JDialog(this, "Edit User", true);
        editDialog.setLayout(new GridLayout(5, 2, 10, 10));
        editDialog.setSize(400, 250);
        editDialog.setLocationRelativeTo(this);
        
        JTextField txtUsername = new JTextField(currentUsername);
        JTextField txtEmail = new JTextField(currentEmail);
        
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"user", "admin"});
        cmbRole.setSelectedItem(currentRole);
        
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");
        
        editDialog.add(new JLabel("Username:"));
        editDialog.add(txtUsername);
        editDialog.add(new JLabel("Email:"));
        editDialog.add(txtEmail);
        editDialog.add(new JLabel("Role:"));
        editDialog.add(cmbRole);
        editDialog.add(btnSave);
        editDialog.add(btnCancel);
        
        btnSave.addActionListener(e -> {
            String newUsername = txtUsername.getText().trim();
            String newEmail = txtEmail.getText().trim();
            String newRole = (String) cmbRole.getSelectedItem();
            
            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog,
                    "Username dan email tidak boleh kosong!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UserDAO userDAO = new UserDAO();

            if (!newUsername.equals(currentUsername) && userDAO.usernameExists(newUsername)) {
                JOptionPane.showMessageDialog(editDialog,
                    "Username sudah digunakan!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
 
            if (!newEmail.equals(currentEmail) && userDAO.emailExists(newEmail)) {
                JOptionPane.showMessageDialog(editDialog,
                    "Email sudah terdaftar!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean updated = userDAO.updateUserProfile(userId, newUsername, newEmail);
            if (updated && !newRole.equals(currentRole)) {
                userDAO.updateUserRole(userId, newRole);
            }
            
            if (updated) {
                JOptionPane.showMessageDialog(editDialog,
                    "User berhasil diupdate!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadUserData(); // Refresh table
                editDialog.dispose();
            }
        });
        
        btnCancel.addActionListener(e -> editDialog.dispose());
        
        editDialog.setVisible(true);
    }
    
    private void deleteUser() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih user yang ingin dihapus!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        if (currentUser != null && userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this,
                "Tidak bisa menghapus akun sendiri!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah yakin ingin menghapus user: " + username + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO userDAO = new UserDAO();
            boolean deleted = userDAO.deleteUser(userId);
            
            if (deleted) {
                JOptionPane.showMessageDialog(this,
                    "User berhasil dihapus!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadUserData(); // Refresh table
            }
        }
    }
    
    private void addNewUser() {

        JDialog addDialog = new JDialog(this, "Tambah User Baru", true);
        addDialog.setLayout(new GridLayout(6, 2, 10, 10));
        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo(this);
        
        JTextField txtUsername = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"user", "admin"});
        
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");
        
        addDialog.add(new JLabel("Username:"));
        addDialog.add(txtUsername);
        addDialog.add(new JLabel("Email:"));
        addDialog.add(txtEmail);
        addDialog.add(new JLabel("Password:"));
        addDialog.add(txtPassword);
        addDialog.add(new JLabel("Konfirmasi:"));
        addDialog.add(txtConfirm);
        addDialog.add(new JLabel("Role:"));
        addDialog.add(cmbRole);
        addDialog.add(btnSave);
        addDialog.add(btnCancel);
        
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirm.getPassword());
            String role = (String) cmbRole.getSelectedItem();
            
            // Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(addDialog,
                    "Semua field harus diisi!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(addDialog,
                    "Password tidak cocok!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(addDialog,
                    "Password minimal 6 karakter!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UserDAO userDAO = new UserDAO();
            
            // Check if username exists
            if (userDAO.usernameExists(username)) {
                JOptionPane.showMessageDialog(addDialog,
                    "Username sudah digunakan!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if email exists
            if (userDAO.emailExists(email)) {
                JOptionPane.showMessageDialog(addDialog,
                    "Email sudah terdaftar!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setRole(role);
            
            // Register user
            boolean success = userDAO.register(newUser);
            
            if (success) {
                JOptionPane.showMessageDialog(addDialog,
                    "User berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadUserData(); // Refresh table
                addDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(addDialog,
                    "Gagal menambahkan user!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> addDialog.dispose());
        
        addDialog.setVisible(true);
    }
    
    private void showLogs() {
        JOptionPane.showMessageDialog(this,
            "Fitur Logs akan datang!\n" +
            "Fitur ini akan menampilkan aktivitas sistem.",
            "Logs",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText(" ADMIN DASHBOARD");

        jLabel2.setText("Selamat Datang Di Info Ngopi Lurr");

        jButton4.setText("Logout");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        jButton5.setText("Edit");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Delete");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Add new");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jButton5)
                .addGap(87, 87, 87)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addGap(105, 105, 105)
                .addComponent(jButton4)
                .addGap(44, 44, 44))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(299, 299, 299)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(267, 267, 267)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 675, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton4))
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        editUser();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        deleteUser();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        addNewUser();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_jButton4ActionPerformed

    
    
        private void formWindowClosing(java.awt.event.WindowEvent evt) {                                   

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah yakin ingin keluar?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.NO_OPTION) {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    } 
        
        


private void generateAdminCode() {
    UserDAO userDAO = new UserDAO();
    String newCode = userDAO.generateAdminCode(currentUser.getId());
    
    if (newCode != null) {
        JOptionPane.showMessageDialog(this,
            "Kode Admin Baru Berhasil Digenerate!\n" +
            "Kode: " + newCode + "\n\n" +
            " Simpan kode ini dengan aman!\n" +
            "Kode hanya bisa digunakan SATU KALI.",
            "Admin Code Generated",
            JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this,
            "Gagal generate kode admin!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

        private void showAdminCodes() {
            UserDAO userDAO = new UserDAO();
            List<String> availableCodes = userDAO.getAvailableAdminCodes();

            StringBuilder message = new StringBuilder();
            message.append("🛡️ KODE ADMIN YANG TERSEDIA:\n\n");

            if (availableCodes.isEmpty()) {
                message.append("Tidak ada kode admin yang tersedia.\n");
            } else {
                for (String code : availableCodes) {
                    message.append("• ").append(code).append("\n");
                }
            }

            message.append("\nTotal: ").append(availableCodes.size()).append(" kode");

            JOptionPane.showMessageDialog(this,
                message.toString(),
                "Available Admin Codes",
                JOptionPane.INFORMATION_MESSAGE);
        }
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
