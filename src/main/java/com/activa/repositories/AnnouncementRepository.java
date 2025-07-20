package com.activa.repositories;

import com.activa.models.Announcement;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani semua operasi database untuk entitas Announcement, disesuaikan untuk SQLite.
 */
public class AnnouncementRepository {

    private final Connection connection;

    public AnnouncementRepository() {
        // Mengambil koneksi database dari SessionManager
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Memetakan baris dari ResultSet ke objek Announcement.
     *
     * @param rs ResultSet yang akan dipetakan.
     * @return Objek Announcement baru.
     * @throws SQLException jika terjadi kesalahan akses database.
     */
    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        return new Announcement(
                // SQLite adjustment: Handle UUID as a String
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
     * Menyimpan pengumuman baru ke database.
     * Diadaptasi untuk SQLite: Menambahkan created_at dan updated_at saat pembuatan.
     *
     * @param announcement Objek Announcement yang akan disimpan.
     */
    public void create(Announcement announcement) {
        // SQLite adjustment: Use 'announcement' table and set timestamps on insert
        String sql = "INSERT INTO announcement (id, title, description, start, \"end\", created_at, updated_at) VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, announcement.getId().toString());
            stmt.setString(2, announcement.getTitle());
            stmt.setString(3, announcement.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(announcement.getStart()));

            if (announcement.getEnd() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(announcement.getEnd()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating announcement: " + e.getMessage());
            throw new RuntimeException("Database error while creating announcement.", e);
        }
    }

    // ## --------------------
    // ## READ / FINDERS
    // ## --------------------

    /**
     * Menemukan satu pengumuman berdasarkan UUID-nya jika belum di-soft-delete.
     *
     * @param id UUID dari pengumuman yang akan dicari.
     * @return Optional yang berisi Announcement jika ditemukan, jika tidak maka kosong.
     */
    public Optional<Announcement> findById(UUID id) {
        // SQLite adjustment: Use 'announcement' table
        String sql = "SELECT * FROM announcement WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString()); // Handle UUID as String
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAnnouncement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding announcement by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching announcement by ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Menemukan semua pengumuman yang belum dihapus, diurutkan berdasarkan waktu mulai.
     *
     * @return Sebuah List dari objek Announcement.
     */
    public List<Announcement> findAll() {
        List<Announcement> announcements = new ArrayList<>();
        // SQLite adjustment: Use 'announcement' table
        String sql = "SELECT * FROM announcement WHERE deleted_at IS NULL ORDER BY start DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all announcements: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all announcements.", e);
        }
        return announcements;
    }

    // ## --------------------
    // ## UPDATE
    // ## --------------------

    /**
     * Memperbarui informasi pengumuman yang sudah ada.
     *
     * @param announcement Objek Announcement dengan detail yang diperbarui.
     */
    public void update(Announcement announcement) {
        // SQLite adjustment: Use 'announcement' table and datetime('now')
        String sql = "UPDATE announcement SET title = ?, description = ?, start = ?, \"end\" = ?, updated_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(announcement.getStart()));

            if (announcement.getEnd() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(announcement.getEnd()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, announcement.getId().toString()); // Handle UUID as String
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating announcement: " + e.getMessage());
            throw new RuntimeException("Database error while updating announcement.", e);
        }
    }

    // ## --------------------
    // ## DELETE
    // ## --------------------

    /**
     * Melakukan soft-delete pada pengumuman dengan mengatur timestamp 'deleted_at'.
     *
     * @param id UUID dari pengumuman yang akan di-soft-delete.
     */
    public void softDeleteById(UUID id) {
        // SQLite adjustment: Use 'announcement' table and datetime('now')
        String sql = "UPDATE announcement SET deleted_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString()); // Handle UUID as String
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting announcement: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting announcement.", e);
        }
    }

    /**
     * Menghapus pengumuman secara permanen dari database. Gunakan dengan hati-hati.
     *
     * @param id UUID dari pengumuman yang akan dihapus secara permanen.
     */
    public void hardDeleteById(UUID id) {
        // SQLite adjustment: Use 'announcement' table
        String sql = "DELETE FROM announcement WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString()); // Handle UUID as String
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error hard-deleting announcement: " + e.getMessage());
            throw new RuntimeException("Database error while hard-deleting announcement.", e);
        }
    }
}