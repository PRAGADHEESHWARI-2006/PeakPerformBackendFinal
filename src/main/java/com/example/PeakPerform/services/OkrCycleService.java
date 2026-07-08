
package com.example.PeakPerform.services;

import java.util.List;

import com.example.PeakPerform.dto.*;

public interface OkrCycleService {

    OkrCycleResponseDto createCycle(OkrCycleRequestDto dto, Long createdByUserId);

    OkrCycleResponseDto activateCycle(Long cycleId);

    OkrCycleResponseDto closeCycle(Long cycleId);

    CycleSummaryStatsDto getCycleSummaryStats(Long cycleId);

    List<OkrCycleResponseDto> getAllCycles();

    OkrCycleResponseDto getCycleById(Long id);

    OkrCycleResponseDto updateCycle(Long id, OkrCycleRequestDto dto);

    void deleteCycle(Long id);

    void computeObjectiveProgress(Long id);
}