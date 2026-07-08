
package com.example.PeakPerform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CheckInRequestDto {

    @NotNull(message = "Key Result ID is required")
    private Long keyResultId;

    @NotNull(message = "Reported value is required")
    private Double reportedValue;

    @NotNull(message = "Confidence level is required")
    @Min(value = 1, message = "Confidence level must be at least 1")
    @Max(value = 10, message = "Confidence level must be at most 10")
    private Integer confidenceLevel;

    private String notes;

    // ---------------- GETTERS ----------------

    public Long getKeyResultId() {
        return keyResultId;
    }

    public Double getReportedValue() {
        return reportedValue;
    }

    public Integer getConfidenceLevel() {
        return confidenceLevel;
    }

    public String getNotes() {
        return notes;
    }

    // ---------------- SETTERS ----------------

    public void setKeyResultId(Long keyResultId) {
        this.keyResultId = keyResultId;
    }

    public void setReportedValue(Double reportedValue) {
        this.reportedValue = reportedValue;
    }

    public void setConfidenceLevel(Integer confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}