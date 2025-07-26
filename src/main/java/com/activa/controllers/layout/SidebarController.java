package com.activa.controllers.layout;

import com.activa.models.User;
import com.activa.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text; // PERBAIKAN: Mengimpor kelas Text

import java.io.IOException;

public class SidebarController {
    private BaseController baseController;

    @FXML private Button btnAttendance;
    @FXML private Button btnDashboard;
    @FXML private Button btnListActivity;
    @FXML private Button btnListAnnouncement;
    @FXML private Button btnListMember;
    @FXML private Button btnListRequest;
    @FXML private Button btnLogout;
    @FXML private Button btnReport;
    @FXML private Button btnUserList;
    @FXML private Button btnProfile;

    @FXML private TitledPane menuActivity;
    @FXML private TitledPane menuAnnouncement;
    @FXML private TitledPane menuMember;
    @FXML private TitledPane menuReport;
    @FXML private TitledPane menuUser;

    @FXML private Text usernameText;
    @FXML private Text roleText;

    private User currentUser;
    private final SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    private void initialize() {
        setActiveButton(btnDashboard);

        currentUser = sessionManager.getCurrentUser();
        setUsernameText(currentUser.getUsername());
        setRoleText(currentUser.getRole());
        btnLogout.setOnAction(e -> handleLogout());
        btnDashboard.setOnAction(e -> handleButtonClick(btnDashboard, this::setDashboardContent));

        if (!currentUser.getRole().equals("Admin")) {
            menuActivity.setVisible(false);
            menuAnnouncement.setVisible(false);
            menuMember.setVisible(false);
            menuReport.setVisible(false);
            menuUser.setVisible(false);
            btnProfile.setVisible(false);
            return;
        }

        btnListMember.setOnAction(e -> handleButtonClick(btnListMember, this::setMemberListContent));
        btnListRequest.setOnAction(e -> handleButtonClick(btnListRequest, this::setRequestListContent));
        btnListActivity.setOnAction(e -> handleButtonClick(btnListActivity, this::setActivityListContent));
        btnListAnnouncement.setOnAction(e -> handleButtonClick(btnListAnnouncement, this::setAnnouncementListContent));
        btnAttendance.setOnAction(e -> handleButtonClick(btnAttendance, this::setAttendanceContent));
        btnReport.setOnAction(e -> handleButtonClick(btnReport, this::setBtnReportContent));
        btnUserList.setOnAction(e -> handleButtonClick(btnUserList, this::setBtnUserListContent));
        btnProfile.setOnAction(e -> handleButtonClick(btnProfile, this::setProfileContent));
    }

    private void handleButtonClick(Button button, ContentLoader contentLoader) {
        setActiveButton(button);
        try {
            contentLoader.load();
        } catch (IOException ex) {
            System.err.println("Failed to load view for button: " + button.getText());
            ex.printStackTrace();
        }
    }

    private void setUsernameText(String username) {
        usernameText.setText(username);
    }
    private void setRoleText(String role) {
        roleText.setText(role);
    }

    @FunctionalInterface
    interface ContentLoader {
        void load() throws IOException;
    }

    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("active");
        btnListMember.getStyleClass().remove("active");
        btnListRequest.getStyleClass().remove("active");
        btnListActivity.getStyleClass().remove("active");
        btnListAnnouncement.getStyleClass().remove("active");
        btnAttendance.getStyleClass().remove("active");
        btnReport.getStyleClass().remove("active");
        btnLogout.getStyleClass().remove("active");
        btnUserList.getStyleClass().remove("active");
        btnProfile.getStyleClass().remove("active");

        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    public void setDashboardContent() throws IOException { baseController.setDashboardContent(); }
    public void setMemberListContent() throws IOException { baseController.setMemberListContent(); }
    public void setRequestListContent() throws IOException { baseController.setRequestListContent(); }
    public void setActivityListContent() throws IOException { baseController.setActivityListContent(); }
    public void setAnnouncementListContent() throws IOException { baseController.setAnnouncementListContent(); }
    public void setAttendanceContent() throws IOException { baseController.setAttendanceContent(); }
    public void setBtnUserListContent() throws IOException { baseController.setUserListContent(); }
    public void setBtnReportContent() throws IOException { baseController.setReportContent(); }
    public void setProfileContent() throws IOException { baseController.setProfileContent(); }

    private void handleLogout() {
        if (baseController != null) {
            baseController.handleLogout();
        } else {
            System.err.println("Error: BaseController tidak di-set di SidebarController. Tidak dapat melakukan logout.");
        }
    }
}
