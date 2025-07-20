package com.activa.controllers.activity;

import com.activa.models.Activity;
import com.activa.services.ActivityService;
import com.activa.utils.Helper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ActivityModalController {

    @FXML private Text titleText;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final ActivityService activityService = new ActivityService();
    private Activity currentActivity;
    private boolean isEditMode = false;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public void initData(Activity activity) {
        this.currentActivity = activity;
        this.isEditMode = true;
        titleText.setText("Edit Aktivitas");

        titleField.setText(activity.getTitle());
        descriptionArea.setText(activity.getDescription());

        if (activity.getStart() != null) {
            startDatePicker.setValue(activity.getStart().toLocalDate());
            startTimeField.setText(activity.getStart().toLocalTime().format(timeFormatter));
        }
        if (activity.getEnd() != null) {
            endDatePicker.setValue(activity.getEnd().toLocalDate());
            endTimeField.setText(activity.getEnd().toLocalTime().format(timeFormatter));
        }
    }

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
            success = activityService.updateActivity(currentActivity.getId(), title, description, startDateTime, endDateTime);
        } else {
            Optional<Activity> newActivity = activityService.createActivity(title, description, startDateTime, endDateTime);
            success = newActivity.isPresent();
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
