package com.activa.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MemberRequest {

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELED
    }

    private UUID id;
    private Member member;
    private RequestStatus status;
    private String note;
    private String motivation; // New field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Full constructor for creating an instance from database data.
     */
    public MemberRequest(UUID id, Member member, RequestStatus status, String note, String motivation, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.member = member;
        this.status = status;
        this.note = note;
        this.motivation = motivation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Simplified constructor for creating a new request.
     */
    public MemberRequest(Member member, String motivation) {
        this.id = UUID.randomUUID();
        this.member = member;
        this.motivation = motivation;
        this.status = RequestStatus.PENDING;
    }

    // --- Standard Getters and Setters ---
    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public Member getMember() {return member;}
    public void setMember(Member member) {this.member = member;}

    public RequestStatus getStatus() {return status;}
    public void setStatus(RequestStatus status) {this.status = status;}

    public String getNote() {return note;}
    public void setNote(String note) {this.note = note;}

    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public LocalDateTime getDeletedAt() {return deletedAt;}
    public void setDeletedAt(LocalDateTime deletedAt) {this.deletedAt = deletedAt;}

    // --- PropertyValueFactory Getters for TableView ---

    public String getMemberName() {
        return member != null ? member.getName() : "N/A";
    }

    public String getNim() {
        return member != null ? member.getNim() : "N/A";
    }

    public String getRequestDate() {
        return createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A";
    }
}
