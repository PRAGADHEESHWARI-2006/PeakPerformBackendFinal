package com.example.PeakPerform.dto;

import java.time.LocalDateTime;

public class CheckInResponseDto {

    private Long id;

    private Long keyResultId;
    private String keyResultTitle;

    private Double reportedValue;
    private Integer confidenceLevel;
    private String notes;

    private Long submittedById;
    private String submittedByName;
    private LocalDateTime submittedAt;

    private String reviewStatus;

    private Long reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;

    private String rejectionReason;

    // ---------------- GETTERS ----------------

    public Long getId() {
        return id;
    }

    public Long getKeyResultId() {
        return keyResultId;
    }

    public String getKeyResultTitle() {
        return keyResultTitle;
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

    public Long getSubmittedById() {
        return submittedById;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public Long getReviewedById() {
        return reviewedById;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    // ---------------- SETTERS ----------------

    public void setId(Long id) {
        this.id = id;
    }

    public void setKeyResultId(Long keyResultId) {
        this.keyResultId = keyResultId;
    }

    public void setKeyResultTitle(String keyResultTitle) {
        this.keyResultTitle = keyResultTitle;
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

    public void setSubmittedById(Long submittedById) {
        this.submittedById = submittedById;
    }

    public void setSubmittedByName(String submittedByName) {
        this.submittedByName = submittedByName;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public void setReviewedById(Long reviewedById) {
        this.reviewedById = reviewedById;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}