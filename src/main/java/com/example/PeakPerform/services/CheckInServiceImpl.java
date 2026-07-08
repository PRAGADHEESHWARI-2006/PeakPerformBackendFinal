package com.example.PeakPerform.services;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.*;
import com.example.PeakPerform.enums.*;
import com.example.PeakPerform.exception.*;
import com.example.PeakPerform.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;
    private final KeyResultRepository keyResultRepository;
    private final AppUserRepository appUserRepository;
    private final ObjectiveService objectiveService;

    public CheckInServiceImpl(CheckInRepository checkInRepository,
                              KeyResultRepository keyResultRepository,
                              AppUserRepository appUserRepository,
                              ObjectiveService objectiveService) {
        this.checkInRepository = checkInRepository;
        this.keyResultRepository = keyResultRepository;
        this.appUserRepository = appUserRepository;
        this.objectiveService = objectiveService;
    }

    // ---------------- SUBMIT ----------------
    @Override
    public CheckInResponseDto submitCheckIn(CheckInRequestDto dto, Long userId) {

        KeyResultEntity kr = keyResultRepository.findById(dto.getKeyResultId())
                .orElseThrow(() -> new ResourceNotFoundException("Key Result not found"));

        if (kr.getStatus() == KeyResultStatus.COMPLETED) {
            throw new BusinessValidationException("Cannot submit check-in for COMPLETED Key Result.");
        }

        if (kr.getObjective().getStatus() != ObjectiveStatus.ACTIVE) {
            throw new BusinessValidationException("Check-ins allowed only for ACTIVE objectives.");
        }

        boolean exists = checkInRepository
                .existsByKeyResultIdAndReviewStatusAndSubmittedById(
                        dto.getKeyResultId(),
                        ReviewStatus.PENDING,
                        userId
                );

        if (exists) {
            throw new BusinessValidationException(
                    "Pending check-in already exists for this Key Result.");
        }

        AppUserEntity user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CheckInEntity c = new CheckInEntity();
        c.setKeyResult(kr);
        c.setReportedValue(dto.getReportedValue());
        c.setConfidenceLevel(dto.getConfidenceLevel());
        c.setNotes(dto.getNotes());
        c.setSubmittedBy(user);
        c.setReviewStatus(ReviewStatus.PENDING);
        c.setSubmittedAt(LocalDateTime.now());

        return toDto(checkInRepository.save(c));
    }

    // ---------------- APPROVE ----------------
    @Override
    @Transactional
    public CheckInResponseDto approveCheckIn(Long checkInId, Long reviewerId) {

        CheckInEntity c = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckIn not found"));

        if (c.getReviewStatus() != ReviewStatus.PENDING) {
            throw new BusinessValidationException("Only PENDING check-ins can be approved.");
        }

        AppUserEntity reviewer = appUserRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        if (!(reviewer.getRole() == Role.ROLE_TEAM_LEAD ||
              reviewer.getRole() == Role.ROLE_PERFORMANCE_ADMIN)) {
            throw new BusinessValidationException("Not authorized to approve check-ins.");
        }

        c.setReviewStatus(ReviewStatus.APPROVED);
        c.setReviewedBy(reviewer);
        c.setReviewedAt(LocalDateTime.now());

        checkInRepository.save(c);

        KeyResultEntity kr = c.getKeyResult();
        kr.setCurrentValue(c.getReportedValue());

        double percent = (kr.getCurrentValue() / kr.getTargetValue()) * 100.0;

        if (percent >= 100) kr.setStatus(KeyResultStatus.COMPLETED);
        else if (percent >= 60) kr.setStatus(KeyResultStatus.ON_TRACK);
        else if (percent >= 30) kr.setStatus(KeyResultStatus.AT_RISK);
        else kr.setStatus(KeyResultStatus.BEHIND);

        keyResultRepository.save(kr);

        objectiveService.computeObjectiveProgress(kr.getObjective().getId());

        return toDto(c);
    }

    // ---------------- REJECT ----------------
    @Override
    public CheckInResponseDto rejectCheckIn(Long id, String reason, Long reviewerId) {

        CheckInEntity c = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CheckIn not found"));

        if (c.getReviewStatus() != ReviewStatus.PENDING) {
            throw new BusinessValidationException("Only PENDING check-ins can be rejected.");
        }

        if (reason == null || reason.isBlank()) {
            throw new BusinessValidationException("Rejection reason required.");
        }

        AppUserEntity reviewer = appUserRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        c.setReviewStatus(ReviewStatus.REJECTED);
        c.setReviewedBy(reviewer);
        c.setReviewedAt(LocalDateTime.now());
        c.setRejectionReason(reason);

        return toDto(checkInRepository.save(c));
    }

    // ---------------- GETTERS ----------------
    @Override
    public Page<CheckInResponseDto> getCheckInsByKeyResult(Long keyResultId, Pageable pageable) {
        return checkInRepository.findByKeyResultId(keyResultId, pageable).map(this::toDto);
    }

    @Override
    public Page<CheckInResponseDto> getMyCheckIns(Long userId, Pageable pageable) {
        return checkInRepository.findBySubmittedByIdOrderBySubmittedAtDesc(userId, pageable)
                .map(this::toDto);
    }

    @Override
    public List<CheckInResponseDto> getPendingCheckInsForTeamLead(Long userId) {

        AppUserEntity user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CheckInEntity> list;

        if (user.getRole() == Role.ROLE_PERFORMANCE_ADMIN) {
            list = checkInRepository.findByReviewStatusOrderBySubmittedAtAsc(ReviewStatus.PENDING);
        } else {
            list = checkInRepository
                    .findByKeyResultObjectiveTeamLeadIdAndReviewStatusOrderBySubmittedAtAsc(
                            userId, ReviewStatus.PENDING);
        }

        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ---------------- DTO ----------------
    private CheckInResponseDto toDto(CheckInEntity c) {

        CheckInResponseDto dto = new CheckInResponseDto();

        dto.setId(c.getId());
        dto.setKeyResultId(c.getKeyResult().getId());
        dto.setKeyResultTitle(c.getKeyResult().getTitle());
        dto.setReportedValue(c.getReportedValue());
        dto.setConfidenceLevel(c.getConfidenceLevel());
        dto.setNotes(c.getNotes());

        dto.setSubmittedById(c.getSubmittedBy().getId());
        dto.setSubmittedByName(c.getSubmittedBy().getFullName());

        dto.setSubmittedAt(c.getSubmittedAt());
        dto.setReviewStatus(c.getReviewStatus().name());

        if (c.getReviewedBy() != null) {
            dto.setReviewedById(c.getReviewedBy().getId());
            dto.setReviewedByName(c.getReviewedBy().getFullName());
        }

        dto.setReviewedAt(c.getReviewedAt());
        dto.setRejectionReason(c.getRejectionReason());

        return dto;
    }
}