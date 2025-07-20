package com.activa.repositories;

import com.activa.models.Activity;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani semua operasi database untuk entitas Activity, disesuaikan untuk SQLite.
 */
public class ActivityRepository {

    private final Connection connection;

    public ActivityRepository() {
        // Mengambil koneksi database dari SessionManager
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Memetakan baris dari ResultSet ke objek Activity.
     *
     * @param rs ResultSet yang akan dipetakan.
     * @return Objek Activity baru.
     * @throws SQLException jika terjadi kesalahan akses database.
     */
    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        return new Activity(
                UUID.fromString(rs.getString("id")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("start") != null ? rs.getTimestamp("start").toLocalDateTime() : null,
                rs.getTimestamp("end") != null ? rs.getTimestamp("end").toLocalDateTime() : null,
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
        );
    }

    // ## --------------------
    // ## CREATE
    // ## --------------------

    /**
     * Menyimpan aktivitas baru ke database.
     * Diadaptasi untuk SQLite: Menambahkan created_at dan updated_at saat pembuatan.
     *
     * @param activity Objek Activity yang akan disimpan.
     */
    public void create(Activity activity) {
        // SQLite adjustment: Set created_at and updated_at on insert.
        String sql = "INSERT INTO activities (id, title, description, start, \"end\", created_at, updated_at) VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, activity.getId().toString());
            stmt.setString(2, activity.getTitle());
            stmt.setString(3, activity.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(activity.getStart()));

            if (activity.getEnd() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(activity.getEnd()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating activity: " + e.getMessage());
            throw new RuntimeException("Database error while creating activity.", e);
        }
    }

    // ## --------------------
    // ## READ / FINDERS
    // ## --------------------

    /**
     * Menemukan satu aktivitas berdasarkan UUID-nya jika belum di-soft-delete.
     *
     * @param id UUID dari aktivitas yang akan dicari.
     * @return Optional yang berisi Activity jika ditemukan, jika tidak maka kosong.
     */
    public Optional<Activity> findById(UUID id) {
        String sql = "SELECT * FROM activities WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id.toString()); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToActivity(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding activity by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching activity by ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Menemukan semua aktivitas yang belum dihapus, diurutkan berdasarkan waktu mulai.
     *
     * @return Sebuah List dari objek Activity.
     */
    public List<Activity> findAll() {
        List<Activity> activities = new ArrayList<>();
        // Mengurutkan berdasarkan waktu mulai (dari yang terbaru)
        String sql = "SELECT * FROM activities WHERE deleted_at IS NULL ORDER BY start DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all activities: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all activities.", e);
        }
        return activities;
    }

    // ## --------------------
    // ## UPDATE
    // ## --------------------

    /**
     * Memperbarui informasi aktivitas yang sudah ada.
     * Diadaptasi untuk SQLite: Menggunakan datetime('now') sebagai pengganti NOW().
     *
     * @param activity Objek Activity dengan detail yang diperbarui.
     */
    public void update(Activity activity) {
        // SQLite adjustment: Use datetime('now') instead of NOW()
        String sql = "UPDATE activities SET title = ?, description = ?, start = ?, \"end\" = ?, updated_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, activity.getTitle());
            stmt.setString(2, activity.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(activity.getStart()));

            if (activity.getEnd() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(activity.getEnd()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, activity.getId().toString()); // Use toString() for UUID
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating activity: " + e.getMessage());
            throw new RuntimeException("Database error while updating activity.", e);
        }
    }

    // ## --------------------
    // ## DELETE
    // ## --------------------

    /**
     * Melakukan soft-delete pada aktivitas dengan mengatur timestamp 'deleted_at'.
     * Diadaptasi untuk SQLite: Menggunakan datetime('now') sebagai pengganti NOW().
     *
     * @param id UUID dari aktivitas yang akan di-soft-delete.
     */
    public void softDeleteById(UUID id) {
        // SQLite adjustment: Use datetime('now') instead of NOW()
        String sql = "UPDATE activities SET deleted_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString()); // Use toString() for UUID
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting activity: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting activity.", e);
        }
    }

    /**
     * Menghapus aktivitas secara permanen dari database. Gunakan dengan hati-hati.
     *
     * @param id UUID dari aktivitas yang akan dihapus secara permanen.
     */
    public void hardDeleteById(UUID id) {
        String sql = "DELETE FROM activities WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString()); // Use toString() for UUID
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error hard-deleting activity: " + e.getMessage());
            throw new RuntimeException("Database error while hard-deleting activity.", e);
        }
    }
}