package com.activa.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Member {

    private UUID id;
    private User user; // Assuming a User class exists
    private String nim;
    private String name;
    private String email;
    private LocalDate birthdate;
    private String address;
    private boolean isActive;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Formatter for the birthdate, used by the TableView
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    /**
     * Full constructor for creating a Member instance from database data.
     * This matches the fields in your MemberRepository.
     */
    public Member(UUID id, User user, String nim, String name, String email, LocalDate birthdate, String address, boolean isActive, LocalDateTime joinedAt, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.user = user;
        this.nim = nim;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.isActive = isActive;
        this.joinedAt = joinedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Simplified constructor for creating a new member.
     */
    public Member(String nim, String name, String email, LocalDate birthdate, String address) {
        this.id = UUID.randomUUID();
        this.user = null; // Can be set later
        this.nim = nim;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.isActive = true; // New members are active by default
        this.joinedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = null; // Not deleted
    }


    // --- Getters and Setters for all fields ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }


    /**
     * Returns the member's status as a readable string ("Active" or "Inactive").
     * This method is used by the PropertyValueFactory in the TableView.
     * @return The status string.
     */
    public String getStatus() {
        return isActive ? "Active" : "Inactive";
    }

    /**
     * Returns the birthdate as a formatted string.
     * This method is used by the PropertyValueFactory in the TableView.
     * @return Formatted birthdate string.
     */
    public String getBirthdateFormatted() {
        if (birthdate == null) {
            return "";
        }
        return birthdate.format(DATE_FORMATTER);
    }
}
