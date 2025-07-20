package com.activa.controllers.report;

import com.activa.models.Activity;
import com.activa.services.ActivityService;
import com.activa.services.ReportService;
import com.activa.utils.Helper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ReportController {

    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private VBox attendanceFilterBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<Activity> activityFilterComboBox;
    @FXML private Button btnGenerate;
    @FXML private TextArea reportTextArea;

    private final ReportService reportService = new ReportService();
    private final ActivityService activityService = new ActivityService(); // Untuk mengisi filter

    @FXML
    private void initialize() {
        // Isi pilihan jenis laporan
        reportTypeComboBox.setItems(FXCollections.observableArrayList("Laporan Aktivitas", "Laporan Absensi"));

        // Listener untuk menampilkan/menyembunyikan filter
        reportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isAttendanceReport = "Laporan Absensi".equals(newVal);
            attendanceFilterBox.setVisible(isAttendanceReport);
            attendanceFilterBox.setManaged(isAttendanceReport);
        });

        // Setup ComboBox untuk filter kegiatan
        activityFilterComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Activity a) { return a == null ? "Semua Kegiatan" : a.getTitle(); }
            @Override public Activity fromString(String s) { return null; }
        });
        activityFilterComboBox.setItems(FXCollections.observableArrayList(activityService.getAllActivities()));
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = reportTypeComboBox.getSelectionModel().getSelectedItem();

        if (reportType == null) {
            Helper.showAlert("Peringatan", "Silakan pilih jenis laporan terlebih dahulu.", Alert.AlertType.WARNING);
            return;
        }

        String reportContent = "";
        if ("Laporan Aktivitas".equals(reportType)) {
            reportContent = reportService.generateActivityReport();
        } else if ("Laporan Absensi".equals(reportType)) {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate == null || endDate == null) {
                Helper.showAlert("Peringatan", "Silakan tentukan periode tanggal untuk laporan absensi.", Alert.AlertType.WARNING);
                return;
            }
            if (startDate.isAfter(endDate)) {
                Helper.showAlert("Peringatan", "Tanggal mulai tidak boleh setelah tanggal selesai.", Alert.AlertType.WARNING);
                return;
            }

            Activity filterActivity = activityFilterComboBox.getSelectionModel().getSelectedItem();
            reportContent = reportService.generateAttendanceReport(startDate, endDate, filterActivity);
        }

        reportTextArea.setText(reportContent);
    }
}
