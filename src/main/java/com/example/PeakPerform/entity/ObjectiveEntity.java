package com.example.PeakPerform.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.PeakPerform.enums.ObjectiveStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "objectives")
public class ObjectiveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_id", nullable = false)
    private OkrCycleEntity cycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_lead_id")
    private AppUserEntity teamLead;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ObjectiveStatus status = ObjectiveStatus.DRAFT;

    @Column(name = "progress_percent", nullable = false)
    private Double progressPercent = 0.0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ObjectiveEntity() {
    }

    public ObjectiveEntity(Long id, String title, String description,
            AppUserEntity owner, OkrCycleEntity cycle,
            AppUserEntity teamLead, ObjectiveStatus status,
            Double progressPercent, LocalDateTime createdAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.cycle = cycle;
        this.teamLead = teamLead;
        this.status = status;
        this.progressPercent = progressPercent;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppUserEntity getOwner() {
        return owner;
    }

    public void setOwner(AppUserEntity owner) {
        this.owner = owner;
    }

    public OkrCycleEntity getCycle() {
        return cycle;
    }

    public void setCycle(OkrCycleEntity cycle) {
        this.cycle = cycle;
    }

    public AppUserEntity getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(AppUserEntity teamLead) {
        this.teamLead = teamLead;
    }

    public ObjectiveStatus getStatus() {
        return status;
    }

    public void setStatus(ObjectiveStatus status) {
        this.status = status;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}