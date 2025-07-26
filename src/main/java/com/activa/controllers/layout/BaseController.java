package com.activa.controllers.layout;

import com.activa.App;
import com.activa.utils.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BaseController {
    @FXML private Button btnClose;
    @FXML private Button btnMaximize;
    @FXML private Button btnMinimize;
    @FXML private StackPane contentPane;
    @FXML private BorderPane rootPane;
    @FXML private AnchorPane sidebar;

    private double xOffset = 0;
    private double yOffset = 0;

    private VBox sidebarVBox;
    private boolean isSidebarExpanded = true;
    private static final double EXPANDED_WIDTH = 180.0;
    private static final double COLLAPSED_WIDTH = 60.0;

    private final SessionManager sessionManager = SessionManager.getInstance();

    private SidebarController sidebarController;

    @FXML
    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/views/layout/sidebar.fxml")));
            Node sidebarContent = loader.load();
            sidebarVBox = (VBox) sidebarContent;
            sidebarController = loader.getController();
            sidebarController.setBaseController(this);

            sidebar.setPrefWidth(EXPANDED_WIDTH);

            sidebar.getChildren().setAll(sidebarVBox);
            AnchorPane.setTopAnchor(sidebarVBox, 0.0);
            AnchorPane.setBottomAnchor(sidebarVBox, 0.0);
            AnchorPane.setLeftAnchor(sidebarVBox, 0.0);
            AnchorPane.setRightAnchor(sidebarVBox, 0.0);

            setDashboardContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupDraggableWindow();

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                if (stage != null) {

                    stage.maximizedProperty().addListener((maxObs, wasMaximized, isNowMaximized) -> {
                        if (isNowMaximized) {
                            toggleSidebar(false);
                        } else {
                            toggleSidebar(true);
                        }
                    });
                    toggleSidebar(!stage.isMaximized());
                }
            }
        });
    }

    private void toggleSidebar(boolean expand) {
        if (expand == isSidebarExpanded) {
            return;
        }

        double targetWidth = expand ? EXPANDED_WIDTH : COLLAPSED_WIDTH;

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(sidebar.prefWidthProperty(), targetWidth);
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
        Node topBar = rootPane.getTop();
        if (topBar != null) {
            topBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            topBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                if (!stage.isMaximized()) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
        }
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

    public void setContent(Node node) {
        if (node instanceof Region) {
            Region region = (Region) node;
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        contentPane.getChildren().setAll(node);
    }

    public Parent getView(String viewPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(viewPath));
        return loader.load();
    }

    public void setDashboardContent() throws IOException {
        Parent view = getView("/views/dashboard/index.fxml");
        setContent(view);
    }

    public void setMemberListContent() throws IOException {
        Parent view = getView("/views/club/member_list.fxml");
        setContent(view);
    }

    public void setRequestListContent() throws IOException {
        Parent view = getView("/views/club/approval/index.fxml");
        setContent(view);
    }

    public void setActivityListContent() throws IOException {
        Parent view = getView("/views/activity/index.fxml");
        setContent(view);
    }

    public void setAnnouncementListContent() throws IOException {
        Parent view = getView("/views/announcement/index.fxml");
        setContent(view);
    }

    public void setAttendanceContent() throws IOException {
        Parent view = getView("/views/attendance/index.fxml");
        setContent(view);
    }

    public void setUserListContent() throws IOException {
        Parent view = getView("/views/user/index.fxml");
        setContent(view);
    }

    public void setReportContent() throws IOException {
        Parent view = getView("/views/report/index.fxml");
        setContent(view);
    }
    public void setProfileContent() throws IOException {
        Parent view = getView("/views/club/profile_form.fxml");
        setContent(view);
    }

    public void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Konfirmasi Logout");
        confirmation.setHeaderText("Anda yakin ingin logout?");
        confirmation.setContentText("Sesi Anda akan berakhir dan Anda akan kembali ke halaman login.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sessionManager.clearSession();

                Stage currentStage = (Stage) rootPane.getScene().getWindow();
                Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("/views/auth/login.fxml")));
                Scene loginScene = new Scene(loginRoot);

                currentStage.setScene(loginScene);
                currentStage.setTitle("Activa - Login");
                currentStage.centerOnScreen();

            } catch (IOException | NullPointerException e) {
                System.err.println("Gagal memuat tampilan login: /views/auth/login.fxml");
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Logout Gagal");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Tidak dapat memuat layar login.");
                errorAlert.showAndWait();
            }
        }
    }
}
