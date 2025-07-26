package com.activa.repositories;

import com.activa.models.Parameter;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani semua operasi database untuk entitas Parameter.
 */
public class ParameterRepository {

    private final Connection connection;

    public ParameterRepository() {
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Memetakan baris dari ResultSet ke objek Parameter.
     */
    private Parameter mapResultSetToParameter(ResultSet rs) throws SQLException {
        return new Parameter(
                UUID.fromString(rs.getString("id")),
                rs.getString("key"),
                rs.getString("title"), // FIX: Menggunakan kolom 'title'
                rs.getString("content")
        );
    }

    /**
     * Menyimpan parameter baru ke database.
     */
    public void create(Parameter parameter) {
        // FIX: Menggunakan kolom 'title'
        String sql = "INSERT INTO parameters (id, key, title, content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parameter.getId().toString());
            pstmt.setString(2, parameter.getKey());
            pstmt.setString(3, parameter.getTitle()); // FIX: Menggunakan getTitle()
            pstmt.setString(4, parameter.getContent());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating parameter: " + e.getMessage());
            throw new RuntimeException("Database error while creating parameter.", e);
        }
    }

    /**
     * Mencari parameter berdasarkan UUID-nya.
     */
    public Optional<Parameter> findById(UUID id) {
        String sql = "SELECT * FROM parameters WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParameter(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding parameter by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching parameter by ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Mencari parameter berdasarkan key uniknya.
     */
    public Optional<Parameter> findByKey(String key) {
        String sql = "SELECT * FROM parameters WHERE key = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParameter(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding parameter by key: " + e.getMessage());
            throw new RuntimeException("Database error while fetching parameter by key.", e);
        }
        return Optional.empty();
    }

    /**
     * Mengambil semua parameter, diurutkan berdasarkan nama (title).
     */
    public List<Parameter> findAll() {
        List<Parameter> parameters = new ArrayList<>();
        // FIX: Mengurutkan berdasarkan 'title'
        String sql = "SELECT * FROM parameters ORDER BY title";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                parameters.add(mapResultSetToParameter(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all parameters: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all parameters.", e);
        }
        return parameters;
    }

    /**
     * Memperbarui parameter yang sudah ada di database.
     */
    public void update(Parameter parameter) {
        // FIX: Menggunakan kolom 'title'
        String sql = "UPDATE parameters SET key = ?, title = ?, content = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parameter.getKey());
            pstmt.setString(2, parameter.getTitle()); // FIX: Menggunakan getTitle()
            pstmt.setString(3, parameter.getContent());
            pstmt.setString(4, parameter.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating parameter: " + e.getMessage());
            throw new RuntimeException("Database error while updating parameter.", e);
        }
    }

    /**
     * Menghapus parameter dari database berdasarkan ID-nya.
     */
    public void deleteById(UUID id) {
        String sql = "DELETE FROM parameters WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting parameter: " + e.getMessage());
            throw new RuntimeException("Database error while deleting parameter.", e);
        }
    }
}
