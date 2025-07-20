package com.activa.controllers.attendance;

import com.activa.models.Activity;
import com.activa.models.Member;
import com.activa.services.AttendanceService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AttendanceController {

    @FXML private ComboBox<Activity> activityComboBox;
    @FXML private VBox attendanceSheetBox;
    @FXML private Text activityTitleText;
    @FXML private TableView<AttendanceViewModel> tableView;
    @FXML private TableColumn<AttendanceViewModel, String> nimColumn;
    @FXML private TableColumn<AttendanceViewModel, String> nameColumn;
    @FXML private TableColumn<AttendanceViewModel, Boolean> statusColumn;
    @FXML private Button btnSave;
    @FXML private Button btnMarkAll;
    @FXML private Button btnReset;
    @FXML private Label infoLabel;
    @FXML private Label attendanceCountLabel;
    @FXML private TextField searchField;

    private final AttendanceService attendanceService = new AttendanceService();
    private final ObservableList<AttendanceViewModel> memberList = FXCollections.observableArrayList();
    private FilteredList<AttendanceViewModel> filteredMemberList;
    private Activity selectedActivity;

    @FXML
    private void initialize() {
        setupActivityComboBox();
        setupTableView();
        setupSearchFilter();
        loadActivities();
    }

    private void setupActivityComboBox() {
        activityComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Activity a) { return a == null ? "" : a.getTitle(); }
            @Override public Activity fromString(String s) { return null; }
        });
        activityComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) {
                this.selectedActivity = val;
                checkActivityStatus(val.getId());
            } else {
                showInfo("Silakan pilih kegiatan untuk memulai.");
            }
        });
    }

    private void setupTableView() {
        nimColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMember().getNim()));
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMember().getName()));
        statusColumn.setCellValueFactory(cell -> cell.getValue().presentProperty());
        statusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(statusColumn));
        statusColumn.setEditable(true);
        tableView.setEditable(true);
    }

    private void setupSearchFilter() {
        filteredMemberList = new FilteredList<>(memberList, p -> true);
        searchField.textProperty().addListener((obs, old, val) -> {
            filteredMemberList.setPredicate(viewModel -> {
                if (val == null || val.isEmpty()) return true;
                String filter = val.toLowerCase();
                Member member = viewModel.getMember();
                return member.getName().toLowerCase().contains(filter) || member.getNim().toLowerCase().contains(filter);
            });
        });
        tableView.setItems(filteredMemberList);
    }

    private void loadActivities() {
        activityComboBox.setItems(FXCollections.observableArrayList(attendanceService.getActiveAndUpcomingActivities()));
    }

    private void checkActivityStatus(UUID activityId) {
        Optional<AttendanceService.AttendanceSheetData> sheetDataOpt = attendanceService.prepareAttendanceSheet(activityId);
        if (sheetDataOpt.isPresent()) {
            displayAttendanceSheet(sheetDataOpt.get());
        } else {
            showInfo("Absensi untuk kegiatan ini belum bisa diambil.");
        }
    }

    private void displayAttendanceSheet(AttendanceService.AttendanceSheetData sheetData) {
        activityTitleText.setText("Absensi untuk: " + sheetData.getActivity().getTitle());
        memberList.clear();

        Set<UUID> presentMemberIds = sheetData.getPresentMemberIds();

        for (Member member : sheetData.getMembers()) {
            boolean isPresent = presentMemberIds.contains(member.getId());
            AttendanceViewModel vm = new AttendanceViewModel(member, isPresent);
            vm.presentProperty().addListener((obs, old, val) -> updateAttendanceCount());
            memberList.add(vm);
        }

        updateAttendanceCount();
        showAttendanceSheet(true);
    }

    @FXML
    private void handleSaveAttendance() {
        if (selectedActivity == null) return;
        Map<UUID, Boolean> attendanceData = new HashMap<>();
        memberList.forEach(vm -> attendanceData.put(vm.getMember().getId(), vm.isPresent()));

        if (attendanceService.saveAttendance(selectedActivity.getId(), attendanceData)) {
            showInfo("Absensi berhasil disimpan. Silakan pilih kegiatan lain.");
            activityComboBox.getSelectionModel().clearSelection();
        }
    }

    @FXML private void handleMarkAll() { filteredMemberList.forEach(vm -> vm.setPresent(true)); }
    @FXML private void handleReset() { filteredMemberList.forEach(vm -> vm.setPresent(false)); }

    private void updateAttendanceCount() {
        long presentCount = memberList.stream().filter(AttendanceViewModel::isPresent).count();
        attendanceCountLabel.setText(String.format("Hadir: %d/%d", presentCount, memberList.size()));
    }

    private void showInfo(String message) {
        attendanceSheetBox.setVisible(false);
        attendanceSheetBox.setManaged(false);
        infoLabel.setVisible(true);
        infoLabel.setManaged(true);
        infoLabel.setText(message);
    }

    private void showAttendanceSheet(boolean show) {
        attendanceSheetBox.setVisible(show);
        attendanceSheetBox.setManaged(show);
        infoLabel.setVisible(!show);
        infoLabel.setManaged(!show);
    }

    public static class AttendanceViewModel {
        private final SimpleObjectProperty<Member> member;
        private final SimpleBooleanProperty present;

        public AttendanceViewModel(Member member, boolean isPresent) {
            this.member = new SimpleObjectProperty<>(member);
            this.present = new SimpleBooleanProperty(isPresent);
        }
        public Member getMember() { return member.get(); }
        public boolean isPresent() { return present.get(); }
        public void setPresent(boolean present) { this.present.set(present); }
        public SimpleBooleanProperty presentProperty() { return present; }
    }
}
