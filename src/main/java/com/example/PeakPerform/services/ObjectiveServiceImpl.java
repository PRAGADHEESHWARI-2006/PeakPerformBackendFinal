package com.example.PeakPerform.services;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PeakPerform.dto.*;
import com.example.PeakPerform.entity.*;
import com.example.PeakPerform.enums.*;
import com.example.PeakPerform.exception.*;
import com.example.PeakPerform.repository.*;

@Service
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final AppUserRepository userRepository;
    private final OkrCycleRepository cycleRepository;
    private final KeyResultRepository keyResultRepository;

    public ObjectiveServiceImpl(
            ObjectiveRepository objectiveRepository,
            AppUserRepository userRepository,
            OkrCycleRepository cycleRepository,
            KeyResultRepository keyResultRepository) {

        this.objectiveRepository = objectiveRepository;
        this.userRepository = userRepository;
        this.cycleRepository = cycleRepository;
        this.keyResultRepository = keyResultRepository;
    }

    @Override
    public ObjectiveResponseDto createObjective(ObjectiveRequestDto dto, Long requesterId) {

        AppUserEntity requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long targetOwnerId = requester.getRole() == Role.ROLE_PERFORMANCE_ADMIN
                ? dto.getOwnerId()
                : requesterId;

        AppUserEntity owner = userRepository.findById(targetOwnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        OkrCycleEntity cycle = cycleRepository.findById(dto.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() == CycleStatus.CLOSED) {
            throw new BusinessValidationException("Cannot create objectives in a CLOSED OKR cycle.");
        }

        AppUserEntity teamLead = null;
        if (dto.getTeamLeadId() != null) {
            teamLead = userRepository.findById(dto.getTeamLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team lead not found"));

            if (teamLead.getRole() != Role.ROLE_TEAM_LEAD) {
                throw new BusinessValidationException("Assigned reviewer must have ROLE_TEAM_LEAD.");
            }
        }

        ObjectiveEntity obj = new ObjectiveEntity();
        obj.setTitle(dto.getTitle());
        obj.setDescription(dto.getDescription());
        obj.setOwner(owner);
        obj.setCycle(cycle);
        obj.setTeamLead(teamLead);
        obj.setStatus(ObjectiveStatus.DRAFT);
        obj.setProgressPercent(0.0);

        return mapToDto(objectiveRepository.save(obj));
    }

    @Override
    public ObjectiveResponseDto activateObjective(Long objectiveId, Long requesterId) {

        ObjectiveEntity obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        validateOwnershipOrAdmin(obj, requesterId);

        if (obj.getStatus() != ObjectiveStatus.DRAFT) {
            throw new BusinessValidationException("Only DRAFT objectives can be activated.");
        }

        List<KeyResultEntity> krs = keyResultRepository.findByObjectiveId(objectiveId);

        if (krs.isEmpty()) {
            throw new BusinessValidationException(
                    "Cannot activate an objective with no Key Results. Add at least one Key Result first.");
        }

        obj.setStatus(ObjectiveStatus.ACTIVE);

        return mapToDto(objectiveRepository.save(obj));
    }

    @Override
    public ObjectiveResponseDto pauseObjective(Long objectiveId, Long requesterId) {

        ObjectiveEntity obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        validateOwnershipOrAdmin(obj, requesterId);

        if (obj.getStatus() != ObjectiveStatus.ACTIVE) {
            throw new BusinessValidationException("Only ACTIVE objectives can be paused.");
        }

        obj.setStatus(ObjectiveStatus.PAUSED);
        return mapToDto(objectiveRepository.save(obj));
    }

    @Override
    public ObjectiveResponseDto resumeObjective(Long objectiveId, Long requesterId) {

        ObjectiveEntity obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        validateOwnershipOrAdmin(obj, requesterId);

        if (obj.getStatus() != ObjectiveStatus.PAUSED) {
            throw new BusinessValidationException("Only PAUSED objectives can be resumed.");
        }

        obj.setStatus(ObjectiveStatus.ACTIVE);
        return mapToDto(objectiveRepository.save(obj));
    }

    @Override
    @Transactional
    public void computeObjectiveProgress(Long objectiveId) {

        ObjectiveEntity obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        List<KeyResultEntity> krs = keyResultRepository.findByObjectiveId(objectiveId);

        if (krs.isEmpty())
            return;

        double total = 0.0;
        boolean allCompleted = true;

        for (KeyResultEntity kr : krs) {

            double progress = 0.0;

            if (kr.getType().name().equals("BOOLEAN")) {
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

    @Override
    public Page<ObjectiveResponseDto> getObjectives(Long ownerId, Long cycleId, Pageable pageable) {

        Page<ObjectiveEntity> page;

        if (ownerId != null && cycleId != null) {
            page = objectiveRepository.findByOwnerIdAndCycleId(ownerId, cycleId, pageable);
        } else if (ownerId != null) {
            page = objectiveRepository.findByOwnerId(ownerId, pageable);
        } else if (cycleId != null) {
            page = objectiveRepository.findByCycleId(cycleId, pageable);
        } else {
            page = objectiveRepository.findAll(pageable);
        }

        return page.map(this::mapToDto);
    }

    @Override
    public ObjectiveResponseDto getObjectiveById(Long id) {

        ObjectiveEntity obj = objectiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        return mapToDto(obj);
    }

    @Override
    public ObjectiveResponseDto updateObjective(Long id, ObjectiveRequestDto dto, Long requesterId) {

        ObjectiveEntity obj = objectiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        validateOwnershipOrAdmin(obj, requesterId);

        if (obj.getStatus() == ObjectiveStatus.COMPLETED ||
                obj.getStatus() == ObjectiveStatus.CANCELLED) {
            throw new BusinessValidationException(
                    "Cannot edit a " + obj.getStatus() + " objective.");
        }

        obj.setTitle(dto.getTitle());
        obj.setDescription(dto.getDescription());

        return mapToDto(objectiveRepository.save(obj));
    }

    @Override
    public void deleteObjective(Long id, Long requesterId) {

        ObjectiveEntity obj = objectiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found"));

        validateOwnershipOrAdmin(obj, requesterId);

        if (obj.getStatus() != ObjectiveStatus.DRAFT) {
            throw new BusinessValidationException("Only DRAFT objectives can be deleted.");
        }

        objectiveRepository.delete(obj);
    }

    private void validateOwnershipOrAdmin(ObjectiveEntity obj, Long requesterId) {

        AppUserEntity requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (requester.getRole() == Role.ROLE_PERFORMANCE_ADMIN) {
            return;
        }

        if (!obj.getOwner().getId().equals(requesterId)) {
            throw new BusinessValidationException(
                    "You can only manage your own objectives.");
        }
    }

    private ObjectiveResponseDto mapToDto(ObjectiveEntity obj) {

        ObjectiveResponseDto dto = new ObjectiveResponseDto();
        dto.setId(obj.getId());
        dto.setTitle(obj.getTitle());
        dto.setDescription(obj.getDescription());
        dto.setStatus(obj.getStatus());
        dto.setProgressPercent(obj.getProgressPercent());
        dto.setOwnerId(obj.getOwner().getId());
        dto.setCycleId(obj.getCycle().getId());

        if (obj.getTeamLead() != null) {
            dto.setTeamLeadId(obj.getTeamLead().getId());
        }

        return dto;
    }
}