package com.activa.controllers.club;

import com.activa.models.Parameter;
import com.activa.services.ParameterService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Map;

public class ProfileFormController {

    @FXML private TextField clubNameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea visionArea;
    @FXML private TextArea benefitArea;
    @FXML private Button btnSave;

    private ParameterService parameterService;

    /**
     * Metode inisialisasi yang dipanggil setelah FXML dimuat.
     */
    @FXML
    private void initialize() {
        this.parameterService = new ParameterService();
        loadProfileData();
    }

    /**
     * Memuat data profil klub dari service dan menampilkannya di form.
     */
    private void loadProfileData() {
        //load data
    }

    /**
     * Menangani event saat tombol 'Simpan Perubahan' diklik.
     */
    @FXML
    private void handleSave() {

    }
}
