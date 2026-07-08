package com.example.PeakPerform.controller;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.services.CheckInService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInService service;

    public CheckInController(CheckInService service) {
        this.service = service;
    }


    @PreAuthorize("hasRole('GOAL_OWNER')")
    @PostMapping
    public ResponseEntity<CheckInResponseDto> submit(
            @Valid @RequestBody CheckInRequestDto dto,
            @AuthenticationPrincipal AppUserEntity user) {

        return new ResponseEntity<>(
                service.submitCheckIn(dto, user.getId()),
                HttpStatus.CREATED);
    }
    

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<CheckInResponseDto>> getByKeyResult(
            @RequestParam Long keyResultId,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getCheckInsByKeyResult(keyResultId, pageable));
    }
    
    @PreAuthorize("hasRole('GOAL_OWNER')") 
    @GetMapping("/mine")
    public ResponseEntity<Page<CheckInResponseDto>> myCheckIns(
            @AuthenticationPrincipal AppUserEntity user,
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getMyCheckIns(user.getId(), pageable));
    }


    @PreAuthorize("hasAnyRole('TEAM_LEAD','PERFORMANCE_ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<CheckInResponseDto>> pending(
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.getPendingCheckInsForTeamLead(user.getId()));
    }
    

    @PreAuthorize("hasAnyRole('TEAM_LEAD','PERFORMANCE_ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<CheckInResponseDto> approve(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.approveCheckIn(id, user.getId()));
    }

    @PreAuthorize("hasAnyRole('TEAM_LEAD','PERFORMANCE_ADMIN')")

    @PutMapping("/{id}/reject")
    public ResponseEntity<CheckInResponseDto> reject(
            @PathVariable Long id,
            @RequestBody CheckInReviewDto dto,
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.rejectCheckIn(id, dto.getRejectionReason(), user.getId()));
    }
}