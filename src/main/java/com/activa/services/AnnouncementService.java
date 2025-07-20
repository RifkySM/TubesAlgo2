package com.activa.services;

import com.activa.models.Announcement;
import com.activa.repositories.AnnouncementRepository;
import com.activa.utils.Helper;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani logika bisnis dan validasi untuk entitas Announcement.
 * Terintegrasi dengan Helper untuk menampilkan dialog.
 */
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService() {
        this.announcementRepository = new AnnouncementRepository();
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
     * Membuat pengumuman baru dengan validasi.
     *
     * @param title       Judul pengumuman.
     * @param description Deskripsi pengumuman.
     * @param start       Waktu mulai.
     * @param end         Waktu selesai (bisa null).
     * @return Optional yang berisi Announcement jika berhasil, jika tidak maka kosong.
     */
    public Optional<Announcement> createAnnouncement(String title, String description, LocalDateTime start, LocalDateTime end) {
        try {
            validateString(title, "Judul");
            validateString(description, "Deskripsi");
            if (start == null) {
                throw new IllegalArgumentException("Waktu publikasi tidak boleh kosong.");
            }
            if (end != null && end.isBefore(start)) {
                throw new IllegalArgumentException("Waktu selesai tidak boleh sebelum waktu publikasi.");
            }

            Announcement newAnnouncement = new Announcement(title, description, start, end);
            announcementRepository.create(newAnnouncement);
            Helper.showAlert("Sukses", "Pengumuman baru berhasil dibuat.", AlertType.INFORMATION);
            return Optional.of(newAnnouncement);
        } catch (IllegalArgumentException e) {
            Helper.showAlert("Gagal Membuat Pengumuman", e.getMessage(), AlertType.ERROR);
            return Optional.empty();
        }
    }

    /**
     * Memperbarui detail pengumuman yang sudah ada.
     *
     * @param announcementId ID dari pengumuman yang akan diperbarui.
     * @param title          Judul baru.
     * @param description    Deskripsi baru.
     * @param start          Waktu mulai baru.
     * @param end            Waktu selesai baru.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateAnnouncement(UUID announcementId, String title, String description, LocalDateTime start, LocalDateTime end) {
        try {
            validateString(title, "Judul");
            validateString(description, "Deskripsi");
            if (start == null) {
                throw new IllegalArgumentException("Waktu publikasi tidak boleh kosong.");
            }
            if (end != null && end.isBefore(start)) {
                throw new IllegalArgumentException("Waktu selesai tidak boleh sebelum waktu publikasi.");
            }

            Announcement existingAnnouncement = announcementRepository.findById(announcementId)
                    .orElseThrow(() -> new IllegalStateException("Pengumuman dengan ID " + announcementId + " tidak ditemukan."));

            existingAnnouncement.setTitle(title);
            existingAnnouncement.setDescription(description);
            existingAnnouncement.setStart(start);
            existingAnnouncement.setEnd(end);

            announcementRepository.update(existingAnnouncement);
            Helper.showAlert("Sukses", "Detail pengumuman berhasil diperbarui.", AlertType.INFORMATION);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            Helper.showAlert("Gagal Memperbarui Pengumuman", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Menghapus (soft delete) sebuah pengumuman.
     *
     * @param announcementId ID dari pengumuman yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deleteAnnouncement(UUID announcementId) {
        try {
            announcementRepository.findById(announcementId)
                    .orElseThrow(() -> new IllegalStateException("Pengumuman dengan ID " + announcementId + " tidak ditemukan."));

            announcementRepository.softDeleteById(announcementId);
            Helper.showAlert("Sukses", "Pengumuman berhasil dihapus.", AlertType.INFORMATION);
            return true;
        } catch (IllegalStateException e) {
            Helper.showAlert("Operasi Gagal", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * Mendapatkan semua pengumuman.
     *
     * @return List dari objek Announcement.
     */
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    /**
     * Menemukan pengumuman berdasarkan ID-nya.
     *
     * @param announcementId ID dari pengumuman.
     * @return Optional yang berisi Announcement jika ditemukan.
     */
    public Optional<Announcement> findAnnouncementById(UUID announcementId) {
        return announcementRepository.findById(announcementId);
    }
}
