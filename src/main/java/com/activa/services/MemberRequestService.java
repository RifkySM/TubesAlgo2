package com.activa.services;

import com.activa.models.Member;
import com.activa.models.MemberRequest;
import com.activa.models.User;
import com.activa.repositories.MemberRepository;
import com.activa.repositories.MemberRequestRepository;
import com.activa.repositories.UserRepository;
import com.activa.utils.Helper;
import javafx.scene.control.Alert;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MemberRequestService {

    private final MemberRequestRepository requestRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public MemberRequestService() {
        this.requestRepository = new MemberRequestRepository();
        this.memberRepository = new MemberRepository();
        this.userRepository = new UserRepository(); // Initialize UserRepository
    }

    /**
     * Approves a pending member request. This action creates a new user account for the member,
     * activates their account, and updates the request status to APPROVED.
     *
     * @param requestId    The ID of the request to approve.
     * @param approvalNote A note explaining the reason for approval.
     */
    public void approveRequest(UUID requestId, String approvalNote) {
        Optional<MemberRequest> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            Helper.showAlert("Error", "Request not found.", Alert.AlertType.ERROR);
            throw new IllegalStateException("Request with ID " + requestId + " not found.");
        }

        MemberRequest request = optionalRequest.get();
        Member member = request.getMember();

        if (request.getStatus() != MemberRequest.RequestStatus.PENDING) {
            Helper.showAlert("Invalid Action", "This request has already been processed.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            if (member.getUser() != null) {
                Helper.showAlert("Error", "This member already has an associated user account.", Alert.AlertType.ERROR);
                return;
            }

            String username = member.getNim();
            if (member.getBirthdate() == null) {
                Helper.showAlert("Error", "Cannot create user because member's birthdate is missing.", Alert.AlertType.ERROR);
                return;
            }
            String defaultPassword = member.getBirthdate().format(DateTimeFormatter.ofPattern("ddMMyyyy"));
            String hashedPassword = Helper.hashPassword(defaultPassword);

            User newUser = new User("Member", member.getName(), username, hashedPassword);

            userRepository.create(newUser);
            member.setUser(newUser);
            memberRepository.update(member);

            requestRepository.update(request.getId(), MemberRequest.RequestStatus.APPROVED, approvalNote);
            memberRepository.updateStatus(member.getId(), true);

            Helper.showAlert("Success", "Member request has been approved and a user account has been created.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            Helper.showAlert("Approval Failed", "An error occurred while creating the user account: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Rejects a pending member request. This action updates the request status
     * to REJECTED. The member remains inactive.
     *
     * @param requestId     The ID of the request to reject.
     * @param rejectionNote A note explaining the reason for rejection.
     */
    public void rejectRequest(UUID requestId, String rejectionNote) {
        Optional<MemberRequest> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new IllegalStateException("Request with ID " + requestId + " not found.");
        }

        MemberRequest request = optionalRequest.get();

        if (request.getStatus() != MemberRequest.RequestStatus.PENDING) {
            System.err.println("Warning: Attempted to reject a request that is not pending. Status: " + request.getStatus());
            return;
        }

        requestRepository.update(request.getId(), MemberRequest.RequestStatus.REJECTED, rejectionNote);
    }

    /**
     * Retrieves all member requests from the repository.
     * @return A list of all member requests.
     */
    public List<MemberRequest> getAllRequests() {
        return requestRepository.findAll();
    }
}
