package com.activa.controllers.member;

import com.activa.models.Member;
import com.activa.services.MemberService;
import com.activa.utils.Helper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MemberModalController {

    @FXML private Text titleText;
    @FXML private TextField nimField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthdatePicker;
    @FXML private TextArea addressArea;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final MemberService memberService = new MemberService();
    private Member currentMember;

    /**
     * Initializes the modal with the data of the member to be edited.
     * @param member The member object to edit.
     */
    public void initData(Member member) {
        this.currentMember = member;

        // Populate the form fields with the member's existing data
        nimField.setText(member.getNim());
        nameField.setText(member.getName());
        emailField.setText(member.getEmail());
        birthdatePicker.setValue(member.getBirthdate());
        addressArea.setText(member.getAddress());
    }

    @FXML
    private void handleSave() {
        if (currentMember == null) {
            Helper.showAlert("Error", "No member data to save.", javafx.scene.control.Alert.AlertType.ERROR);
            return;
        }

        // Get updated values from the form
        String nim = nimField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        LocalDate birthdate = birthdatePicker.getValue();
        String address = addressArea.getText();

        // Call the service to update the member
        boolean success = memberService.updateMemberDetails(
                currentMember.getId(),
                nim,
                name,
                email,
                birthdate,
                address
        );

        if (success) {
            closeWindow();
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
