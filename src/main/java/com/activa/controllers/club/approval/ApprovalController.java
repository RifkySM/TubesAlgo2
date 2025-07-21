package com.activa.controllers.club.approval;

import com.activa.models.Member;
import com.activa.models.MemberRequest;
import com.activa.services.MemberRequestService;
import com.activa.utils.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.stream.Collectors;

public class ApprovalController {

    @FXML private TableView<MemberRequest> tableView;
    @FXML private TableColumn<MemberRequest, String> nameColumn;
    @FXML private TableColumn<MemberRequest, String> nimColumn;
    @FXML private TableColumn<MemberRequest, String> requestDateColumn;
    @FXML private TableColumn<MemberRequest, String> statusColumn;
    @FXML private TextField searchField;

    @FXML private VBox detailsPane;
    @FXML private Text detailNameText;
    @FXML private Text detailNimText;
    @FXML private Text detailEmailText;
    @FXML private Text detailAddressText;
    @FXML private Text detailMotivationText;

    @FXML private TextArea notesArea;
    @FXML private Button btnApprove;
    @FXML private Button btnReject;

    private MemberRequestService memberRequestService;

    private ObservableList<MemberRequest> requestList;
    private FilteredList<MemberRequest> filteredRequestList;

    @FXML
    private void initialize() {
        this.memberRequestService = new MemberRequestService();
        this.requestList = FXCollections.observableArrayList();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        nimColumn.setCellValueFactory(new PropertyValueFactory<>("nim"));
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> displayRequestDetails(newSelection)
        );

        btnApprove.setOnAction(event -> handleApprove());
        btnReject.setOnAction(event -> handleReject());

        setupSearchFilter();
        loadRequests();
    }

    private void loadRequests() {
        requestList.setAll(memberRequestService.getAllRequests().stream()
                .filter(req -> req.getStatus() == MemberRequest.RequestStatus.PENDING)
                .collect(Collectors.toList()));
        displayRequestDetails(null);
    }

    private void setupSearchFilter() {
        filteredRequestList = new FilteredList<>(requestList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRequestList.setPredicate(request -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (request.getMember().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return request.getMember().getNim().toLowerCase().contains(lowerCaseFilter);
            });
        });

        tableView.setItems(filteredRequestList);
    }

    private void displayRequestDetails(MemberRequest request) {
        if (request != null && request.getMember() != null) {
            detailsPane.setVisible(true);
            Member member = request.getMember();
            detailNameText.setText("Name: " + member.getName());
            detailNimText.setText("NIM: " + member.getNim());
            detailEmailText.setText("Email: " + member.getEmail());
            detailAddressText.setText("Address: " + member.getAddress());
            detailMotivationText.setText("Motivation: " + request.getMotivation());
            notesArea.clear();
            boolean isPending = request.getStatus() == MemberRequest.RequestStatus.PENDING;
            btnApprove.setDisable(!isPending);
            btnReject.setDisable(!isPending);
        } else {
            detailsPane.setVisible(false);
            detailNameText.setText("Name: -");
            detailNimText.setText("NIM: -");
            detailEmailText.setText("Email: -");
            detailAddressText.setText("Address: -");
            detailMotivationText.setText("Motivation: -");
            notesArea.clear();
            btnApprove.setDisable(true);
            btnReject.setDisable(true);
        }
    }

    @FXML
    private void handleApprove() {
        MemberRequest selectedRequest = tableView.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            Helper.showAlert("No Selection", "Please select a request to approve.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedRequest.getStatus() != MemberRequest.RequestStatus.PENDING) {
            Helper.showAlert("Invalid Action", "This request has already been processed.", Alert.AlertType.INFORMATION);
            return;
        }

        String note = notesArea.getText();
        if (Helper.showConfirmation("Confirm Approval", "Are you sure you want to approve this membership request?")) {
            memberRequestService.approveRequest(selectedRequest.getId(), note);
            loadRequests();
        }
    }

    @FXML
    private void handleReject() {
        MemberRequest selectedRequest = tableView.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            Helper.showAlert("No Selection", "Please select a request to reject.", Alert.AlertType.WARNING);
            return;
        }

        if (selectedRequest.getStatus() != MemberRequest.RequestStatus.PENDING) {
            Helper.showAlert("Invalid Action", "This request has already been processed.", Alert.AlertType.INFORMATION);
            return;
        }

        String note = notesArea.getText();
        if (note == null || note.trim().isEmpty()) {
            Helper.showAlert("Note Required", "Please provide a reason for rejecting the request in the notes area.", Alert.AlertType.WARNING);
            return;
        }

        if (Helper.showConfirmation("Confirm Rejection", "Are you sure you want to reject this membership request?")) {
            memberRequestService.rejectRequest(selectedRequest.getId(), note);
            loadRequests();
        }
    }
}
