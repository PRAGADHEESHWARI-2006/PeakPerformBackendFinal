package com.example.PeakPerform.services;

import com.example.PeakPerform.dto.ProgressSnapshotResponseDto;
import com.example.PeakPerform.entity.*;
import com.example.PeakPerform.exception.*;
import com.example.PeakPerform.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressSnapshotServiceImpl implements ProgressSnapshotService {

    private final ProgressSnapshotRepository snapshotRepository;
    private final ObjectiveRepository objectiveRepository;
    private final AppUserRepository appUserRepository;

    public ProgressSnapshotServiceImpl(ProgressSnapshotRepository snapshotRepository,
                                       ObjectiveRepository objectiveRepository,
                                       AppUserRepository appUserRepository) {
        this.snapshotRepository = snapshotRepository;
        this.objectiveRepository = objectiveRepository;
        this.appUserRepository = appUserRepository;
    }

    // ---------------- CAPTURE SNAPSHOT ----------------
    @Override
    public ProgressSnapshotResponseDto captureSnapshot(Long objectiveId, Long userId) {

        ObjectiveEntity obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        LocalDate today = LocalDate.now();

        if (snapshotRepository.existsByObjectiveIdAndSnapshotDate(objectiveId, today)) {
            throw new BusinessValidationException(
                    "Snapshot already exists for today (" + today + ").");
        }

        AppUserEntity user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProgressSnapshotEntity snapshot = new ProgressSnapshotEntity();
        snapshot.setObjective(obj);
        snapshot.setSnapshotDate(today);
        snapshot.setProgressPercent(obj.getProgressPercent());
        snapshot.setCapturedBy(user);

        return toDto(snapshotRepository.save(snapshot));
    }

    // ---------------- TREND (DATE RANGE) ----------------
    @Override
    @Transactional(readOnly = true)
    public List<ProgressSnapshotResponseDto> getProgressTrend(Long objectiveId,
                                                               LocalDate from,
                                                               LocalDate to) {

        objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        List<ProgressSnapshotEntity> list;

        if (from != null && to != null) {

            if (to.isBefore(from)) {
                throw new BusinessValidationException("'to' date must not be before 'from' date.");
            }

            list = snapshotRepository.findByObjectiveIdAndSnapshotDateBetween(
                    objectiveId, from, to
            );

        } else {
            list = snapshotRepository.findByObjectiveIdOrderBySnapshotDateAsc(objectiveId);
        }

        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ---------------- ALL SNAPSHOTS ----------------
    @Override
    public List<ProgressSnapshotResponseDto> getAllSnapshotsForObjective(Long objectiveId) {

        objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        return snapshotRepository.findByObjectiveIdOrderBySnapshotDateAsc(objectiveId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ---------------- DTO MAPPER ----------------
    private ProgressSnapshotResponseDto toDto(ProgressSnapshotEntity p) {

        ProgressSnapshotResponseDto dto = new ProgressSnapshotResponseDto();

        dto.setId(p.getId());
        dto.setObjectiveId(p.getObjective().getId());
        dto.setObjectiveTitle(p.getObjective().getTitle());
        dto.setSnapshotDate(p.getSnapshotDate());
        dto.setProgressPercent(p.getProgressPercent());
        dto.setCapturedById(p.getCapturedBy().getId());
        dto.setCapturedByName(p.getCapturedBy().getFullName());

        return dto;
    }
}