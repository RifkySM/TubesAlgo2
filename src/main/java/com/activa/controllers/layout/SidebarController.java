package com.activa.controllers.layout;

import com.activa.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    // PERBAIKAN: Mengubah tipe dari Button menjadi Text agar sesuai dengan FXML
    @FXML private Text usernameText;
    @FXML private Text roleText;

    @FXML
    private void initialize() {
        // Mengatur tombol dashboard sebagai aktif saat pertama kali dimuat
        setActiveButton(btnDashboard);

        btnDashboard.setOnAction(e -> handleButtonClick(btnDashboard, this::setDashboardContent));
        btnListMember.setOnAction(e -> handleButtonClick(btnListMember, this::setMemberListContent));
        btnListRequest.setOnAction(e -> handleButtonClick(btnListRequest, this::setRequestListContent));
        btnListActivity.setOnAction(e -> handleButtonClick(btnListActivity, this::setActivityListContent));
        btnListAnnouncement.setOnAction(e -> handleButtonClick(btnListAnnouncement, this::setAnnouncementListContent));
        btnAttendance.setOnAction(e -> handleButtonClick(btnAttendance, this::setAttendanceContent));
        btnReport.setOnAction(e -> handleButtonClick(btnReport, this::setBtnReportContent));
        btnUserList.setOnAction(e -> handleButtonClick(btnUserList, this::setBtnUserListContent));

        btnLogout.setOnAction(e -> handleLogout());

        // Mengisi teks username dan role saat inisialisasi
        setUsernameText(SessionManager.getInstance().getCurrentUser().getName());
        setRoleText(SessionManager.getInstance().getCurrentUser().getRole());
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
        // Hapus kelas 'active' dari semua tombol
        btnDashboard.getStyleClass().remove("active");
        btnListMember.getStyleClass().remove("active");
        btnListRequest.getStyleClass().remove("active");
        btnListActivity.getStyleClass().remove("active");
        btnListAnnouncement.getStyleClass().remove("active");
        btnAttendance.getStyleClass().remove("active");
        btnReport.getStyleClass().remove("active");
        btnLogout.getStyleClass().remove("active");
        btnUserList.getStyleClass().remove("active");

        // Tambahkan kelas 'active' ke tombol yang diklik
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

    private void handleLogout() {
        if (baseController != null) {
            baseController.handleLogout();
        } else {
            System.err.println("Error: BaseController tidak di-set di SidebarController. Tidak dapat melakukan logout.");
        }
    }
}
