package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.EncouragementReview;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EncouragementReviewRepository extends JpaRepository<EncouragementReview, UUID>, JpaSpecificationExecutor<EncouragementReview> {
    Optional<EncouragementReview> findAllByEncouragementReviewTypeAndEncouragementReviewEncouragementIdAndEncouragementReviewResult(Integer encouragementReviewType, UUID encouragementId, Integer reviewResultEnum);
    List<EncouragementReview> findAllByEncouragementReviewEncouragementIdOrderByEncouragementReviewUpdatedAtAsc(UUID encouragementId);
    List<EncouragementReview> findAllByEncouragementReviewRegistrarOrganizationIdAndEncouragementReviewEncouragementIdAndEncouragementReviewTypeOrderByEncouragementReviewCreatedAtDesc(String encouragementRegistrarOrganizationId, UUID encouragementId, Integer reviewType);
    Optional<EncouragementReview> findByEncouragementReviewRegistrarOrganizationIdAndEncouragementReviewEncouragementIdAndEncouragementReviewResultAndEncouragementReviewType(String encouragementRegistrarOrganizationId, UUID encouragementId, Integer reviewResultEnum, Integer reviewType);

    Optional<EncouragementReview> findByEncouragementReviewRegistrarOrganizationIdAndEncouragementReviewEncouragementId(String encouragementRegistrarOrganizationId, UUID encouragementId);

    @Modifying
    @Transactional
    @Query("UPDATE EncouragementReview e " +
            "SET e.isEncouragementReviewSeen = TRUE " +
            "WHERE e.encouragementReviewId IN :idList")
    void markAsSeenWithoutUpdatingTimestamp(@Param("idList") List<UUID> idList);
}
