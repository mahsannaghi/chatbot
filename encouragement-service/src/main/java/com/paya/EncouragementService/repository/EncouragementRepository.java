package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.Encouragement;
import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface EncouragementRepository extends JpaRepository<Encouragement, UUID>, JpaSpecificationExecutor<Encouragement> {


    boolean existsByEncouragementReasonTypeId(UUID reasonTypeId);

    List<Encouragement> findByEncouragementReasonTypeId(UUID reasonTypeId);
    Optional<Encouragement> findByEncouragementId(UUID uuid);

    @Query("SELECT MAX(CAST(e.encouragementNumber AS long)) FROM Encouragement e")
    Optional<Long> findMaxEncouragementNumber();

    @Query("SELECT e FROM Encouragement e WHERE e.encouragementStatus = 1 AND e.hasUserSeenEncouragement = FALSE AND e.encouragementPersonnelOrganizationId = :personnelOrganizationID")
    Page<Encouragement> returnThisPersonnelUnSeenEncouragement(@Param("personnelOrganizationID") String personnelOrganizationID, PageRequest pageRequest);

    @Transactional
    @Modifying
    @Query("UPDATE Encouragement e SET e.hasUserSeenEncouragement = TRUE WHERE e IN :encouragementList")
    void changeHasUserSeenEncouragementTrue(@Param("encouragementList") List<Encouragement> encouragementList);
//    Page<Encouragement> findByEncouragementRelatedPersonnelOrganizationId(List<String> orgIdList, Pageable pageable);
//    Page<Encouragement> findByEncouragementRelatedPersonnelOrganizationId(String orgId, Pageable pageable);



}


