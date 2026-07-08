package com.example.PeakPerform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PeakPerform.entity.KeyResultEntity;
import com.example.PeakPerform.enums.KeyResultStatus;

@Repository
public interface KeyResultRepository extends JpaRepository<KeyResultEntity, Long> {

    List<KeyResultEntity> findByObjectiveId(Long objectiveId);

    List<KeyResultEntity> findByObjectiveIdAndStatus(Long objectiveId,
            KeyResultStatus status);

   @Query("SELECT kr FROM KeyResultEntity kr WHERE kr.objective.id = :objectiveId AND kr.status <> com.example.PeakPerform.enums.KeyResultStatus.COMPLETED")
List<KeyResultEntity> findIncompleteByObjectiveId(
        @Param("objectiveId") Long objectiveId);

    @Query("SELECT COUNT(kr) FROM KeyResultEntity kr WHERE kr.objective.id = :objectiveId AND kr.status = :status")
    long countByObjectiveIdAndStatus(
            @Param("objectiveId") Long objectiveId,
            @Param("status") KeyResultStatus status);

    @Query("SELECT kr FROM KeyResultEntity kr WHERE kr.objective.cycle.id = :cycleId AND kr.status = :status")
    List<KeyResultEntity> findByCycleIdAndStatus(
            @Param("cycleId") Long cycleId,
            @Param("status") KeyResultStatus status);

@Query("SELECT COUNT(kr) FROM KeyResultEntity kr WHERE kr.objective.cycle.id = :cycleId")
long countByCycleId(@Param("cycleId") Long cycleId);

@Query("SELECT COUNT(kr) FROM KeyResultEntity kr WHERE kr.objective.cycle.id = :cycleId AND kr.status = :status")
long countByCycleIdAndStatus(
        @Param("cycleId") Long cycleId,
        @Param("status") KeyResultStatus status);

}