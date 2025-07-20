package com.activa.controllers.auth;

import com.activa.services.LoginService;
import com.activa.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginController {
    @FXML private Button btnClose;
    @FXML private Button btnMinimize;
    @FXML private AnchorPane rootPane;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private HBox topbar;

    private double xOffset = 0;
    private double yOffset = 0;
    private LoginService loginService;

    @FXML
    private void initialize() {
        this.loginService = new LoginService();
        setupDraggableWindow();

        // PERBAIKAN: Menambahkan listener untuk tombol Enter pada kedua field.
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submitLogin();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submitLogin();
            }
        });
    }

    private void setupDraggableWindow() {
        topbar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            topbar.setCursor(Cursor.CLOSED_HAND);
        });

        topbar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topbar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        topbar.setOnMouseReleased(event -> {
            topbar.setCursor(Cursor.HAND);
        });

        topbar.setOnMouseEntered(event -> {
            topbar.setCursor(Cursor.HAND);
        });
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Menangani aksi tombol login. Metode ini sekarang hanya memanggil submitLogin().
     */
    @FXML
    void handleLogin(ActionEvent event) {
        submitLogin();
    }

    /**
     * Logika inti untuk memproses login. Dipanggil oleh tombol login dan tombol Enter.
     */
    private void submitLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            Helper.showAlert("Login Error", "Username and Password cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        boolean isLoginSuccessful = loginService.login(username, password);

        if (isLoginSuccessful) {
            Helper.showAlert("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION);
            openDashboardWindow();
            closeCurrentStage();
        } else {
            Helper.showAlert("Login Failed", "Invalid username or password. Please try again.", Alert.AlertType.ERROR);
            passwordField.clear();
        }
    }

    /**
     * Membuka jendela utama aplikasi (Dashboard).
     */
    private void openDashboardWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/layout/base.fxml"));
            Parent dashboardRoot = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Activa - Dashboard");
            dashboardStage.setScene(new Scene(dashboardRoot));
            dashboardStage.setMaximized(true);
            dashboardStage.initStyle(StageStyle.UNDECORATED);
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Helper.showAlert("Login Failed", "Invalid username or password. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void openRegistrationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/club/registration.fxml"));
            Parent dashboardRoot = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Activa - Registration");
            dashboardStage.setScene(new Scene(dashboardRoot));

            dashboardStage.initStyle(StageStyle.UNDECORATED);
            closeCurrentStage();
            dashboardStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Helper.showAlert("Internal Error!", "Please contact the Admin!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Menutup stage login saat ini.
     */
    private void closeCurrentStage() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
