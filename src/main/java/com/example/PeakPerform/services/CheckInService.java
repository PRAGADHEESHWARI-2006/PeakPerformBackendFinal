package com.example.PeakPerform.services;

import com.example.PeakPerform.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CheckInService {

    CheckInResponseDto submitCheckIn(CheckInRequestDto dto, Long submittedByUserId);

    CheckInResponseDto approveCheckIn(Long checkInId, Long reviewerUserId);

    CheckInResponseDto rejectCheckIn(Long checkInId, String rejectionReason, Long reviewerUserId);

    Page<CheckInResponseDto> getCheckInsByKeyResult(Long keyResultId, Pageable pageable);

    Page<CheckInResponseDto> getMyCheckIns(Long userId, Pageable pageable);

    List<CheckInResponseDto> getPendingCheckInsForTeamLead(Long userId);
}
