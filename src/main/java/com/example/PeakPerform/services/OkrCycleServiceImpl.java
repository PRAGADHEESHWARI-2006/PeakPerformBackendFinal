package com.example.PeakPerform.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.*;
import com.example.PeakPerform.enums.*;
import com.example.PeakPerform.exception.*;
import com.example.PeakPerform.repository.*;

@Service
public class OkrCycleServiceImpl implements OkrCycleService {

    private final OkrCycleRepository cycleRepository;
    private final AppUserRepository userRepository;
    private final ObjectiveRepository objectiveRepository;
    private final KeyResultRepository keyResultRepository;
    private final CheckInRepository checkInRepository;

    public OkrCycleServiceImpl(
            OkrCycleRepository cycleRepository,
            AppUserRepository userRepository,
            ObjectiveRepository objectiveRepository,
            KeyResultRepository keyResultRepository,
            CheckInRepository checkInRepository) {

        this.cycleRepository = cycleRepository;
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
        this.keyResultRepository = keyResultRepository;
        this.checkInRepository = checkInRepository;
    }

    @Override
    public OkrCycleResponseDto createCycle(OkrCycleRequestDto dto, Long createdByUserId) {

        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new BusinessValidationException("End date must be after start date.");
        }

        AppUserEntity creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        OkrCycleEntity cycle = new OkrCycleEntity();
        cycle.setTitle(dto.getTitle());
       cycle.setCycleType(CycleType.valueOf(dto.getCycleType()));
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());
        cycle.setStatus(CycleStatus.DRAFT);
        cycle.setCreatedBy(creator);

        return mapToDto(cycleRepository.save(cycle));
    }

    @Override
    public OkrCycleResponseDto activateCycle(Long cycleId) {

        OkrCycleEntity cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BusinessValidationException(
                    "Only DRAFT cycles can be activated. Current status: " + cycle.getStatus());
        }

        if (cycleRepository.existsByStatus(CycleStatus.ACTIVE)) {
            throw new BusinessValidationException(
                    "Another OKR cycle is already ACTIVE. Close it before activating a new one.");
        }

        if (cycle.getStartDate().isAfter(LocalDate.now())) {
            throw new BusinessValidationException(
                    "Cannot activate a cycle whose start date is in the future.");
        }

        cycle.setStatus(CycleStatus.ACTIVE);
        return mapToDto(cycleRepository.save(cycle));
    }

    @Override
    @Transactional
    public OkrCycleResponseDto closeCycle(Long cycleId) {

        OkrCycleEntity cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.ACTIVE) {
            throw new BusinessValidationException("Only ACTIVE cycles can be closed.");
        }

        cycle.setStatus(CycleStatus.CLOSED);

        objectiveRepository.bulkUpdateStatusByCycleId(
                cycleId,
                ObjectiveStatus.CANCELLED
        );

        return mapToDto(cycleRepository.save(cycle));
    }

    @Override
    public List<OkrCycleResponseDto> getAllCycles() {
        return cycleRepository.findAllOrderByStartDateDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OkrCycleResponseDto getCycleById(Long id) {

        OkrCycleEntity cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        return mapToDto(cycle);
    }

    @Override
    public OkrCycleResponseDto updateCycle(Long id, OkrCycleRequestDto dto) {

        OkrCycleEntity cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BusinessValidationException("Only DRAFT cycles can be edited.");
        }

        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new BusinessValidationException("End date must be after start date.");
        }

        cycle.setTitle(dto.getTitle());
        cycle.setCycleType(CycleType.valueOf(dto.getCycleType()));
        cycle.setStartDate(dto.getStartDate());
        cycle.setEndDate(dto.getEndDate());

        return mapToDto(cycleRepository.save(cycle));
    }

    @Override
    public void deleteCycle(Long id) {

        OkrCycleEntity cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BusinessValidationException(
                    "Only DRAFT cycles with no objectives can be deleted.");
        }

        boolean hasObjectives = !objectiveRepository.findByCycleId(id).isEmpty();

        if (hasObjectives) {
            throw new BusinessValidationException(
                    "Cannot delete a cycle that has objectives. Archive it instead.");
        }

        cycleRepository.delete(cycle);
    }

    @Override
    @Transactional(readOnly = true)
    public CycleSummaryStatsDto getCycleSummaryStats(Long cycleId) {

        CycleSummaryStatsDto dto = new CycleSummaryStatsDto();

        long totalObjectives = objectiveRepository.countByCycleId(cycleId);
        long activeObjectives = objectiveRepository.countByCycleIdAndStatus(cycleId, ObjectiveStatus.ACTIVE);
        long completedObjectives = objectiveRepository.countByCycleIdAndStatus(cycleId, ObjectiveStatus.COMPLETED);
        long cancelledObjectives = objectiveRepository.countByCycleIdAndStatus(cycleId, ObjectiveStatus.CANCELLED);

        Double avg = objectiveRepository.averageProgressByCycleId(cycleId);
        if (avg == null) avg = 0.0;

        dto.setTotalObjectives(totalObjectives);
        dto.setActiveObjectives(activeObjectives);
        dto.setCompletedObjectives(completedObjectives);
        dto.setCancelledObjectives(cancelledObjectives);
        dto.setAverageProgress(Math.round(avg * 10.0) / 10.0);
      

        dto.setOnTrackKeyResults(
                keyResultRepository.countByCycleIdAndStatus(cycleId, KeyResultStatus.ON_TRACK)
        );

        dto.setAtRiskKeyResults(
                keyResultRepository.countByCycleIdAndStatus(cycleId, KeyResultStatus.AT_RISK)
        );

        dto.setBehindKeyResults(
                keyResultRepository.countByCycleIdAndStatus(cycleId, KeyResultStatus.BEHIND)
        );

        dto.setPendingCheckIns(
                checkInRepository.countByCycleIdAndStatus(cycleId,ReviewStatus.PENDING)
        );

        return dto;
    }

    private OkrCycleResponseDto mapToDto(OkrCycleEntity cycle) {

        OkrCycleResponseDto dto = new OkrCycleResponseDto();

        dto.setId(cycle.getId());
        dto.setTitle(cycle.getTitle());
        dto.setCycleType(cycle.getCycleType() != null 
        ? cycle.getCycleType().name() 
        : null);
        dto.setStartDate(cycle.getStartDate());
        dto.setEndDate(cycle.getEndDate());
        
        dto.setStatus(cycle.getStatus() != null
        ? cycle.getStatus().name()
        : null);
        dto.setCreatedById(cycle.getCreatedBy().getId());
        dto.setCreatedAt(cycle.getCreatedAt());

        return dto;
    }

    @Override
    @Transactional
    public void computeObjectiveProgress(Long objectiveId) {
        ObjectiveEntity obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        List<KeyResultEntity> krs = keyResultRepository.findByObjectiveId(objectiveId);

        if (krs.isEmpty()) return;

        double total = 0.0;
        boolean allCompleted = true;

        for (KeyResultEntity kr : krs) {
            double progress = 0.0;
            if (kr.getType() == KeyResultType.BOOLEAN) {
                progress = kr.getCurrentValue() >= 1.0 ? 100.0 : 0.0;
            } else {
                if (kr.getTargetValue() > 0) {
                    progress = (kr.getCurrentValue() / kr.getTargetValue()) * 100.0;
                }
                progress = Math.min(progress, 100.0);
            }

            total += progress;

            if (progress < 100.0) {
                allCompleted = false;
            }
        }

        double newProgress = Math.round((total / krs.size()) * 10.0) / 10.0;
        obj.setProgressPercent(newProgress);

        if (allCompleted && obj.getStatus() == ObjectiveStatus.ACTIVE) {
            obj.setStatus(ObjectiveStatus.COMPLETED);
        }

        objectiveRepository.save(obj);
    }
}