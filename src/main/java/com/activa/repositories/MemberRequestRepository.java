package com.activa.repositories;

import com.activa.models.MemberRequest;
import com.activa.utils.SessionManager; // Assuming SessionManager is in this package

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all database operations for MemberRequest entities.
 */
public class MemberRequestRepository {

    private final Connection connection;

    public MemberRequestRepository() {
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Maps a row from a ResultSet to a MemberRequest object.
     *
     * @param rs The ResultSet to map from.
     * @return A new MemberRequest object.
     * @throws SQLException if a database access error occurs.
     */
    private MemberRequest mapResultSetToMemberRequest(ResultSet rs) throws SQLException {
        return new MemberRequest(
                rs.getObject("id", UUID.class),
                rs.getObject("member_id", UUID.class),
                MemberRequest.RequestStatus.valueOf(rs.getString("status")),
                rs.getString("note"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
        );
    }

    /**
     * Saves a new member request to the database.
     *
     * @param request The MemberRequest object to save.
     */
    public void create(MemberRequest request) {
        String sql = "INSERT INTO requests (id, member_id, status, note) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, request.getId());
            pstmt.setObject(2, request.getMemberId());
            pstmt.setString(3, request.getStatus().name());
            pstmt.setString(4, request.getNote());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating member request: " + e.getMessage());
            throw new RuntimeException("Database error while creating member request.", e);
        }
    }


    /**
     * Finds a single request by its UUID, if it hasn't been soft-deleted.
     *
     * @param id The UUID of the request to find.
     * @return An Optional containing the MemberRequest if found, otherwise empty.
     */
    public Optional<MemberRequest> findById(UUID id) {
        String sql = "SELECT * FROM requests WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMemberRequest(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding request by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching request by ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Finds all non-deleted requests, ordered by creation date descending.
     *
     * @return A List of MemberRequest objects.
     */
    public List<MemberRequest> findAll() {
        List<MemberRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE deleted_at IS NULL ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(mapResultSetToMemberRequest(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all requests: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all requests.", e);
        }
        return requests;
    }

    /**
     * Finds all non-deleted requests with a specific status.
     *
     * @param status The status to filter by.
     * @return A List of matching MemberRequest objects.
     */
    public List<MemberRequest> findAllByStatus(MemberRequest.RequestStatus status) {
        List<MemberRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE status = ? AND deleted_at IS NULL ORDER BY created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToMemberRequest(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding requests by status: " + e.getMessage());
            throw new RuntimeException("Database error while fetching requests by status.", e);
        }
        return requests;
    }

    /**
     * Updates the status and note of an existing request.
     *
     * @param requestId The ID of the request to update.
     * @param newStatus The new status to set.
     * @param newNote   The new note to set.
     */
    public void update(UUID requestId, MemberRequest.RequestStatus newStatus, String newNote) {
        String sql = "UPDATE requests SET status = ?, note = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, newNote);
            pstmt.setObject(3, requestId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating request: " + e.getMessage());
            throw new RuntimeException("Database error while updating request.", e);
        }
    }


    /**
     * Soft-deletes a request by setting its 'deleted_at' timestamp.
     *
     * @param id The UUID of the request to soft-delete.
     */
    public void softDeleteById(UUID id) {
        String sql = "UPDATE requests SET deleted_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting request: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting request.", e);
        }
    }

    /**
     * Permanently deletes a request from the database. Use with caution.
     *
     * @param id The UUID of the request to permanently delete.
     */
    public void hardDeleteById(UUID id) {
        String sql = "DELETE FROM requests WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error hard-deleting request: " + e.getMessage());
            throw new RuntimeException("Database error while hard-deleting request.", e);
        }
    }
}