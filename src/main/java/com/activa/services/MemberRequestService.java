package com.activa.services;

import com.activa.models.MemberRequest;
import com.activa.repositories.MemberRepository;
import com.activa.repositories.MemberRequestRepository;

import java.util.Optional;
import java.util.UUID;

public class MemberRequestService {

    private final MemberRequestRepository requestRepository;
    private final MemberRepository memberRepository;

    public MemberRequestService() {
        this.requestRepository = new MemberRequestRepository();
        this.memberRepository = new MemberRepository();
    }

    /**
     * Approves a pending member request. This action activates the member's account
     * and updates the request status to APPROVED.
     *
     * @param requestId    The ID of the request to approve.
     * @param approvalNote A note explaining the reason for approval.
     */
    public void approveRequest(UUID requestId, String approvalNote) {
        Optional<MemberRequest> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new IllegalStateException("Request with ID " + requestId + " not found.");
        }

        MemberRequest request = optionalRequest.get();

        if (request.getStatus() != MemberRequest.RequestStatus.PENDING) {
            System.err.println("Warning: Attempted to approve a request that is not pending. Status: " + request.getStatus());
            return;
        }

        requestRepository.update(request.getId(), MemberRequest.RequestStatus.APPROVED, approvalNote);
        memberRepository.updateStatus(request.getMemberId(), true);
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
}