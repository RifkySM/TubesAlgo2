package com.activa.controllers.auth;

import com.activa.services.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegistrationController {
    @FXML
    private Button btnClose;
    @FXML private Button btnMinimize;
    @FXML private AnchorPane rootPane;

    private double xOffset = 0;
    private double yOffset = 0;
    private LoginService loginService;

    @FXML
    private void initialize() {
        this.loginService = new LoginService();
        setupDraggableWindow();
    }

    private void setupDraggableWindow() {
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
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
}
