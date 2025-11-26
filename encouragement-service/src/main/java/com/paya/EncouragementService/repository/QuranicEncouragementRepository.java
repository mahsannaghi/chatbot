package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.QuranicEncouragement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuranicEncouragementRepository extends JpaRepository<QuranicEncouragement, UUID>, JpaSpecificationExecutor<QuranicEncouragement> {
    Optional<QuranicEncouragement> findByQuranicSeniorityId(UUID uuid);
    List<QuranicEncouragement> findByRelatedPersonnelId(UUID organizationId);
}