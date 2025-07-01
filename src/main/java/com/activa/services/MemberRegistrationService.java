package com.activa.services;

import com.activa.models.Member;
import com.activa.models.MemberRequest;
import com.activa.repositories.MemberRepository;
import com.activa.repositories.MemberRequestRepository;
import java.time.LocalDate;

/**
 * Handles the business logic for new member registrations.
 */
public class MemberRegistrationService {

    private final MemberRepository memberRepository;
    private final MemberRequestRepository requestRepository;

    public MemberRegistrationService() {
        this.memberRepository = new MemberRepository();
        this.requestRepository = new MemberRequestRepository();
    }

    /**
     * Creates a new member with an inactive status and a corresponding pending request for approval.
     * This should be executed within a single database transaction.
     *
     * @param nim       The new member's NIM.
     * @param name      The new member's full name.
     * @param birthdate The new member's date of birth.
     * @param address   The new member's address.
     * @return The newly created Member object.
     */
    public Member registerNewMember(String nim, String name, LocalDate birthdate, String address, String requestNote) {
        Member newMember = new Member(nim, name, birthdate, address);
        newMember.setActive(false);

        memberRepository.create(newMember);

        MemberRequest newRequest = new MemberRequest(newMember.getId(), requestNote);
        requestRepository.create(newRequest);

        return newMember;
    }
}