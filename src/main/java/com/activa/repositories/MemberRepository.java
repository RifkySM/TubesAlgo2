package com.activa.repositories;

import com.activa.models.Member;
import com.activa.utils.SessionManager; // Assuming SessionManager is in this package

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all database operations for Member entities.
 */
public class MemberRepository {

    private final Connection connection;

    public MemberRepository() {
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Maps a row from a ResultSet to a Member object.
     *
     * @param rs The ResultSet to map from.
     * @return A new Member object.
     * @throws SQLException if a database access error occurs.
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getObject("id", UUID.class),
                rs.getString("nim"),
                rs.getString("name"),
                rs.getObject("birthdate", LocalDate.class),
                rs.getString("address"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("joined_at") != null ? rs.getTimestamp("joined_at").toLocalDateTime() : null,
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
        );
    }

    // ## --------------------
    // ## CREATE
    // ## --------------------

    /**
     * Saves a new member to the database.
     *
     * @param member The Member object to save.
     */
    public void create(Member member) {
        String sql = "INSERT INTO members (id, nim, name, birthdate, address, is_active, joined_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, member.getId());
            pstmt.setString(2, member.getNim());
            pstmt.setString(3, member.getName());
            pstmt.setObject(4, member.getBirthdate());
            pstmt.setString(5, member.getAddress());
            pstmt.setBoolean(6, member.isActive());
            pstmt.setTimestamp(7, Timestamp.valueOf(member.getJoinedAt()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating member: " + e.getMessage());
            throw new RuntimeException("Database error while creating member.", e);
        }
    }

    // ## --------------------
    // ## READ / FINDERS
    // ## --------------------

    /**
     * Finds a single member by their UUID, if they haven't been soft-deleted.
     *
     * @param id The UUID of the member to find.
     * @return An Optional containing the Member if found, otherwise empty.
     */
    public Optional<Member> findById(UUID id) {
        String sql = "SELECT * FROM members WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding member by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching member by ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Finds all non-deleted members, ordered by name.
     *
     * @return A List of Member objects.
     */
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE deleted_at IS NULL ORDER BY name ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all members: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all members.", e);
        }
        return members;
    }

    // ## --------------------
    // ## UPDATE
    // ## --------------------

    /**
     * Updates an existing member's personal information.
     *
     * @param member The Member object with updated details.
     */
    public void update(Member member) {
        String sql = "UPDATE members SET nim = ?, name = ?, birthdate = ?, address = ?, is_active = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, member.getNim());
            pstmt.setString(2, member.getName());
            pstmt.setObject(3, member.getBirthdate());
            pstmt.setString(4, member.getAddress());
            pstmt.setObject(5, member.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            throw new RuntimeException("Database error while updating member.", e);
        }
    }

    /**
     * Updates only the active status of a member.
     *
     * @param memberId The ID of the member to update.
     * @param isActive The new active status.
     */
    public void updateStatus(UUID memberId, boolean isActive) {
        String sql = "UPDATE members SET is_active = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setObject(2, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating member status: " + e.getMessage());
            throw new RuntimeException("Database error while updating member status.", e);
        }
    }

    /**
     * Soft-deletes a member by setting their 'deleted_at' timestamp.
     *
     * @param id The UUID of the member to soft-delete.
     */
    public void softDeleteById(UUID id) {
        String sql = "UPDATE members SET deleted_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting member: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting member.", e);
        }
    }

    /**
     * Permanently deletes a member from the database. Use with caution.
     *
     * @param id The UUID of the member to permanently delete.
     */
    public void hardDeleteById(UUID id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error hard-deleting member: " + e.getMessage());
            throw new RuntimeException("Database error while hard-deleting member.", e);
        }
    }
}