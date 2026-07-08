package com.example.PeakPerform.entity;

import java.time.LocalDate;

import com.example.PeakPerform.enums.KeyResultStatus;
import com.example.PeakPerform.enums.MetricType;

import com.example.PeakPerform.enums.KeyResultType;

import jakarta.persistence.*;

@Entity
@Table(name = "key_results")
public class KeyResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objective_id", nullable = false)
    private ObjectiveEntity objective;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false, length = 20)
    private MetricType metricType;

    @Column(name = "target_value", nullable = false)
    private Double targetValue;

    @Column(name = "current_value", nullable = false)
    private Double currentValue = 0.0;

    @Column(name = "unit", length = 50)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private KeyResultStatus status = KeyResultStatus.ON_TRACK;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
@Column(name = "type", nullable = false, length = 20)
private KeyResultType type;

    
    public KeyResultEntity(Long id, String title, ObjectiveEntity objective, MetricType metricType, Double targetValue,
            Double currentValue, String unit, KeyResultStatus status, LocalDate dueDate, KeyResultType type) {
        this.id = id;
        this.title = title;
        this.objective = objective;
        this.metricType = metricType;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.unit = unit;
        this.status = status;
        this.dueDate = dueDate;
        this.type = type;
    }

    public KeyResultEntity() {
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

    public ObjectiveEntity getObjective() {
        return objective;
    }

    public void setObjective(ObjectiveEntity objective) {
        this.objective = objective;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public KeyResultStatus getStatus() {
        return status;
    }

    public void setStatus(KeyResultStatus status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public KeyResultType getType() {
    return type;
}

public void setType(KeyResultType type) {
    this.type = type;
}


}