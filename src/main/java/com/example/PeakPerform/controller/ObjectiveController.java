package com.example.PeakPerform.controller;

import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.services.ObjectiveService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController {

    private final ObjectiveService service;

    public ObjectiveController(ObjectiveService service) {
        this.service = service;
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PostMapping
    public ResponseEntity<ObjectiveResponseDto> create(
            @Valid @RequestBody ObjectiveRequestDto dto,
            @AuthenticationPrincipal AppUserEntity user) {

        return new ResponseEntity<>(
                service.createObjective(dto, user.getId()),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ObjectiveResponseDto>> getAll(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long cycleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getObjectives(ownerId, cycleId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectiveResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getObjectiveById(id));
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ObjectiveResponseDto> update(
            @PathVariable Long id,
            @RequestBody ObjectiveRequestDto dto,
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.updateObjective(id, dto, user.getId()));
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<ObjectiveResponseDto> activate(@PathVariable Long id,
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.activateObjective(id, user.getId()));
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PutMapping("/{id}/pause")
    public ResponseEntity<ObjectiveResponseDto> pause(@PathVariable Long id,
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.pauseObjective(id, user.getId()));
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PutMapping("/{id}/resume")
    public ResponseEntity<ObjectiveResponseDto> resume(@PathVariable Long id,
            @AuthenticationPrincipal AppUserEntity user) {

        return ResponseEntity.ok(service.resumeObjective(id, user.getId()));
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUserEntity user) {

        service.deleteObjective(id, user.getId());
        return ResponseEntity.ok("Objective deleted successfully.");
    }
}