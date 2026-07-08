package com.example.PeakPerform.repository;

import com.example.PeakPerform.entity.CheckInEntity;
import com.example.PeakPerform.enums.ReviewStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckInEntity, Long> {

    // ---------------- BASIC ----------------

    Page<CheckInEntity> findByKeyResultId(Long keyResultId, Pageable pageable);

    Page<CheckInEntity> findBySubmittedByIdOrderBySubmittedAtDesc(Long userId, Pageable pageable);

    List<CheckInEntity> findByReviewStatusOrderBySubmittedAtAsc(ReviewStatus status);

    // ---------------- TEAM LEAD VIEW ----------------

    @Query("""
        SELECT c FROM CheckInEntity c
        WHERE c.keyResult.objective.teamLead.id = :userId
        AND c.reviewStatus = :status
        ORDER BY c.submittedAt ASC
    """)
    List<CheckInEntity> findByKeyResultObjectiveTeamLeadIdAndReviewStatusOrderBySubmittedAtAsc(
            @Param("userId") Long userId,
            @Param("status") ReviewStatus status
    );

    // ---------------- DUPLICATE CHECK ----------------

    boolean existsByKeyResultIdAndReviewStatusAndSubmittedById(
            Long keyResultId,
            ReviewStatus status,
            Long submittedById
    );

    @Query("""
SELECT COUNT(c)
FROM CheckInEntity c
WHERE c.keyResult.objective.cycle.id = :cycleId
AND c.reviewStatus = :status
""")
long countByCycleIdAndStatus(
        @Param("cycleId") Long cycleId,
        @Param("status") ReviewStatus status
);
}