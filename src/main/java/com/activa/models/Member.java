package com.activa.models;

import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Member {

    private UUID id;
    private User user;
    private String nim;
    private String name;
    private String email; // The member's email address.
    private LocalDate birthdate;
    private String address;
    private boolean isActive;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Full constructor for creating a Member instance from database data.
     *
     * @param id        The unique identifier of the member record.
     * @param user      The associated user object (can be null).
     * @param nim       The member's student identification number (NIM).
     * @param name      The full name of the member.
     * @param email     The member's email address.
     * @param birthdate The member's date of birth.
     * @param address   The member's physical address.
     * @param isActive  Flag indicating if the member's account is active.
     * @param joinedAt  The timestamp when the user officially joined.
     * @param createdAt The timestamp when the record was created.
     * @param updatedAt The timestamp when the record was last updated.
     * @param deletedAt The timestamp for soft-deletion.
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
     * Simplified constructor for creating a new member with an associated user.
     *
     * @param user      The associated user object.
     * @param nim       The member's student identification number (NIM).
     * @param name      The full name of the member.
     * @param email     The member's email address.
     * @param birthdate The member's date of birth.
     * @param address   The member's physical address.
     */
    public Member(User user, String nim, String name, String email, LocalDate birthdate, String address) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.nim = nim;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.isActive = false;
        this.joinedAt = null;
    }

    /**
     * New simplified constructor for creating a new member without an initial user account.
     * This is useful for member requests.
     *
     * @param nim       The member's student identification number (NIM).
     * @param name      The full name of the member.
     * @param email     The member's email address.
     * @param birthdate The member's date of birth.
     * @param address   The member's physical address.
     */
    public Member(String nim, String name, String email, LocalDate birthdate, String address) {
        this.id = UUID.randomUUID();
        this.user = null;
        this.nim = nim;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.isActive = false;
        this.joinedAt = null;
    }


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

}
