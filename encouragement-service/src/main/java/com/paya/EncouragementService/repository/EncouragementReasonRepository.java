package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EncouragementReasonRepository extends JpaRepository<EncouragementReason, UUID>, JpaSpecificationExecutor<EncouragementReason> {
    Optional<EncouragementReason> findByEncouragementReasonTitle(String reasonTitle);
    Optional<EncouragementReason> findByEncouragementReasonId(UUID reasonId);

    @Query("select er.encouragementReasonTitle from EncouragementReason er where er.encouragementReasonId = :id")
    String getTitleWithId(UUID id);
  

}

