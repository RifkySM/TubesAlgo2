package com.activa.repositories;

import com.activa.models.User;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {
    private final Connection connection;

    public UserRepository() {
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

    /**
     * Maps a row from a ResultSet to a User object.
     * This method fetches the associated Role using the RoleRepository.
     * @param rs The ResultSet to map from.
     * @return A new User object.
     * @throws SQLException if a database access error occurs.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getObject("id", UUID.class),
                rs.getString("role"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
        );
    }


    public void create(User user) {
        String sql = "INSERT INTO users (id, role_id, name, username, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, user.getId());
            pstmt.setObject(2, user.getRole());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, user.getPassword()); // Assumes password is already hashed
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            throw new RuntimeException("Database error while creating user.", e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE deleted_at IS NULL ORDER BY name ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all users.", e);
        }
        return users;
    }

    public Optional<User> findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching user by ID.", e);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND deleted_at IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            throw new RuntimeException("Database error while fetching user by username.", e);
        }
        return Optional.empty();
    }

    public void update(User user) {
        String sql = "UPDATE users SET role_id = ?, name = ?, username = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, user.getRole());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getUsername());
            pstmt.setObject(4, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            throw new RuntimeException("Database error while updating user.", e);
        }
    }

    public void updatePassword(UUID userId, String newHashedPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setObject(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            throw new RuntimeException("Database error while updating password.", e);
        }
    }

    public void softDeleteById(UUID id) {
        String sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting user: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting user.", e);
        }
    }

    public void hardDeleteById(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error hard-deleting user: " + e.getMessage());
            throw new RuntimeException("Database error while hard-deleting user.", e);
        }
    }
}