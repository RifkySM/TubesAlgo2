package com.activa.repositories;

import com.activa.models.Member;
import com.activa.models.User;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani semua operasi database untuk entitas Member, disesuaikan untuk SQLite.
 */
public class MemberRepository {

    private final Connection connection;
    private final UserRepository userRepository;

    public MemberRepository() {
        this.userRepository = new UserRepository();
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Memetakan baris dari ResultSet ke objek Member.
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        String userIdStr = rs.getString("user_id");
        User user = (userIdStr != null) ? userRepository.findById(UUID.fromString(userIdStr)).orElse(null) : null;

        return new Member(
                UUID.fromString(rs.getString("id")),
                user,
                rs.getString("nim"),
                rs.getString("name"),
                rs.getString("email"),
                // SQLite stores DATE as TEXT, so we parse it.
                rs.getString("birth_date") != null ? LocalDate.parse(rs.getString("birth_date")) : null,
                rs.getString("address"),
                // SQLite stores BOOLEAN as INTEGER (0 or 1).
                rs.getInt("is_active") == 1,
                rs.getTimestamp("joined_at") != null ? rs.getTimestamp("joined_at").toLocalDateTime() : null,
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
        );
    }

    // ## CREATE ##
    public void create(Member member) {
        // SQLite adjustment: Set timestamps on creation.
        String sql = "INSERT INTO members (id, user_id, nim, name, email, birth_date, address, is_active, joined_at, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, member.getId().toString());
            if (member.getUser() != null) {
                stmt.setString(2, member.getUser().getId().toString());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setString(3, member.getNim());
            stmt.setString(4, member.getName());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getBirthdate() != null ? member.getBirthdate().toString() : null);
            stmt.setString(7, member.getAddress());
            stmt.setInt(8, member.isActive() ? 1 : 0); // Store boolean as integer
            if (member.getJoinedAt() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(member.getJoinedAt()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating member: " + e.getMessage());
            throw new RuntimeException("Database error while creating member.", e);
        }
    }

    // ## READ / FINDERS ##
    public Optional<Member> findById(UUID id) {
        String sql = "SELECT * FROM members WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
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

    public Optional<Member> findByNim(String nim) {
        String sql = "SELECT * FROM members WHERE nim = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding member by NIM: " + e.getMessage());
            throw new RuntimeException("Database error while fetching member by NIM.", e);
        }
        return Optional.empty();
    }

    public Optional<Member> findByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding member by Email: " + e.getMessage());
            throw new RuntimeException("Database error while fetching member by Email.", e);
        }
        return Optional.empty();
    }

    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE deleted_at IS NULL ORDER BY name";
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

    // ## UPDATE ##
    public void update(Member member) {
        // SQLite adjustment: Use datetime('now')
        String sql = "UPDATE members SET nim = ?, name = ?, email = ?, birth_date = ?, address = ?, is_active = ?, user_id = ?, updated_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, member.getNim());
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getBirthdate() != null ? member.getBirthdate().toString() : null);
            stmt.setString(5, member.getAddress());
            stmt.setInt(6, member.isActive() ? 1 : 0);
            if (member.getUser() != null) {
                stmt.setString(7, member.getUser().getId().toString());
            } else {
                stmt.setNull(7, Types.VARCHAR);
            }
            stmt.setString(8, member.getId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            throw new RuntimeException("Database error while updating member.", e);
        }
    }

    public void updateStatus(UUID memberId, boolean isActive) {
        // SQLite adjustment: Use datetime('now')
        String sql = "UPDATE members SET is_active = ?, updated_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, isActive ? 1 : 0);
            stmt.setString(2, memberId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating member status: " + e.getMessage());
            throw new RuntimeException("Database error while updating member status.", e);
        }
    }

    // ## DELETE ##
    public void softDeleteById(UUID id) {
        // SQLite adjustment: Use datetime('now')
        String sql = "UPDATE members SET deleted_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting member: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting member.", e);
        }
    }
}