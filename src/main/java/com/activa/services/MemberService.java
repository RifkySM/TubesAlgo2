package com.activa.services;

import com.activa.models.Member;
import com.activa.repositories.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles business logic for managing existing Member entities.
 */
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService() {
        this.memberRepository = new MemberRepository();
    }

    /**
     * Retrieves a list of all non-deleted members.
     *
     * @return A List of all active Member objects.
     */
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Finds a single member by their unique ID.
     *
     * @param memberId The UUID of the member to find.
     * @return An Optional containing the Member if found, otherwise empty.
     */
    public Optional<Member> findMemberById(UUID memberId) {
        return memberRepository.findById(memberId);
    }

    /**
     * Updates the profile information of an existing member.
     *
     * @param memberId  The ID of the member to update.
     * @param newNim    The member's new NIM.
     * @param newName   The member's new full name.
     * @param newBirthdate The member's new date of birth.
     * @param newAddress The member's new address.
     */
    public void updateMemberDetails(UUID memberId, String newNim, String newName, LocalDate newBirthdate, String newAddress) {
        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Member with ID " + memberId + " not found."));

        existingMember.setNim(newNim);
        existingMember.setName(newName);
        existingMember.setBirthdate(newBirthdate);
        existingMember.setAddress(newAddress);

        memberRepository.update(existingMember);
    }

    /**
     * Deactivates a member's account.
     *
     * @param memberId The ID of the member to deactivate.
     */
    public void deactivateMember(UUID memberId) {
        memberRepository.updateStatus(memberId, false);
    }

    /**
     * Reactivates a member's account.
     *
     * @param memberId The ID of the member to reactivate.
     */
    public void reactivateMember(UUID memberId) {
        memberRepository.updateStatus(memberId, true);
    }

    /**
     * Soft-deletes a member from the system.
     * The record remains in the database but is marked as deleted.
     *
     * @param memberId The ID of the member to delete.
     */
    public void deleteMember(UUID memberId) {
        memberRepository.softDeleteById(memberId);
    }
}