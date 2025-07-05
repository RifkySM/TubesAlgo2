package com.activa.controllers.layout;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class SidebarController {
    private BaseController baseController;

    @FXML private Button btnDashboard;
    @FXML private Button btnListMember;
    @FXML private Button btnListRequest;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {
        btnDashboard.setOnAction(e -> {
            setActiveButton(btnDashboard);
            try {
                setDashboardContent();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnListMember.setOnAction(e -> {
            setActiveButton(btnListMember);
            try {
                setMemberListContent();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnListRequest.setOnAction(e -> {
            setActiveButton(btnListRequest);
            try {
                setRequestListContent();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        btnLogout.setOnAction(e -> handleLogout());
    }

    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    /**
     * Set the given button as active and remove the active state from others.
     *
     * @param activeButton the button to activate
     */
    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("active");
        btnListMember.getStyleClass().remove("active");
        btnListRequest.getStyleClass().remove("active");
        btnLogout.getStyleClass().remove("active");

        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }

    public void setDashboardContent() throws IOException {
        baseController.setDashboardContent();
    }
    public void setMemberListContent() throws IOException {
        baseController.setMemberListContent();
    }
    public void setRequestListContent() throws IOException {
        baseController.setRequestListContent();
    }

    private void handleLogout() {}
}
