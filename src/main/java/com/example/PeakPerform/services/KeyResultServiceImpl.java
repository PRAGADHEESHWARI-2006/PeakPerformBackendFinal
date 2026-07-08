package com.example.PeakPerform.services;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.*;
import com.example.PeakPerform.enums.*;
import com.example.PeakPerform.exception.*;

import com.example.PeakPerform.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeyResultServiceImpl implements KeyResultService {

    private final KeyResultRepository keyResultRepository;
    private final ObjectiveRepository objectiveRepository;
    private final OkrCycleService okrCycleService;

    public KeyResultServiceImpl(KeyResultRepository keyResultRepository,
                                ObjectiveRepository objectiveRepository,
                                OkrCycleService okrCycleService) {
        this.keyResultRepository = keyResultRepository;
        this.objectiveRepository = objectiveRepository;
        this.okrCycleService = okrCycleService;
    }

    // ---------------- CREATE KEY RESULT ----------------
    @Override
    public KeyResultResponseDto createKeyResult(KeyResultRequestDto dto) {

        ObjectiveEntity objective = objectiveRepository.findById(dto.getObjectiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        if (objective.getStatus() == ObjectiveStatus.COMPLETED ||
            objective.getStatus() == ObjectiveStatus.CANCELLED) {
            throw new BusinessValidationException(
                    "Cannot add Key Results to a " + objective.getStatus() + " objective.");
        }

        KeyResultEntity kr = new KeyResultEntity();
        kr.setTitle(dto.getTitle());
        kr.setObjective(objective);
        kr.setUnit(dto.getUnit());
        kr.setDueDate(dto.getDueDate());

        String metricTypeStr = dto.getMetricType().toUpperCase();
        MetricType metricType;
        KeyResultType type;
        if (metricTypeStr.equals("NUMERIC")) {
            metricType = MetricType.NUMERIC;
            type = KeyResultType.METRIC;
        } else if (metricTypeStr.equals("PERCENTAGE")) {
            metricType = MetricType.PERCENTAGE;
            type = KeyResultType.METRIC;
        } else if (metricTypeStr.equals("BOOLEAN")) {
            metricType = MetricType.BOOLEAN;
            type = KeyResultType.BOOLEAN;
        } else {
            metricType = MetricType.valueOf(metricTypeStr);
            type = (metricType == MetricType.BOOLEAN) ? KeyResultType.BOOLEAN : KeyResultType.METRIC;
        }

        kr.setMetricType(metricType);
        kr.setType(type);

        if (type == KeyResultType.BOOLEAN) {
            kr.setTargetValue(1.0);
            kr.setCurrentValue(0.0);
        } else {
            kr.setTargetValue(dto.getTargetValue());
            kr.setCurrentValue(dto.getCurrentValue() != null ? dto.getCurrentValue() : 0.0);
        }

        kr.setStatus(KeyResultStatus.ON_TRACK);

        return toDto(keyResultRepository.save(kr));
    }

    private KeyResultResponseDto toDto(KeyResultEntity kr) {
        KeyResultResponseDto dto = new KeyResultResponseDto();
        dto.setId(kr.getId());
        dto.setTitle(kr.getTitle());
        dto.setObjectiveId(kr.getObjective().getId());
        dto.setObjectiveTitle(kr.getObjective().getTitle());
        dto.setMetricType(kr.getMetricType() != null ? kr.getMetricType().name() : null);
        dto.setTargetValue(kr.getTargetValue());
        dto.setCurrentValue(kr.getCurrentValue());
        dto.setUnit(kr.getUnit());
        dto.setStatus(kr.getStatus().name());
        dto.setDueDate(kr.getDueDate());

        double target = kr.getTargetValue() != null ? kr.getTargetValue() : 1.0;
        double current = kr.getCurrentValue() != null ? kr.getCurrentValue() : 0.0;
        double completion = (current / target) * 100.0;
        double completionPercent = Math.round(Math.min(completion, 100.0) * 10.0) / 10.0;
        dto.setCompletionPercent(completionPercent);

        return dto;
    }

    // ---------------- UPDATE CURRENT VALUE ----------------
    @Override
    @Transactional
    public KeyResultResponseDto updateCurrentValue(Long keyResultId,
                                                    Double newValue,
                                                    String newStatus) {

        KeyResultEntity kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new ResourceNotFoundException("Key Result not found"));

        if (kr.getStatus() == KeyResultStatus.COMPLETED) {
            throw new BusinessValidationException("Cannot update value of a COMPLETED Key Result.");
        }

        if (newValue < 0) {
            throw new BusinessValidationException("Current value cannot be negative.");
        }

        if (kr.getType() == KeyResultType.BOOLEAN && newValue > 1.0) {
            throw new BusinessValidationException("BOOLEAN key results accept only 0 or 1.");
        }

        kr.setCurrentValue(newValue);

        // status override OR auto-derive
        if (newStatus != null && !newStatus.isBlank()) {
            kr.setStatus(KeyResultStatus.valueOf(newStatus.toUpperCase()));
        } else {
            double percent = (kr.getCurrentValue() / kr.getTargetValue()) * 100.0;

            if (percent >= 100) kr.setStatus(KeyResultStatus.COMPLETED);
            else if (percent >= 60) kr.setStatus(KeyResultStatus.ON_TRACK);
            else if (percent >= 30) kr.setStatus(KeyResultStatus.AT_RISK);
            else kr.setStatus(KeyResultStatus.BEHIND);
        }

        KeyResultEntity saved = keyResultRepository.save(kr);

        okrCycleService.computeObjectiveProgress(kr.getObjective().getId());

        return toDto(saved);
    }

    // ---------------- GET BY OBJECTIVE ----------------
    @Override
    public List<KeyResultResponseDto> getKeyResultsByObjective(Long objectiveId) {

        objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        return keyResultRepository.findByObjectiveId(objectiveId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ---------------- GET BY ID ----------------
    @Override
    public KeyResultResponseDto getKeyResultById(Long id) {

        KeyResultEntity kr = keyResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key Result not found"));

        return toDto(kr);
    }

    // ---------------- UPDATE KEY RESULT ----------------
    @Override
    public KeyResultResponseDto updateKeyResult(Long id, KeyResultRequestDto dto) {

        KeyResultEntity kr = keyResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Key Result not found"));

        if (kr.getStatus() == KeyResultStatus.COMPLETED) {
            throw new BusinessValidationException("Cannot edit a COMPLETED Key Result.");
        }

        if (dto.getTargetValue() < kr.getCurrentValue()) {
            throw new BusinessValidationException(
                    "Target value cannot be less than the current value (" + kr.getCurrentValue() + ").");
        }

        kr.setTitle(dto.getTitle());
        kr.setTargetValue(dto.getTargetValue());
        kr.setUnit(dto.getUnit());
        kr.setDueDate(dto.getDueDate());

        return toDto(keyResultRepository.save(kr));
    }

    // ---------------- DELETE KEY RESULT ----------------
   @Override
public void deleteKeyResult(Long id) {

    KeyResultEntity kr = keyResultRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Key Result not found"));

    if (kr.getStatus() == KeyResultStatus.COMPLETED) {
        throw new BusinessValidationException("Cannot delete a COMPLETED Key Result.");
    }

    keyResultRepository.delete(kr);
}
}
