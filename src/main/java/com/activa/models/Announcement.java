package com.activa.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents an announcement made to club members.
 * This class is a plain old Java object (POJO) that maps to the 'announcements' table in the database.
 */
public class Announcement {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Full constructor for creating an Activity instance, typically used when mapping from a database result.
     */
    public Announcement(UUID id, String title, String description, LocalDateTime start, LocalDateTime end, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Simplified constructor for creating a new activity instance before saving to the database.
     */
    public Announcement(String title, String description, LocalDateTime start, LocalDateTime end) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    // Standard getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getStartFormatted() {
        if (start == null) {
            return "";
        }
        return start.format(formatter);
    }

    public String getEndFormatted() {
        if (end == null) {
            return "";
        }
        return end.format(formatter);
    }
}