package com.activa.controllers.announcement;

import com.activa.App;
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
    private final AnnouncementService announcementService = new AnnouncementService();

    // Data lists for the table
    private final ObservableList<Announcement> announcementList = FXCollections.observableArrayList();
    private FilteredList<Announcement> filteredAnnouncementList;

    @FXML
    private void initialize() {
        // Set up table columns to bind to Announcement properties
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startFormatted"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endFormatted"));

        // Set up the search filter and bind the list to the table
        setupSearchFilter();
        // Load the initial data from the service
        loadAnnouncements();

        // Set button actions
        btnAdd.setOnAction(event -> handleAddAnnouncement());
        btnEdit.setOnAction(event -> handleEditAnnouncement());
        btnDelete.setOnAction(event -> handleDeleteAnnouncement());
    }

    /**
     * Loads announcements from the service and populates the master list.
     */
    private void loadAnnouncements() {
        announcementList.setAll(announcementService.getAllAnnouncements());
    }

    /**
     * Sets up the search filter and binds the filtered list to the TableView.
     */
    private void setupSearchFilter() {
        filteredAnnouncementList = new FilteredList<>(announcementList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAnnouncementList.setPredicate(announcement -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return announcement.getTitle().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // **FIX:** Bind the filtered list to the TableView to ensure data is displayed.
        tableView.setItems(filteredAnnouncementList);
    }

    /**
     * Handles the action of clicking the 'Add' button.
     */
    private void handleAddAnnouncement() {
        // **FIX:** Call the modal form to add a new announcement.
        openAnnouncementFormModal(null);
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

        if (Helper.showConfirmation("Confirm Deletion", "Are you sure you want to delete this announcement?")) {
            if (announcementService.deleteAnnouncement(selectedAnnouncement.getId())){
                loadAnnouncements();
            }
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
