package com.example.PeakPerform.repository;

import com.example.PeakPerform.entity.ProgressSnapshotEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProgressSnapshotRepository extends JpaRepository<ProgressSnapshotEntity, Long> {

    // ---------------- ALL SNAPSHOTS FOR OBJECTIVE ----------------
    List<ProgressSnapshotEntity> findByObjectiveIdOrderBySnapshotDateAsc(Long objectiveId);

    // ---------------- DATE RANGE ----------------
    List<ProgressSnapshotEntity> findByObjectiveIdAndSnapshotDateBetween(
            Long objectiveId,
            LocalDate from,
            LocalDate to
    );

    // ---------------- DUPLICATE PREVENTION ----------------
    boolean existsByObjectiveIdAndSnapshotDate(Long objectiveId, LocalDate snapshotDate);

    // ---------------- OPTIONAL STATS ----------------
    @Query("""
        SELECT AVG(p.progressPercent)
        FROM ProgressSnapshotEntity p
        WHERE p.objective.id = :objectiveId
    """)
    Double averageProgressByObjective(@Param("objectiveId") Long objectiveId);
}