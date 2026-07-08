
package com.example.PeakPerform.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.PeakPerform.dto.ObjectiveRequestDto;
import com.example.PeakPerform.dto.ObjectiveResponseDto;

public interface ObjectiveService {

    ObjectiveResponseDto createObjective(ObjectiveRequestDto dto, Long requesterId);

    ObjectiveResponseDto activateObjective(Long objectiveId, Long requesterId);

    ObjectiveResponseDto pauseObjective(Long objectiveId, Long requesterId);

    ObjectiveResponseDto resumeObjective(Long objectiveId, Long requesterId);

    void computeObjectiveProgress(Long objectiveId);

    Page<ObjectiveResponseDto> getObjectives(Long ownerId, Long cycleId, Pageable pageable);

    ObjectiveResponseDto getObjectiveById(Long id);

    ObjectiveResponseDto updateObjective(Long id, ObjectiveRequestDto dto, Long requesterId);

    void deleteObjective(Long id, Long requesterId);
}