package com.example.PeakPerform.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class KeyResultRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Objective ID is required")
    private Long objectiveId;

    @NotNull(message = "Metric type is required")
    private String metricType;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    private Double targetValue;

    private Double currentValue = 0.0;

    private String unit;

    private LocalDate dueDate;

    // ---------------- GETTERS ----------------

    public String getTitle() {
        return title;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public String getMetricType() {
        return metricType;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // ---------------- SETTERS ----------------

    public void setTitle(String title) {
        this.title = title;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}