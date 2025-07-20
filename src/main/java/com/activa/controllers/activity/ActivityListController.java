package com.activa.controllers.activity;

import com.activa.App;
import com.activa.models.Activity;
import com.activa.services.ActivityService;
import com.activa.utils.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class ActivityListController {

    @FXML private TableView<Activity> tableView;
    @FXML private TableColumn<Activity, String> titleColumn;
    @FXML private TableColumn<Activity, String> descriptionColumn;
    @FXML private TableColumn<Activity, String> startColumn;
    @FXML private TableColumn<Activity, String> endColumn;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private TextField searchField; // Referensi ke field pencarian

    private final ActivityService activityService = new ActivityService();
    private final ObservableList<Activity> activityList = FXCollections.observableArrayList();
    private FilteredList<Activity> filteredActivityList; // Daftar untuk memfilter

    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startFormatted"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endFormatted"));

        loadActivities();
        setupSearchFilter(); // Panggil metode untuk setup filter

        btnAdd.setOnAction(event -> handleAddActivity());
        btnEdit.setOnAction(event -> handleEditActivity());
        btnDelete.setOnAction(event -> handleDeleteActivity());
    }

    private void loadActivities() {
        // Memuat data ke dalam daftar utama
        activityList.setAll(activityService.getAllActivities());
    }

    /**
     * Mengatur fungsionalitas filter pencarian untuk TableView.
     */
    private void setupSearchFilter() {
        filteredActivityList = new FilteredList<>(activityList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredActivityList.setPredicate(activity -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                // Filter berdasarkan judul aktivitas
                return activity.getTitle().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Mengikat tabel ke daftar yang sudah difilter
        tableView.setItems(filteredActivityList);
    }

    private void handleAddActivity() {
        openActivityFormModal(null);
    }

    private void handleEditActivity() {
        Activity selectedActivity = tableView.getSelectionModel().getSelectedItem();
        if (selectedActivity == null) {
            Helper.showAlert("Peringatan", "Pilih aktivitas yang ingin diedit.", Alert.AlertType.WARNING);
            return;
        }
        openActivityFormModal(selectedActivity);
    }

    private void handleDeleteActivity() {
        Activity selectedActivity = tableView.getSelectionModel().getSelectedItem();
        if (selectedActivity == null) {
            Helper.showAlert("Peringatan", "Pilih aktivitas yang ingin dihapus.", Alert.AlertType.WARNING);
            return;
        }

        if (activityService.deleteActivity(selectedActivity.getId())) {
            loadActivities();
        }
    }

    private void openActivityFormModal(Activity activity) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("/views/activity/modal.fxml")));
            Parent root = loader.load();

            ActivityModalController controller = loader.getController();
            if (activity != null) {
                controller.initData(activity);
            }

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tableView.getScene().getWindow());
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setTitle(activity == null ? "Tambah Aktivitas Baru" : "Edit Aktivitas");

            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();

            loadActivities();

        } catch (IOException e) {
            e.printStackTrace();
            Helper.showAlert("Error", "Gagal memuat form aktivitas.", Alert.AlertType.ERROR);
        }
    }
}
