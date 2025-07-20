package com.activa.services;

import com.activa.models.Activity;
import com.activa.repositories.ActivityRepository;
import com.activa.utils.Helper;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani logika bisnis dan validasi untuk entitas Activity.
 * Terintegrasi dengan Helper untuk menampilkan dialog.
 */
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService() {
        this.activityRepository = new ActivityRepository();
    }

    /**
     * Memvalidasi apakah sebuah string null atau kosong.
     * @param value String yang akan divalidasi.
     * @param fieldName Nama field untuk pesan error.
     * @throws IllegalArgumentException jika string tidak valid.
     */
    private void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " tidak boleh kosong.");
        }
    }

    /**
     * Membuat aktivitas baru dengan validasi.
     *
     * @param title       Judul aktivitas.
     * @param description Deskripsi aktivitas.
     * @param start       Waktu mulai.
     * @param end         Waktu selesai (bisa null).
     * @return Optional yang berisi Activity jika berhasil, jika tidak maka kosong.
     */
    public Optional<Activity> createActivity(String title, String description, LocalDateTime start, LocalDateTime end) {
        try {
            validateString(title, "Judul");
            validateString(description, "Deskripsi");
            if (start == null) {
                throw new IllegalArgumentException("Waktu mulai tidak boleh kosong.");
            }
            if (end != null && end.isBefore(start)) {
                throw new IllegalArgumentException("Waktu selesai tidak boleh sebelum waktu mulai.");
            }

            Activity newActivity = new Activity(title, description, start, end);
            activityRepository.create(newActivity);
            Helper.showAlert("Sukses", "Aktivitas baru berhasil dibuat.", AlertType.INFORMATION);
            return Optional.of(newActivity);
        } catch (IllegalArgumentException e) {
            Helper.showAlert("Gagal Membuat Aktivitas", e.getMessage(), AlertType.ERROR);
            return Optional.empty();
        }
    }

    /**
     * Memperbarui detail aktivitas yang sudah ada.
     *
     * @param activityId  ID dari aktivitas yang akan diperbarui.
     * @param title       Judul baru.
     * @param description Deskripsi baru.
     * @param start       Waktu mulai baru.
     * @param end         Waktu selesai baru.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateActivity(UUID activityId, String title, String description, LocalDateTime start, LocalDateTime end) {
        try {
            validateString(title, "Judul");
            validateString(description, "Deskripsi");
            if (start == null) {
                throw new IllegalArgumentException("Waktu mulai tidak boleh kosong.");
            }
            if (end != null && end.isBefore(start)) {
                throw new IllegalArgumentException("Waktu selesai tidak boleh sebelum waktu mulai.");
            }

            Activity existingActivity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalStateException("Aktivitas dengan ID " + activityId + " tidak ditemukan."));

            existingActivity.setTitle(title);
            existingActivity.setDescription(description);
            existingActivity.setStart(start);
            existingActivity.setEnd(end);

            activityRepository.update(existingActivity);
            Helper.showAlert("Sukses", "Detail aktivitas berhasil diperbarui.", AlertType.INFORMATION);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            Helper.showAlert("Gagal Memperbarui Aktivitas", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Menghapus (soft delete) sebuah aktivitas.
     *
     * @param activityId ID dari aktivitas yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deleteActivity(UUID activityId) {
        try {
            activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalStateException("Aktivitas dengan ID " + activityId + " tidak ditemukan."));

            activityRepository.softDeleteById(activityId);
            Helper.showAlert("Sukses", "Aktivitas berhasil dihapus.", AlertType.INFORMATION);
            return true;
        } catch (IllegalStateException e) {
            Helper.showAlert("Operasi Gagal", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Mendapatkan semua aktivitas.
     *
     * @return List dari objek Activity.
     */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /**
     * Menemukan aktivitas berdasarkan ID-nya.
     *
     * @param activityId ID dari aktivitas.
     * @return Optional yang berisi Activity jika ditemukan.
     */
    public Optional<Activity> findActivityById(UUID activityId) {
        return activityRepository.findById(activityId);
    }
}
