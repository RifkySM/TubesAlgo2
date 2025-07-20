package com.activa.services;

import com.activa.models.User;
import com.activa.repositories.UserRepository;
import com.activa.utils.Helper;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani logika bisnis dan validasi untuk entitas User.
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
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
     * Mengambil semua pengguna dari database.
     * @return List dari objek User.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Membuat pengguna baru dengan validasi.
     * @param name Nama lengkap pengguna.
     * @param username Username untuk login.
     * @param password Kata sandi (plain text).
     * @param role Peran pengguna.
     * @return Optional yang berisi User jika berhasil, jika tidak maka kosong.
     */
    public Optional<User> createUser(String name, String username, String password, String role) {
        try {
            validateString(name, "Nama");
            validateString(username, "Username");
            validateString(password, "Password");
            validateString(role, "Role");

            if (userRepository.findByUsername(username).isPresent()) {
                throw new IllegalStateException("Username '" + username + "' sudah digunakan.");
            }

            String hashedPassword = Helper.hashPassword(password);
            User newUser = new User(role, name, username, hashedPassword);
            userRepository.create(newUser);
            Helper.showAlert("Sukses", "Pengguna baru berhasil dibuat.", Alert.AlertType.INFORMATION);
            return Optional.of(newUser);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Helper.showAlert("Gagal Membuat Pengguna", e.getMessage(), Alert.AlertType.ERROR);
            return Optional.empty();
        }
    }

    /**
     * Memperbarui data pengguna yang ada.
     * @param userId ID dari pengguna yang akan diperbarui.
     * @param name Nama baru.
     * @param username Username baru.
     * @param role Peran baru.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateUser(UUID userId, String name, String username, String role) {
        try {
            validateString(name, "Nama");
            validateString(username, "Username");
            validateString(role, "Role");

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Pengguna tidak ditemukan."));

            Optional<User> userWithSameUsername = userRepository.findByUsername(username);
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(userId)) {
                throw new IllegalStateException("Username '" + username + "' sudah digunakan oleh pengguna lain.");
            }

            existingUser.setName(name);
            existingUser.setUsername(username);
            existingUser.setRole(role);
            userRepository.update(existingUser);
            Helper.showAlert("Sukses", "Data pengguna berhasil diperbarui.", Alert.AlertType.INFORMATION);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            Helper.showAlert("Gagal Memperbarui", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    /**
     * Memperbarui kata sandi pengguna.
     * @param userId ID pengguna.
     * @param newPassword Kata sandi baru (plain text).
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updatePassword(UUID userId, String newPassword) {
        try {
            validateString(newPassword, "Password baru");
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("Password minimal harus 8 karakter.");
            }
            String newHashedPassword = Helper.hashPassword(newPassword);
            userRepository.updatePassword(userId, newHashedPassword);
            Helper.showAlert("Sukses", "Kata sandi berhasil diperbarui.", Alert.AlertType.INFORMATION);
            return true;
        } catch (IllegalArgumentException e) {
            Helper.showAlert("Gagal Memperbarui", e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    /**
     * Menghapus pengguna (soft delete) dengan konfirmasi.
     * @param userId ID pengguna yang akan dihapus.
     * @return true jika berhasil, false jika gagal atau dibatalkan.
     */
    public boolean deleteUser(UUID userId) {
        Optional<User> userToDelete = userRepository.findById(userId);
        if (userToDelete.isEmpty()) {
            Helper.showAlert("Error", "Pengguna tidak ditemukan.", Alert.AlertType.ERROR);
            return false;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Konfirmasi Hapus");
        confirmation.setHeaderText("Anda yakin ingin menghapus pengguna: " + userToDelete.get().getName() + "?");
        confirmation.setContentText("Tindakan ini tidak dapat dibatalkan.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userRepository.softDeleteById(userId);
            Helper.showAlert("Sukses", "Pengguna berhasil dihapus.", Alert.AlertType.INFORMATION);
            return true;
        }
        return false;
    }
}
