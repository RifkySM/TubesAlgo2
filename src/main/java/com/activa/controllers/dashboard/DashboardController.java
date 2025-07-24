package com.activa.controllers.dashboard;

import com.activa.models.Activity;
import com.activa.models.Announcement;
import com.activa.services.DashboardService;
import com.activa.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class DashboardController {
    public DashboardService dashboardService = new DashboardService();
    @FXML
    private Text ActiveCount;

    @FXML
    private TableColumn<Activity, String> ActivityCol;

    @FXML
    private HBox AnnouncementList;

    @FXML
    private TableColumn<Activity, String> EndCol;

    @FXML
    private Text InActiveCount;

    @FXML
    private Text PendingRequestCount;

    @FXML
    private TableColumn<Activity, String> StartCol;

    @FXML
    private TableView<Activity> TableView;

    @FXML
    private Text date;

    @FXML
    private Text text;

    @FXML
    public void initialize(){
        ActiveCount.setText(String.valueOf(dashboardService.getMemberCount(true)));
        InActiveCount.setText(String.valueOf(dashboardService.getMemberCount(false)));
        PendingRequestCount.setText(String.valueOf(dashboardService.getRequestCount()));
        setAnnouncementList();
        SetActivity();

        date.setText(getCurrentDate());




        text.setText("Welcome " + SessionManager.getInstance().getCurrentUser().getName());
    }

    public void setAnnouncementList() {
        List<Announcement> announcements = dashboardService.getAnnouncement();

        AnnouncementList.getChildren().clear(); // Kosongkan dulu kalau ada isi sebelumnya

        for (Announcement announcement : announcements) {
            VBox box = createAnnouncementBox(announcement.getTitle(), announcement.getDescription());
            AnnouncementList.getChildren().add(box);
        }
    }

    public String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();

        // Create a Locale for Indonesia (Bahasa Indonesia)
        Locale indonesiaLocale = new Locale("id", "ID");

        // 1. Format using a standard, full-length style for the locale
        DateTimeFormatter fullFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .withLocale(indonesiaLocale);
        String formattedDateFull = currentDate.format(fullFormatter);
        System.out.println("Standard Full Format: " + formattedDateFull);

        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", indonesiaLocale);
        String formattedDateCustom = currentDate.format(customFormatter);
        System.out.println("Custom Pattern Format: " + formattedDateCustom);

        return formattedDateCustom;
    }

    private VBox createAnnouncementBox(String title, String description) {
        VBox vBox = new VBox();
        vBox.setPrefWidth(923);
        vBox.setPrefHeight(178);
        vBox.setPadding(new Insets(30));
        vBox.getStyleClass().addAll("bg-rounded-3xl", "bg-amber-100");

        Text titleText = new Text(title);
        titleText.setFont(Font.font(25));
        titleText.setStrokeType(StrokeType.OUTSIDE);
        titleText.setStrokeWidth(0);
        titleText.getStyleClass().add("font-bold");

        Text count = new Text(description);
        count.setFont(Font.font(18));
        count.setStrokeType(StrokeType.OUTSIDE);
        count.setStrokeWidth(0);

        vBox.getChildren().addAll(titleText, count);

        return vBox;
    }

    public void SetActivity() {
        List<Activity> activities = dashboardService.getAllActivity();

        // Konversi list biasa ke ObservableList
        ObservableList<Activity> activityList = FXCollections.observableArrayList(activities);

        // Mapping kolom ke property di kelas Activity
        ActivityCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        StartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        EndCol.setCellValueFactory(new PropertyValueFactory<>("end"));

        // Set data ke TableView
        TableView.setItems(activityList);
    }

}
