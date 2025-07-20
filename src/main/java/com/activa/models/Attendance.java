package com.activa.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a single attendance record for a member in an activity.
 * This class links a Member to an Activity, signifying their presence.
 */
public class Attendance {

    private UUID id;
    private Activity activity;
    private Member member;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Full constructor for creating an Attendance instance, typically used when mapping from a database result.
     *
     * @param id          The unique identifier of the attendance record.
     * @param activity    The associated Activity object.
     * @param member      The associated Member object.
     * @param createdAt   The creation timestamp.
     * @param updatedAt   The last update timestamp.
     * @param deletedAt   The soft-deletion timestamp.
     */
    public Attendance(UUID id, Activity activity, Member member, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.activity = activity;
        this.member = member;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Simplified constructor for creating a new attendance record.
     *
     * @param activity The activity for which attendance is being taken.
     * @param member   The member being marked.
     */
    public Attendance(Activity activity, Member member) {
        this.id = UUID.randomUUID();
        this.activity = activity;
        this.member = member;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}