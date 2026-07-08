package com.example.PeakPerform.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.PeakPerform.enums.ReviewStatus;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Table(name = "check_ins")
public class CheckInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "key_result_id", nullable = false)
@JsonIgnore
private KeyResultEntity keyResult;

    @Column(name = "reported_value", nullable = false)
    private Double reportedValue;

    @Column(name = "confidence_level", nullable = false)
    private Integer confidenceLevel;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "submitted_by", nullable = false)
@JsonIgnore
private AppUserEntity submittedBy;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 20)
    private ReviewStatus reviewStatus = ReviewStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnore
    private AppUserEntity reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    public CheckInEntity() {
    }

    public CheckInEntity(Long id, KeyResultEntity keyResult,
            Double reportedValue, Integer confidenceLevel,
            String notes, AppUserEntity submittedBy,
            LocalDateTime submittedAt, ReviewStatus reviewStatus,
            AppUserEntity reviewedBy, LocalDateTime reviewedAt,
            String rejectionReason) {

        this.id = id;
        this.keyResult = keyResult;
        this.reportedValue = reportedValue;
        this.confidenceLevel = confidenceLevel;
        this.notes = notes;
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
        this.reviewStatus = reviewStatus;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.rejectionReason = rejectionReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KeyResultEntity getKeyResult() {
        return keyResult;
    }

    public void setKeyResult(KeyResultEntity keyResult) {
        this.keyResult = keyResult;
    }

    public Double getReportedValue() {
        return reportedValue;
    }

    public void setReportedValue(Double reportedValue) {
        this.reportedValue = reportedValue;
    }

    public Integer getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Integer confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public AppUserEntity getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(AppUserEntity submittedBy) {
        this.submittedBy = submittedBy;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public AppUserEntity getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(AppUserEntity reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}