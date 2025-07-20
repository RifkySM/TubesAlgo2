package com.activa.controllers.user;

import com.activa.App;
import com.activa.models.User;
import com.activa.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class UserListController {

    @FXML private Button btnAdd;
    @FXML private Button btnChangePassword;
    @FXML private Button btnDelete;
    @FXML private Button btnEdit;
    @FXML private TableView<User> tableView;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, LocalDateTime> updatedAtColumn;
    @FXML private TextField searchField; // Referensi ke field pencarian

    private final UserService userService = new UserService();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredUserList; // Daftar untuk memfilter

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        loadUsers();
        setupSearchFilter(); // Panggil metode untuk setup filter

        btnAdd.setOnAction(event -> handleAddUser());
        btnEdit.setOnAction(event -> handleEditUser());
        btnChangePassword.setOnAction(event -> handleChangePassword());
        btnDelete.setOnAction(event -> handleDeleteUser());
    }

    private void loadUsers() {
        // Memuat data ke dalam daftar utama
        userList.setAll(userService.getAllUsers());
    }

    /**
     * Mengatur fungsionalitas filter pencarian untuk TableView.
     */
    private void setupSearchFilter() {
        filteredUserList = new FilteredList<>(userList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUserList.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter cocok dengan nama
                } else if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter cocok dengan username
                }
                return false; // Tidak ada yang cocok
            });
        });

        // Mengikat tabel ke daftar yang sudah difilter
        tableView.setItems(filteredUserList);
    }

    private void handleAddUser() {
        openUserFormModal(null);
    }

    private void handleEditUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih pengguna yang ingin diedit.");
            return;
        }
        openUserFormModal(selectedUser);
    }

    private void openChangePasswordModal(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("/views/user/change_password.fxml")));
            Parent root = loader.load();
            ChangePasswordController controller = loader.getController();
            controller.initData(user);
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tableView.getScene().getWindow());
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setTitle("Ubah Password");
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat form ubah password.");
        }
    }

    private void handleChangePassword() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih pengguna yang akan diubah kata sandinya.");
            return;
        }
        openChangePasswordModal(selectedUser);
    }

    private void handleDeleteUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih pengguna yang ingin dihapus.");
            return;
        }
        if (userService.deleteUser(selectedUser.getId())) {
            loadUsers();
        }
    }

    private void openUserFormModal(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("/views/user/modal.fxml")));
            Parent root = loader.load();
            UserModalController controller = loader.getController();
            if (user != null) {
                controller.initData(user);
            }
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tableView.getScene().getWindow());
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setTitle(user == null ? "Tambah Pengguna Baru" : "Edit Pengguna");
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat form pengguna.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
