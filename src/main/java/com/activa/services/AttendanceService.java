package com.activa.services;

import com.activa.models.Activity;
import com.activa.models.Attendance;
import com.activa.models.Member;
import com.activa.repositories.ActivityRepository;
import com.activa.repositories.AttendanceRepository;
import com.activa.repositories.MemberRepository;
import com.activa.utils.Helper;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final ActivityRepository activityRepository;

    public AttendanceService() {
        this.attendanceRepository = new AttendanceRepository();
        this.memberRepository = new MemberRepository();
        this.activityRepository = new ActivityRepository();
    }

    public List<Activity> getActiveAndUpcomingActivities() {
        return activityRepository.findAll().stream()
                .filter(activity -> activity.getEnd() == null || activity.getEnd().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    /**
     * PERBAIKAN: Metode ini sekarang juga mengambil data absensi yang sudah ada.
     */
    public Optional<AttendanceSheetData> prepareAttendanceSheet(UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalStateException("Aktivitas dengan ID " + activityId + " tidak ditemukan."));

        if (activity.getStart().isAfter(LocalDateTime.now())) {
            Helper.showAlert("Info", "Absensi hanya dapat diambil setelah aktivitas dimulai.", AlertType.INFORMATION);
            return Optional.empty();
        }

        List<Member> allMembers = memberRepository.findAll().stream()
                .filter(Member::isActive)
                .collect(Collectors.toList());

        // Mengambil data absensi yang sudah ada untuk kegiatan ini
        List<Attendance> existingAttendances = attendanceRepository.findByActivityId(activityId);
        Set<UUID> presentMemberIds = existingAttendances.stream()
                .map(attendance -> attendance.getMember().getId())
                .collect(Collectors.toSet());

        return Optional.of(new AttendanceSheetData(activity, allMembers, presentMemberIds));
    }

    /**
     * PERBAIKAN: Logika penyimpanan sekarang bisa menambah dan menghapus absensi.
     */
    public boolean saveAttendance(UUID activityId, Map<UUID, Boolean> newAttendanceStatus) {
        try {
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalStateException("Aktivitas tidak ditemukan saat akan menyimpan absensi."));

            Map<UUID, Attendance> existingAttendanceMap = attendanceRepository.findByActivityId(activityId).stream()
                    .collect(Collectors.toMap(att -> att.getMember().getId(), att -> att));

            for (Map.Entry<UUID, Boolean> entry : newAttendanceStatus.entrySet()) {
                UUID memberId = entry.getKey();
                boolean isNowPresent = entry.getValue();
                boolean wasPresent = existingAttendanceMap.containsKey(memberId);

                if (isNowPresent && !wasPresent) {
                    // Jika sekarang hadir tapi sebelumnya tidak, buat data baru
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new IllegalStateException("Anggota dengan ID " + memberId + " tidak ditemukan."));
                    attendanceRepository.create(new Attendance(activity, member));
                } else if (!isNowPresent && wasPresent) {
                    // Jika sekarang tidak hadir tapi sebelumnya iya, hapus data lama
                    UUID attendanceIdToDelete = existingAttendanceMap.get(memberId).getId();
                    attendanceRepository.softDeleteById(attendanceIdToDelete);
                }
            }
            Helper.showAlert("Sukses", "Data absensi berhasil disimpan.", AlertType.INFORMATION);
            return true;
        } catch (IllegalStateException e) {
            Helper.showAlert("Gagal Menyimpan", e.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    /**
     * PERBAIKAN: Kelas data sekarang juga membawa ID anggota yang sudah hadir.
     */
    public static class AttendanceSheetData {
        private final Activity activity;
        private final List<Member> members;
        private final Set<UUID> presentMemberIds;

        public AttendanceSheetData(Activity activity, List<Member> members, Set<UUID> presentMemberIds) {
            this.activity = activity;
            this.members = members;
            this.presentMemberIds = presentMemberIds;
        }

        public Activity getActivity() { return activity; }
        public List<Member> getMembers() { return members; }
        public Set<UUID> getPresentMemberIds() { return presentMemberIds; }
    }
}
