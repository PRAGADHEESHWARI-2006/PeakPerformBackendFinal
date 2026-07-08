package com.example.PeakPerform.dto;

import java.time.LocalDate;

public class ProgressSnapshotResponseDto {

    private Long id;
    private Long objectiveId;
    private String objectiveTitle;
    private LocalDate snapshotDate;
    private Double progressPercent;
    private Long capturedById;
    private String capturedByName;

    // ---------------- GETTERS ----------------

    public Long getId() {
        return id;
    }

    public Long getObjectiveId() {
        return objectiveId;
    }

    public String getObjectiveTitle() {
        return objectiveTitle;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public Long getCapturedById() {
        return capturedById;
    }

    public String getCapturedByName() {
        return capturedByName;
    }

    // ---------------- SETTERS ----------------

    public void setId(Long id) {
        this.id = id;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public void setObjectiveTitle(String objectiveTitle) {
        this.objectiveTitle = objectiveTitle;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public void setCapturedById(Long capturedById) {
        this.capturedById = capturedById;
    }

    public void setCapturedByName(String capturedByName) {
        this.capturedByName = capturedByName;
    }
}