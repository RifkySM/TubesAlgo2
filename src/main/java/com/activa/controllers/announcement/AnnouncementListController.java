package com.activa.controllers.announcement;

import com.activa.App;
import com.activa.controllers.activity.ActivityModalController;
import com.activa.models.Activity;
import com.activa.models.Announcement;
import com.activa.services.AnnouncementService;
import com.activa.utils.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class AnnouncementListController {

    // FXML bindings for UI elements
    @FXML private TableView<Announcement> tableView;
    @FXML private TableColumn<Announcement, String> titleColumn;
    @FXML private TableColumn<Announcement, String> descriptionColumn;
    @FXML private TableColumn<Announcement, String> startColumn;
    @FXML private TableColumn<Announcement, String> endColumn;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private TextField searchField;

    // Service for business logic
    private AnnouncementService announcementService = new AnnouncementService();

    // Data lists for the table
    private ObservableList<Announcement> announcementList = FXCollections.observableArrayList();
    private FilteredList<Announcement> filteredAnnouncementList;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startFormatted"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endFormatted"));

        loadAnnouncements();
        setupSearchFilter(); // Panggil metode untuk setup filter

        btnAdd.setOnAction(event -> handleAddAnnouncement());
        btnEdit.setOnAction(event -> handleEditAnnouncement());
        btnDelete.setOnAction(event -> handleDeleteAnnouncement());
    }

    /**
     * Loads announcements from the service and populates the table.
     */
    private void loadAnnouncements() {
        announcementList.setAll(announcementService.getAllAnnouncements());
    }

    /**
     * Sets up the search filter functionality for the TableView.
     */
    private void setupSearchFilter() {
        filteredAnnouncementList = new FilteredList<>(announcementList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAnnouncementList.setPredicate(activity -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                // Filter berdasarkan judul aktivitas
                return activity.getTitle().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Mengikat tabel ke daftar yang sudah difilter
    }

    /**
     * Handles the action of clicking the 'Add' button.
     */
    private void handleAddAnnouncement() {
        // Method body is intentionally left empty as requested.
    }

    /**
     * Handles the action of clicking the 'Edit' button.
     */
    private void handleEditAnnouncement() {
        Announcement selectedAnnouncement = tableView.getSelectionModel().getSelectedItem();
        if (selectedAnnouncement == null) {
            Helper.showAlert("Peringatan", "Pilih announcement yang ingin di edit.", Alert.AlertType.WARNING);
            return;
        }
        openAnnouncementFormModal(selectedAnnouncement);
    }

    /**
     * Handles the action of clicking the 'Delete' button.
     */
    private void handleDeleteAnnouncement() {
        Announcement selectedAnnouncement = tableView.getSelectionModel().getSelectedItem();
        if (selectedAnnouncement == null) {
            Helper.showAlert("Peringatan", "Pilih announcement yang ingin di hapus", Alert.AlertType.WARNING);
            return;
        }
        if (announcementService.deleteAnnouncement(selectedAnnouncement.getId())){
            loadAnnouncements();
        }
    }

    /**
     * Opens the modal form for adding or editing an announcement.
     * @param announcement The announcement to edit, or null to add a new one.
     */
    private void openAnnouncementFormModal(Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("/views/announcement/modal.fxml")));
            Parent root = loader.load();

            AnnouncementModalController controller = loader.getController();
            if (announcement != null) {
                controller.initData(announcement);
            }

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tableView.getScene().getWindow());
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setTitle(announcement == null ? "Tambah Pengumuman Baru" : "Edit Pengumuman");

            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();

            loadAnnouncements();

        } catch (IOException e) {
            e.printStackTrace();
            Helper.showAlert("Error", "Gagal memuat form pengumuman.", Alert.AlertType.ERROR);
        }
    }
}
