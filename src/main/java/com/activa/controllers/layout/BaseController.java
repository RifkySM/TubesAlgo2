package com.activa.controllers.layout;

import com.activa.utils.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BaseController {
    @FXML private Button btnClose;
    @FXML private Button btnMaximize;
    @FXML private Button btnMinimize;
    @FXML private StackPane contentPane;
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane sidebar;

    private double xOffset = 0;
    private double yOffset = 0;

    private VBox sidebarVBox;
    private boolean isSidebarExpanded = true;
    private static final double EXPANDED_WIDTH = 180.0;
    private static final double COLLAPSED_WIDTH = 60.0;

    private SessionManager sessionManager = SessionManager.getInstance();

    private SidebarController sidebarController;

    @FXML
    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/views/layout/sidebar.fxml")));
            Node sidebarContent = loader.load();
            sidebarVBox = (VBox) sidebarContent;
            sidebarController = loader.getController();
            sidebarController.setBaseController(this);

            sidebarVBox.setPrefWidth(EXPANDED_WIDTH);
            sidebar.getChildren().setAll(sidebarVBox);
            AnchorPane.setTopAnchor(sidebarVBox, 0.0);
            AnchorPane.setBottomAnchor(sidebarVBox, 0.0);
            AnchorPane.setLeftAnchor(sidebarVBox, 0.0);
            AnchorPane.setRightAnchor(sidebarVBox, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        setupDraggableWindow();

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                Stage stage = (Stage) newScene.getWindow();
            if (stage != null) {
                toggleSidebar(!stage.isMaximized());
                stage.maximizedProperty().addListener((maxObs, wasMaximized, isNowMaximized) -> {
                    toggleSidebar(!isNowMaximized);
                });
            }
        });
    }

    private void toggleSidebar(boolean expand) {
        if (expand == isSidebarExpanded) {
            return;
        }

        double targetWidth = expand ? EXPANDED_WIDTH : COLLAPSED_WIDTH;

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(sidebarVBox.prefWidthProperty(), targetWidth);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        List<Label> labels = sidebarVBox.lookupAll(".label").stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .toList();

        for (Label label : labels) {
            label.setVisible(expand);
            label.setManaged(expand);
        }

        isSidebarExpanded = expand;
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
    void handleMaximize(ActionEvent event) {
        Stage stage = (Stage) btnMaximize.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) btnMinimize.getScene().getWindow();
        stage.setIconified(true);
    }
}