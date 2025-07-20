package com.activa.controllers.member;

import com.activa.App;
import com.activa.models.Member;
import com.activa.services.MemberService;
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
import java.util.Objects;

public class MemberListController {

    @FXML private TableView<Member> tableView;
    @FXML private TableColumn<Member, String> nimColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, String> emailColumn;
    @FXML private TableColumn<Member, String> birthdateColumn;
    @FXML private TableColumn<Member, String> statusColumn;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnDeactivate;
    @FXML private Button btnReactivate;
    @FXML private TextField searchField;

    private final MemberService memberService = new MemberService();
    private final ObservableList<Member> memberList = FXCollections.observableArrayList();
    private FilteredList<Member> filteredMemberList;

    @FXML
    private void initialize() {
        // Setup table columns to bind to Member properties
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        birthdateColumn.setCellValueFactory(new PropertyValueFactory<>("birthdateFormatted"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadMembers();
        setupSearchFilter();

        // Set button actions
        btnEdit.setOnAction(event -> handleEditMember());
        btnDelete.setOnAction(event -> handleDeleteMember());
        btnDeactivate.setOnAction(event -> handleDeactivateMember());
        btnReactivate.setOnAction(event -> handleReactivateMember());
    }

    private void loadMembers() {
        memberList.setAll(memberService.getAllMembers());
    }

    private void setupSearchFilter() {
        filteredMemberList = new FilteredList<>(memberList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMemberList.setPredicate(member -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                // Filter by name or NIM
                if (member.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return member.getNim().toLowerCase().contains(lowerCaseFilter);
            });
        });

        tableView.setItems(filteredMemberList);
    }

    private void handleEditMember() {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            Helper.showAlert("Warning", "Please select a member to edit.", Alert.AlertType.WARNING);
            return;
        }
        openMemberFormModal(selectedMember);
    }

    private void handleDeleteMember() {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            Helper.showAlert("Warning", "Please select a member to delete.", Alert.AlertType.WARNING);
            return;
        }

        if (Helper.showConfirmation("Confirm Deletion", "Are you sure you want to delete " + selectedMember.getName() + "?")) {
            if (memberService.deleteMember(selectedMember.getId())) {
                loadMembers();
            }
        }
    }

    private void handleDeactivateMember() {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            Helper.showAlert("Warning", "Please select a member to deactivate.", Alert.AlertType.WARNING);
            return;
        }
        if (memberService.deactivateMember(selectedMember.getId())) {
            loadMembers();
        }
    }

    private void handleReactivateMember() {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            Helper.showAlert("Warning", "Please select a member to reactivate.", Alert.AlertType.WARNING);
            return;
        }
        if (memberService.reactivateMember(selectedMember.getId())) {
            loadMembers();
        }
    }

    private void openMemberFormModal(Member member) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("/views/club/modal.fxml")));
            Parent root = loader.load();

            MemberModalController controller = loader.getController();
            controller.initData(member);

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tableView.getScene().getWindow());
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setTitle("Edit Member Details");

            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();

            loadMembers();

        } catch (IOException e) {
            e.printStackTrace();
            Helper.showAlert("Error", "Failed to load the member form.", Alert.AlertType.ERROR);
        }
    }
}
