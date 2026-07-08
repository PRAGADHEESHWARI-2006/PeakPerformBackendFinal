package com.example.PeakPerform.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.PeakPerform.enums.CycleStatus;
import com.example.PeakPerform.enums.CycleType;

import jakarta.persistence.*;

@Entity
@Table(name = "okr_cycles")
public class OkrCycleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false, length = 20)
    private CycleType cycleType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CycleStatus status = CycleStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUserEntity createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    

    public OkrCycleEntity() {
    }

    public OkrCycleEntity(Long id, String title, CycleType cycleType,
                    LocalDate startDate, LocalDate endDate,
                    CycleStatus status, AppUserEntity createdBy,
                    LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.cycleType = cycleType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public CycleType getCycleType() { return cycleType; }

    public void setCycleType(CycleType cycleType) { this.cycleType = cycleType; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public CycleStatus getStatus() { return status; }

    public void setStatus(CycleStatus status) { this.status = status; }

    public AppUserEntity getCreatedBy() { return createdBy; }

    public void setCreatedBy(AppUserEntity createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }





}