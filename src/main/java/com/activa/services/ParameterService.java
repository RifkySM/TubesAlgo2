package com.activa.services;

import com.activa.models.Parameter;
import com.activa.repositories.ParameterRepository;
import com.activa.utils.Helper;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Menangani logika bisnis untuk mengelola parameter aplikasi, termasuk profil klub.
 */
public class ParameterService {

    private final ParameterRepository parameterRepository;

    public static final String CLUB_NAME_KEY = "CLUB_NAME";
    public static final String CLUB_DESC_KEY = "CLUB_DESCRIPTION";
    public static final String CLUB_VISION_MISSION_KEY = "CLUB_VISION_MISSION";
    public static final String CLUB_BENEFIT_KEY = "CLUB_BENEFIT";

    public ParameterService() {
        this.parameterRepository = new ParameterRepository();
    }

    /**
     * Mengambil semua data profil klub dari database.
     *
     * @return Map yang berisi data profil, dengan kunci sesuai konstanta di atas.
     */
    public Map<String, Parameter> getClubProfile() {
        Map<String, Parameter> profileMap = new HashMap<>();
        parameterRepository.findByKey(CLUB_NAME_KEY).ifPresent(p -> profileMap.put(CLUB_NAME_KEY, p));
        parameterRepository.findByKey(CLUB_DESC_KEY).ifPresent(p -> profileMap.put(CLUB_DESC_KEY, p));
        parameterRepository.findByKey(CLUB_VISION_MISSION_KEY).ifPresent(p -> profileMap.put(CLUB_VISION_MISSION_KEY, p));
        parameterRepository.findByKey(CLUB_BENEFIT_KEY).ifPresent(p -> profileMap.put(CLUB_BENEFIT_KEY, p));
        return profileMap;
    }

    /**
     * Menyimpan atau memperbarui sebuah parameter di database.
     *
     * @param key     Kunci unik dari parameter.
     * @param name    Nama deskriptif dari parameter.
     * @param content Isi atau nilai dari parameter.
     */
    private void saveOrUpdateParameter(String key, String name, String content) {
        Optional<Parameter> existingParamOpt = parameterRepository.findByKey(key);
        if (existingParamOpt.isPresent()) {
            Parameter existingParam = existingParamOpt.get();
            existingParam.setContent(content);
            parameterRepository.update(existingParam);
        } else {
            Parameter newParam = new Parameter(key, name, content);
            parameterRepository.create(newParam);
        }
    }

    /**
     * Menyimpan semua detail profil klub ke database.
     *
     * @param clubName      Nama klub.
     * @param description   Deskripsi klub.
     * @param visionMission Visi dan Misi klub.
     * @param benefit       Benefit bergabung dengan klub.
     */
    public void saveClubProfile(String clubName, String description, String visionMission, String benefit) {
        try {
            saveOrUpdateParameter(CLUB_NAME_KEY, "Nama Klub", clubName);
            saveOrUpdateParameter(CLUB_DESC_KEY, "Deskripsi Klub", description);
            saveOrUpdateParameter(CLUB_VISION_MISSION_KEY, "Visi dan Misi Klub", visionMission);
            saveOrUpdateParameter(CLUB_BENEFIT_KEY, "Benefit Klub", benefit);
            Helper.showAlert("Sukses", "Profil klub berhasil diperbarui.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            Helper.showAlert("Error", "Gagal menyimpan profil klub: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
