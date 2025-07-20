package com.activa.repositories;

import com.activa.models.Activity;
import com.activa.models.Attendance;
import com.activa.models.Member;
import com.activa.utils.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Menangani semua operasi database untuk entitas Attendance, disesuaikan untuk SQLite.
 * Repositori ini menghubungkan antara Member dan Activity.
 */
public class AttendanceRepository {

    private final Connection connection;
    private final ActivityRepository activityRepository;
    private final MemberRepository memberRepository;

    public AttendanceRepository() {
        this.connection = SessionManager.getInstance().getDatabaseConnection();
        // Inisialisasi repositori yang menjadi dependensi
        this.activityRepository = new ActivityRepository();
        this.memberRepository = new MemberRepository();
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        // SQLite adjustment: Handle UUIDs as Strings
        UUID activityId = UUID.fromString(rs.getString("activity_id"));
        UUID memberId = UUID.fromString(rs.getString("member_id"));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new SQLException("Gagal menemukan activity dengan id: " + activityId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new SQLException("Gagal menemukan member dengan id: " + memberId));

        return new Attendance(
                UUID.fromString(rs.getString("id")),
                activity,
                member,
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null
        );
    }

    // ## CREATE ##
    public void create(Attendance attendance) {
        // SQLite adjustment: Use 'attendance' table and set timestamps on insert
        String sql = "INSERT INTO attendance (id, activity_id, member_id, created_at, updated_at) VALUES (?, ?, ?, datetime('now'), datetime('now'))";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, attendance.getId().toString());
            stmt.setString(2, attendance.getActivity().getId().toString());
            stmt.setString(3, attendance.getMember().getId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating attendance: " + e.getMessage());
            throw new RuntimeException("Database error while creating attendance.", e);
        }
    }

    // ## READ / FINDERS ##
    public Optional<Attendance> findById(UUID id) {
        // SQLite adjustment: Use 'attendance' table
        String sql = "SELECT * FROM attendance WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding attendance by ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching attendance by ID.", e);
        }
        return Optional.empty();
    }

    public List<Attendance> findByActivityId(UUID activityId) {
        List<Attendance> attendances = new ArrayList<>();
        // SQLite adjustment: Use 'attendance' table
        String sql = "SELECT * FROM attendance WHERE activity_id = ? AND deleted_at IS NULL ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, activityId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attendances.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding attendances by activity ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching attendances.", e);
        }
        return attendances;
    }

    public Optional<Attendance> findByActivityAndMember(UUID activityId, UUID memberId) {
        // SQLite adjustment: Use 'attendance' table
        String sql = "SELECT * FROM attendance WHERE activity_id = ? AND member_id = ? AND deleted_at IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, activityId.toString());
            stmt.setString(2, memberId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding attendance by activity and member ID: " + e.getMessage());
            throw new RuntimeException("Database error while fetching attendance.", e);
        }
        return Optional.empty();
    }

    /**
     * Mengambil semua data absensi yang belum di-soft-delete.
     *
     * @return Sebuah List dari semua objek Attendance.
     */
    public List<Attendance> findAll() {
        List<Attendance> attendances = new ArrayList<>();
        // SQLite adjustment: Use 'attendance' table
        String sql = "SELECT * FROM attendance WHERE deleted_at IS NULL ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                attendances.add(mapResultSetToAttendance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all attendances: " + e.getMessage());
            throw new RuntimeException("Database error while fetching all attendances.", e);
        }
        return attendances;
    }

    // ## DELETE ##
    public void softDeleteById(UUID id) {
        // SQLite adjustment: Use 'attendance' table and datetime('now')
        String sql = "UPDATE attendance SET deleted_at = datetime('now') WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error soft-deleting attendance: " + e.getMessage());
            throw new RuntimeException("Database error while soft-deleting attendance.", e);
        }
    }

    public void hardDeleteById(UUID id) {
        // SQLite adjustment: Use 'attendance' table
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error hard-deleting attendance: " + e.getMessage());
            throw new RuntimeException("Database error while hard-deleting attendance.", e);
        }
    }
}