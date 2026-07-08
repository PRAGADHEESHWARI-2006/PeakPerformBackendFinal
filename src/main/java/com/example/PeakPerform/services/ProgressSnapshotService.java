package com.example.PeakPerform.services;

import com.example.PeakPerform.dto.ProgressSnapshotResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ProgressSnapshotService {

    ProgressSnapshotResponseDto captureSnapshot(Long objectiveId, Long capturedByUserId);

    List<ProgressSnapshotResponseDto> getProgressTrend(Long objectiveId,
                                                        LocalDate from,
                                                        LocalDate to);

    List<ProgressSnapshotResponseDto> getAllSnapshotsForObjective(Long objectiveId);
}
