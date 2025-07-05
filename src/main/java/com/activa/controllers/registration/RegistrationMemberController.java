package com.activa.controllers.registration;

import com.activa.services.MemberRegistrationService;
import com.activa.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RegistrationMemberController {
    @FXML private Button btnClose;
    @FXML private Button btnMinimize;
    @FXML private AnchorPane rootPane;
    @FXML private HBox topbar;

    private double xOffset = 0;
    private double yOffset = 0;

    private MemberRegistrationService memberRegistrationService;

    @FXML
    private void initialize() {
        this.memberRegistrationService = new MemberRegistrationService();
        setupDraggableWindow();
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

    @FXML
    private void openLoginWindow () {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/login.fxml"));
            Parent dashboardRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Activa - Login");
            stage.setScene(new Scene(dashboardRoot));

            stage.initStyle(StageStyle.UNDECORATED);
            closeCurrentStage();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Helper.showAlert("Internal Error!", "Please contact the Admin!", Alert.AlertType.ERROR);
        }
    }

    private void closeCurrentStage() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
