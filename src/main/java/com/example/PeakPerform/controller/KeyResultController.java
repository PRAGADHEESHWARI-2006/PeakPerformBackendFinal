package com.example.PeakPerform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.services.KeyResultService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/key-results")
public class KeyResultController {

    private final KeyResultService service;

    public KeyResultController(KeyResultService service) {
        this.service = service;
    }
     
    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PostMapping
    public ResponseEntity<KeyResultResponseDto> create(
            @Valid @RequestBody KeyResultRequestDto dto) {

        return new ResponseEntity<>(service.createKeyResult(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<KeyResultResponseDto>> getByObjective(
            @RequestParam Long objectiveId) {

        return ResponseEntity.ok(service.getKeyResultsByObjective(objectiveId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KeyResultResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getKeyResultById(id));
    }
    

    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<KeyResultResponseDto> update(
            @PathVariable Long id,
            @RequestBody KeyResultRequestDto dto) {

        return ResponseEntity.ok(service.updateKeyResult(id, dto));
    }


    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @PatchMapping("/{id}/value")
    public ResponseEntity<KeyResultResponseDto> updateValue(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        Double value = Double.valueOf(body.get("currentValue").toString());
        String status = body.get("status") != null ? body.get("status").toString() : null;

        return ResponseEntity.ok(service.updateCurrentValue(id, value, status));
    }
    

    @PreAuthorize("hasAnyRole('GOAL_OWNER','PERFORMANCE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteKeyResult(id);
        return ResponseEntity.ok("KeyResult deleted successfully.");
    }
}