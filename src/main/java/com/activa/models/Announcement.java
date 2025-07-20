package com.activa.models;

import java.time.LocalDateTime;
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

    /**
     * Full constructor for creating an Announcement instance, typically used when mapping from a database result.
     *
     * @param id          The unique identifier of the announcement.
     * @param title       The title of the announcement.
     * @param description The main content of the announcement.
     * @param start       The publication date and time.
     * @param end         The expiry date and time (nullable).
     * @param createdAt   The creation timestamp.
     * @param updatedAt   The last update timestamp.
     * @param deletedAt   The soft-deletion timestamp (nullable).
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
     * Simplified constructor for creating a new announcement instance before saving to the database.
     *
     * @param title       The title of the announcement.
     * @param description The main content of the announcement.
     * @param start       The publication date and time.
     * @param end         The expiry date and time (nullable).
     */
    public Announcement(String title, String description, LocalDateTime start, LocalDateTime end) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
    }


    /**
     * Gets the unique identifier of the announcement.
     * @return The UUID of the announcement.
     */
    public UUID getId() { return id; }

    /**
     * Sets the unique identifier of the announcement.
     * @param id The UUID to set.
     */
    public void setId(UUID id) { this.id = id; }

    /**
     * Gets the title of the announcement.
     * @return The title string.
     */
    public String getTitle() { return title; }

    /**
     * Sets the title of the announcement.
     * @param title The title string to set.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the description/content of the announcement.
     * @return The description string.
     */
    public String getDescription() { return description; }

    /**
     * Sets the description/content of the announcement.
     * @param description The description string to set.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the start/publish date of the announcement.
     * @return The start LocalDateTime.
     */
    public LocalDateTime getStart() { return start; }

    /**
     * Sets the start/publish date of the announcement.
     * @param start The start LocalDateTime to set.
     */
    public void setStart(LocalDateTime start) { this.start = start; }

    /**
     * Gets the end/expiry date of the announcement.
     * @return The end LocalDateTime, or null if it doesn't expire.
     */
    public LocalDateTime getEnd() { return end; }

    /**
     * Sets the end/expiry date of the announcement.
     * @param end The end LocalDateTime to set. Can be null.
     */
    public void setEnd(LocalDateTime end) { this.end = end; }

    /**
     * Gets the creation timestamp of the record.
     * @return The creation LocalDateTime.
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the creation timestamp of the record.
     * @param createdAt The creation LocalDateTime to set.
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Gets the last update timestamp of the record.
     * @return The last update LocalDateTime.
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
     * Sets the last update timestamp of the record.
     * @param updatedAt The last update LocalDateTime to set.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Gets the soft-deletion timestamp of the record.
     * @return The deletion LocalDateTime, or null if not deleted.
     */
    public LocalDateTime getDeletedAt() { return deletedAt; }

    /**
     * Sets the soft-deletion timestamp of the record.
     * @param deletedAt The deletion LocalDateTime to set.
     */
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}