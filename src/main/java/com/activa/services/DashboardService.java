package com.activa.services;

import com.activa.models.Activity;
import com.activa.models.Announcement;
import com.activa.repositories.DashboardRepository;

import java.util.List;

public class DashboardService {
    private final DashboardRepository dashboardRepository;

    public DashboardService() {this.dashboardRepository = new DashboardRepository();}

    public int getMemberCount(Boolean IsActive) {return dashboardRepository.MemberCount(IsActive);}
    public int getRequestCount() {return dashboardRepository.RequestCount();}
    public List<Announcement> getAnnouncement() {return dashboardRepository.getAnnouncement();}
    public List<Activity> getAllActivity() {
        return dashboardRepository.findAll();
    }
}
