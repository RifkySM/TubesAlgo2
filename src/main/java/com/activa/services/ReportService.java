package com.activa.services;

import com.activa.models.Activity;
import com.activa.models.Attendance;
import com.activa.repositories.ActivityRepository;
import com.activa.repositories.AttendanceRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private final ActivityRepository activityRepository;
    private final AttendanceRepository attendanceRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public ReportService() {
        this.activityRepository = new ActivityRepository();
        this.attendanceRepository = new AttendanceRepository();
    }

    public String generateActivityReport() {
        List<Activity> activities = activityRepository.findAll();
        if (activities.isEmpty()) {
            return "Tidak ada data aktivitas yang ditemukan.";
        }

        StringBuilder report = new StringBuilder();
        report.append("LAPORAN SEMUA AKTIVITAS\n");
        report.append("=========================\n\n");

        for (Activity activity : activities) {
            report.append("Judul      : ").append(activity.getTitle()).append("\n");
            report.append("Deskripsi  : ").append(activity.getDescription()).append("\n");
            report.append("Mulai      : ").append(activity.getStartFormatted()).append("\n");
            report.append("Selesai    : ").append(activity.getEnd() != null ? activity.getEndFormatted() : "-").append("\n");
            report.append("--------------------------------------------------\n");
        }
        return report.toString();
    }

    public String generateAttendanceReport(LocalDate startDate, LocalDate endDate, Activity filterActivity) {
        List<Attendance> allAttendances = attendanceRepository.findAll();

        List<Attendance> filteredAttendances = allAttendances.stream()
                .filter(att -> att.getActivity() != null && att.getActivity().getStart() != null)
                .filter(att -> {
                    LocalDate activityStartDate = att.getActivity().getStart().toLocalDate();
                    return !activityStartDate.isBefore(startDate) && !activityStartDate.isAfter(endDate);
                })
                .filter(att -> filterActivity == null || att.getActivity().getId().equals(filterActivity.getId()))
                .collect(Collectors.toList());

        if (filteredAttendances.isEmpty()) {
            return "Tidak ada data absensi yang ditemukan untuk filter yang dipilih.";
        }

        StringBuilder report = new StringBuilder();
        report.append("LAPORAN ABSENSI\n");
        report.append("Periode: ").append(startDate.format(dateFormatter)).append(" - ").append(endDate.format(dateFormatter)).append("\n");
        if (filterActivity != null) {
            report.append("Kegiatan: ").append(filterActivity.getTitle()).append("\n");
        }
        report.append("==================================================\n\n");

        // MODIFIED: Grouping by Activity ID to correctly summarize attendance for each unique activity.
        Map<Object, List<Attendance>> groupedByActivityId = filteredAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getActivity().getId()));

        for (List<Attendance> attendances : groupedByActivityId.values()) {
            // All attendances in this list belong to the same activity, so we can get details from the first record.
            if (!attendances.isEmpty()) {
                Activity activity = attendances.get(0).getActivity();
                report.append("Kegiatan: ").append(activity.getTitle()).append(" (").append(activity.getStartFormatted()).append(")\n");
                report.append("Jumlah Hadir: ").append(attendances.size()).append(" orang\n");
                report.append("--------------------------------------------------\n");
                for (int i = 0; i < attendances.size(); i++) {
                    report.append(String.format("%d. %s (%s)\n", i + 1, attendances.get(i).getMember().getName(), attendances.get(i).getMember().getNim()));
                }
                report.append("\n");
            }
        }

        return report.toString();
    }
}