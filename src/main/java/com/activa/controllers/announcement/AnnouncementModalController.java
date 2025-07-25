package com.activa.controllers.announcement;

import com.activa.models.Activity;
import com.activa.models.Announcement;
import com.activa.services.AnnouncementService;
import com.activa.utils.Helper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class AnnouncementModalController {

    // FXML bindings for UI elements
    @FXML private Text titleText;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    // Service for business logic
    private AnnouncementService announcementService;

    // Data model for the current announcement being edited
    private Announcement currentAnnouncement;
    private boolean isEditMode;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    /**
     * Initializes the modal with data for an existing announcement.
     * @param announcement The announcement to be edited.
     */
    public void initData(Announcement announcement) {
        this.currentAnnouncement = announcement;
        this.isEditMode = true;
        titleText.setText("Edit Announcement");

        titleField.setText(announcement.getTitle());
        descriptionArea.setText(announcement.getDescription());

        if (announcement.getStart() != null) {
            startDatePicker.setValue(announcement.getStart().toLocalDate());
            startTimeField.setText(announcement.getStart().toLocalTime().format(timeFormatter));
        }
        if (announcement.getEnd() != null) {
            endDatePicker.setValue(announcement.getEnd().toLocalDate());
            endTimeField.setText(announcement.getEnd().toLocalTime().format(timeFormatter));
        }
    }

    /**
     * Handles the action of clicking the 'Save' button.
     */
    @FXML
    private void handleSave() {
        String title = titleField.getText();
        String description = descriptionArea.getText();

        LocalDateTime startDateTime = parseDateTime(startDatePicker, startTimeField, "Waktu Mulai");
        if (startDateTime == null && startDatePicker.getValue() != null) return; // Error parsing, stop process

        LocalDateTime endDateTime = parseDateTime(endDatePicker, endTimeField, "Waktu Selesai");
        if (endDateTime == null && endDatePicker.getValue() != null) return; // Error parsing, stop process

        boolean success;
        if (isEditMode) {
            success = announcementService.updateAnnouncement(currentAnnouncement.getId(), title, description, startDateTime, endDateTime);
        } else {
            Optional<Announcement> newAnnouncement = announcementService.createAnnouncement(title, description, startDateTime, endDateTime);
            success = newAnnouncement.isPresent();
        }

        if (success) {
            closeWindow();
        }
    }

    private LocalDateTime parseDateTime(DatePicker datePicker, TextField timeField, String fieldName) {
        LocalDate date = datePicker.getValue();
        String timeText = timeField.getText();

        if (date == null && (timeText == null || timeText.isBlank())) {
            return null; // Field sengaja dikosongkan
        }
        if (date == null) {
            Helper.showAlert("Error", "Tanggal untuk '" + fieldName + "' harus diisi jika waktu diisi.", Alert.AlertType.ERROR);
            return null;
        }
        if (timeText == null || timeText.isBlank()) {
            timeText = "00:00"; // Default time if empty
        }

        try {
            LocalTime time = LocalTime.parse(timeText, timeFormatter);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            Helper.showAlert("Format Salah", "Format waktu untuk '" + fieldName + "' harus HH:mm (contoh: 14:30).", Alert.AlertType.ERROR);
            return null;
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
