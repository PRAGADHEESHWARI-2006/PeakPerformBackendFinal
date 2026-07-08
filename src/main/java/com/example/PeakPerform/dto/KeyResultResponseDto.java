package com.example.PeakPerform.dto;

import java.time.LocalDate;

public class KeyResultResponseDto {

    private Long id;
    private String title;
    private Long objectiveId;
    private String objectiveTitle;
    private String metricType;
    private Double targetValue;
    private Double currentValue;
    private String unit;
    private String status;
    private Double completionPercent;
    private LocalDate dueDate;

    // ---------------- GETTERS ----------------

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public String getObjectiveTitle() {
        return objectiveTitle;
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

    public String getStatus() {
        return status;
    }

    public Double getCompletionPercent() {
        return completionPercent;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // ---------------- SETTERS ----------------

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public void setObjectiveTitle(String objectiveTitle) {
        this.objectiveTitle = objectiveTitle;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompletionPercent(Double completionPercent) {
        this.completionPercent = completionPercent;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}