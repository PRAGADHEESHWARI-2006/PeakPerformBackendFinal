package com.example.PeakPerform.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PeakPerform.entity.ObjectiveEntity;
import com.example.PeakPerform.enums.ObjectiveStatus;

import jakarta.transaction.Transactional;

@Repository
public interface ObjectiveRepository extends JpaRepository<ObjectiveEntity, Long> {

    Page<ObjectiveEntity> findByOwnerId(Long ownerId, Pageable pageable);

    Page<ObjectiveEntity> findByCycleId(Long cycleId, Pageable pageable);

    Page<ObjectiveEntity> findByOwnerIdAndCycleId(Long ownerId,
            Long cycleId,
            Pageable pageable);

    List<ObjectiveEntity> findByCycleId(Long cycleId);

    List<ObjectiveEntity> findByTeamLeadId(Long teamLeadId);

    List<ObjectiveEntity> findByCycleIdAndStatus(Long cycleId,
            ObjectiveStatus status);

    @Query("SELECT o FROM ObjectiveEntity o WHERE o.cycle.id = :cycleId AND o.owner.department = :department")
    List<ObjectiveEntity> findByCycleIdAndOwnerDepartment(
            @Param("cycleId") Long cycleId,
            @Param("department") String department);
    
    @Transactional
    @Modifying
    @Query("UPDATE ObjectiveEntity o SET o.status = :status WHERE o.cycle.id = :cycleId AND o.status NOT IN ('COMPLETED','CANCELLED')")
    int bulkUpdateStatusByCycleId(
            @Param("cycleId") Long cycleId,
            @Param("status") ObjectiveStatus status);

    

    @Query("SELECT AVG(o.progressPercent) FROM ObjectiveEntity o WHERE o.cycle.id = :cycleId")
    Double averageProgressByCycleId(
            @Param("cycleId") Long cycleId);


    @Query("SELECT COUNT(o) FROM ObjectiveEntity o WHERE o.cycle.id = :cycleId")
long countByCycleId(@Param("cycleId") Long cycleId);

@Query("SELECT COUNT(o) FROM ObjectiveEntity o WHERE o.cycle.id = :cycleId AND o.status = :status")
long countByCycleIdAndStatus(@Param("cycleId") Long cycleId, @Param("status") ObjectiveStatus status);
            
}