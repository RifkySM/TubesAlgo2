package com.activa.controllers.user;

import com.activa.models.User;
import com.activa.services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class UserModalController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Text titleText;
    @FXML private Label passwordLabel;
    @FXML private GridPane gridPane;
    @FXML private RowConstraints passwordRow;

    private final UserService userService = new UserService();
    private User currentUser;
    private boolean isEditMode = false;

    @FXML
    private void initialize() {
        // Isi ComboBox dengan pilihan peran
        roleComboBox.setItems(FXCollections.observableArrayList("Admin", "Member"));
    }

    /**
     * Menginisialisasi data form untuk mode edit.
     * @param user Pengguna yang akan diedit.
     */
    public void initData(User user) {
        this.currentUser = user;
        this.isEditMode = true;

        // Isi field dengan data pengguna yang ada
        nameField.setText(user.getName());
        usernameField.setText(user.getUsername());
        roleComboBox.setValue(user.getRole());

        // Ubah judul dan sembunyikan field password
        titleText.setText("Edit Pengguna");
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);

        // Hapus baris password dari grid agar layout rapi
        if (gridPane.getRowConstraints().contains(passwordRow)) {
            gridPane.getRowConstraints().remove(passwordRow);
        }
    }

    /**
     * Menangani aksi saat tombol 'Simpan' ditekan.
     */
    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();

        boolean success;

        if (isEditMode) {
            success = userService.updateUser(currentUser.getId(), name, username, role);
        } else {
            Optional<User> newUser = userService.createUser(name, username, password, role);
            success = newUser.isPresent();
        }

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
