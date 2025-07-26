package com.activa.controllers.club;

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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProfileController {
    @FXML private Button btnClose;
    @FXML private Button btnMinimize;
    @FXML private Text clubName;
    @FXML private AnchorPane rootPane;
    @FXML private Label textBenefit;
    @FXML private Label textDescription;
    @FXML private Label textVisiMisi;
    @FXML private HBox topbar;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
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

    private void closeCurrentStage() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
