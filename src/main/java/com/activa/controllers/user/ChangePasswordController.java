package com.activa.controllers.user;

import com.activa.models.User;
import com.activa.services.UserService;
import com.activa.utils.Helper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChangePasswordController {

    @FXML private Text usernameText;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private User currentUser;
    private final UserService userService = new UserService();

    /**
     * Menginisialisasi data untuk dialog, yaitu pengguna yang akan diubah passwordnya.
     * @param user Pengguna yang dipilih.
     */
    public void initData(User user) {
        this.currentUser = user;
        usernameText.setText("untuk pengguna: " + user.getName());
    }

    /**
     * Menangani aksi saat tombol 'Simpan' ditekan.
     */
    @FXML
    private void handleSave() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Helper.showAlert("Validasi Gagal", "Semua field harus diisi.", Alert.AlertType.WARNING);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Helper.showAlert("Validasi Gagal", "Password baru dan konfirmasi tidak cocok.", Alert.AlertType.WARNING);
            return;
        }

        // Panggil service untuk update password
        boolean success = userService.updatePassword(currentUser.getId(), newPassword);

        if (success) {
            closeWindow();
        }
    }

    /**
     * Menangani aksi saat tombol 'Batal' ditekan.
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Menutup jendela modal.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
