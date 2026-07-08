package com.example.PeakPerform.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.services.ProgressSnapshotService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/snapshots")
public class ProgressSnapshotController {

    private final ProgressSnapshotService service;

    public ProgressSnapshotController(ProgressSnapshotService service) {
        this.service = service;
    }

    @PostMapping("/{objectiveId}")
    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN','TEAM_LEAD')")
    public ResponseEntity<ProgressSnapshotResponseDto> capture(
            @PathVariable Long objectiveId,
            @AuthenticationPrincipal AppUserEntity user) {

        return new ResponseEntity<>(
                service.captureSnapshot(objectiveId, user.getId()),
                HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{objectiveId}/trend")
    public ResponseEntity<List<ProgressSnapshotResponseDto>> trend(
            @PathVariable Long objectiveId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        return ResponseEntity.ok(service.getProgressTrend(objectiveId, from, to));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{objectiveId}")
    public ResponseEntity<List<ProgressSnapshotResponseDto>> getAll(
            @PathVariable Long objectiveId) {

        return ResponseEntity.ok(service.getAllSnapshotsForObjective(objectiveId));
    }
}