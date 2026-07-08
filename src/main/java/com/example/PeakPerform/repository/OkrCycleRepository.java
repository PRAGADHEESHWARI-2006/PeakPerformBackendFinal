package com.example.PeakPerform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.PeakPerform.entity.OkrCycleEntity;
import com.example.PeakPerform.enums.CycleStatus;

@Repository
public interface OkrCycleRepository extends JpaRepository<OkrCycleEntity, Long> {

    List<OkrCycleEntity> findByStatus(CycleStatus status);

    Optional<OkrCycleEntity> findFirstByStatus(CycleStatus status);

    boolean existsByStatus(CycleStatus status);

    @Query("SELECT c FROM OkrCycleEntity c ORDER BY c.startDate DESC")
    List<OkrCycleEntity> findAllOrderByStartDateDesc();

    @Query("SELECT c FROM OkrCycleEntity c WHERE c.createdBy.id = :userId ORDER BY c.createdAt DESC")
    List<OkrCycleEntity> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

}