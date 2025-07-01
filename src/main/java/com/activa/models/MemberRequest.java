package com.activa.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberRequest {

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELED
    }

    private UUID id;
    private UUID memberId;
    private RequestStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Full constructor for creating an instance from database data.
     *
     * @param id        The unique identifier of the request.
     * @param memberId  The ID of the member this request pertains to.
     * @param status    The current status of the request.
     * @param note      An optional note associated with the request.
     * @param createdAt The timestamp when the request was created.
     * @param updatedAt The timestamp when the request was last updated.
     * @param deletedAt The timestamp when the request was soft-deleted.
     */
    public MemberRequest(UUID id, UUID memberId, RequestStatus status, String note, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.memberId = memberId;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Simplified constructor for creating a new request.
     * Timestamps are typically handled by the database.
     *
     * @param memberId The ID of the member for whom the request is being made.
     * @param note     An initial note for the request.
     */
    public MemberRequest(UUID memberId, String note) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.note = note;
        this.status = RequestStatus.PENDING;
    }

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public UUID getMemberId() {return memberId;}
    public void setMemberId(UUID memberId) {this.memberId = memberId;}

    public RequestStatus getStatus() {return status;}
    public void setStatus(RequestStatus status) {this.status = status;}

    public String getNote() {return note;}
    public void setNote(String note) {this.note = note;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public LocalDateTime getDeletedAt() {return deletedAt;}
    public void setDeletedAt(LocalDateTime deletedAt) {this.deletedAt = deletedAt;}
}