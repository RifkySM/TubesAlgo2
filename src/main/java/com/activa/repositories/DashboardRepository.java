package com.activa.repositories;

import com.activa.models.Activity;
import com.activa.models.Announcement;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DashboardRepository {
    private final Connection connection;

    public DashboardRepository() {
        this.connection = SessionManager.getInstance().getDatabaseConnection();
    }

//    Count Member Active
    public int MemberCount(Boolean Active) {
        String sql = "SELECT COUNT (is_active) as jumlah FROM members WHERE is_active = ?";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setBoolean(1,Active);
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()){
                    return rs.getInt("jumlah");
                }
            };
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all users.", e);
        }
        return 0;
    }

//    Request Count
    public int RequestCount() {
        String sql = "SELECT COUNT (id) as jumlah FROM requests WHERE status = 'PENDING'";
        try (PreparedStatement psmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()){
                    return rs.getInt("jumlah");
                }
            };
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all users.", e);
        }
        return 0;
    }

    public List<Announcement> getAnnouncement() {
        List<Announcement> announcements = new ArrayList<>();
        // SQLite adjustment: Use 'announcement' table
        String sql = "SELECT * FROM announcement a WHERE a.\"start\" < DATETIME('now') AND a.\"end\" > DATETIME('now')";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                announcements.add(new Announcement(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start") != null ? rs.getTimestamp("start").toLocalDateTime() : null,
                        rs.getTimestamp("end") != null ? rs.getTimestamp("end").toLocalDateTime() : null,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                        rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all announcements: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all announcements.", e);
        }
        return announcements;
    }

    public List<Activity> findAll() {
        List<Activity> activities = new ArrayList<>();
        // Mengurutkan berdasarkan waktu mulai (dari yang terbaru)
        String sql = "SELECT *\n" +
                "FROM activities\n" +
                "WHERE \"end\" > datetime('now', '+7 days')\n" +
                "ORDER BY datetime(\"start\") ASC;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(new Activity(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start") != null ? rs.getTimestamp("start").toLocalDateTime() : null,
                        rs.getTimestamp("end") != null ? rs.getTimestamp("end").toLocalDateTime() : null,
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                        rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all activities: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all activities.", e);
        }
        return activities;
    }
}
