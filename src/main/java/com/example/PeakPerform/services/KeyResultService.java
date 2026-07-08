
package com.example.PeakPerform.services;

import com.example.PeakPerform.dto.*;
import java.util.List;

public interface KeyResultService {

    KeyResultResponseDto createKeyResult(KeyResultRequestDto dto);

    KeyResultResponseDto updateCurrentValue(Long keyResultId, Double newValue, String newStatus);

    List<KeyResultResponseDto> getKeyResultsByObjective(Long objectiveId);

    KeyResultResponseDto getKeyResultById(Long id);

    KeyResultResponseDto updateKeyResult(Long id, KeyResultRequestDto dto);

    void deleteKeyResult(Long id);
}

