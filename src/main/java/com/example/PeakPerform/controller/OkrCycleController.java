package com.example.PeakPerform.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.services.OkrCycleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cycles")
public class OkrCycleController {

    private final OkrCycleService service;

    public OkrCycleController(OkrCycleService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('PERFORMANCE_ADMIN')")
    public ResponseEntity<OkrCycleResponseDto> create(
            @Valid @RequestBody OkrCycleRequestDto dto,
            @AuthenticationPrincipal AppUserEntity user) {

        return new ResponseEntity<>(service.createCycle(dto, user.getId()), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OkrCycleResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAllCycles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OkrCycleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCycleById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PERFORMANCE_ADMIN')")
    public ResponseEntity<OkrCycleResponseDto> update(
            @PathVariable Long id,
            @RequestBody OkrCycleRequestDto dto) {

        return ResponseEntity.ok(service.updateCycle(id, dto));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('PERFORMANCE_ADMIN')")
    public ResponseEntity<OkrCycleResponseDto> activate(@PathVariable Long id) {
        return ResponseEntity.ok(service.activateCycle(id));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('PERFORMANCE_ADMIN')")
    public ResponseEntity<OkrCycleResponseDto> close(@PathVariable Long id) {
        return ResponseEntity.ok(service.closeCycle(id));
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('PERFORMANCE_ADMIN','TEAM_LEAD')")
    public ResponseEntity<CycleSummaryStatsDto> stats(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCycleSummaryStats(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PERFORMANCE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteCycle(id);
        return ResponseEntity.noContent().build();
    }
}