package com.example.PeakPerform.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "progress_snapshots")
public class ProgressSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objective_id", nullable = false)
    private ObjectiveEntity objective;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "progress_percent", nullable = false)
    private Double progressPercent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "captured_by", nullable = false)
    private AppUserEntity capturedBy;

    public ProgressSnapshotEntity() {
    }

    // Constructor required by SRS
    public ProgressSnapshotEntity(ObjectiveEntity objective,
            LocalDate snapshotDate,
            Double progressPercent,
            AppUserEntity capturedBy) {
        this.objective = objective;
        this.snapshotDate = snapshotDate;
        this.progressPercent = progressPercent;
        this.capturedBy = capturedBy;
    }

    // Optional full constructor
    public ProgressSnapshotEntity(Long id,
            ObjectiveEntity objective,
            LocalDate snapshotDate,
            Double progressPercent,
            AppUserEntity capturedBy) {
        this.id = id;
        this.objective = objective;
        this.snapshotDate = snapshotDate;
        this.progressPercent = progressPercent;
        this.capturedBy = capturedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObjectiveEntity getObjective() {
        return objective;
    }

    public void setObjective(ObjectiveEntity objective) {
        this.objective = objective;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(LocalDate snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public AppUserEntity getCapturedBy() {
        return capturedBy;
    }

    public void setCapturedBy(AppUserEntity capturedBy) {
        this.capturedBy = capturedBy;
    }
}