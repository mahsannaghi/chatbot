package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.EncouragementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EncouragementTypeRepository extends JpaRepository<EncouragementType, UUID>, JpaSpecificationExecutor<EncouragementType> {
    Optional<EncouragementType> findByEncouragementTypeTitle(String title);

    List<EncouragementType> findAll();

    List<EncouragementType> findByEncouragementTypeIdIn(List<UUID> encouragementTypeIds);

    @Query("select et from EncouragementReasonType ert join EncouragementType et on ert.encouragementTypeId = et.encouragementTypeId where ert.encouragementTypeId = :id")
    List<EncouragementType> getAllExitInEncouragementReasonType(UUID id);

    @Query("select et.encouragementTypeId from EncouragementReasonType ert join  EncouragementType et on ert.encouragementTypeId = et.encouragementTypeId where ert.encouragementReasonTypeId =:reasonTypeId ")
    UUID findIdByReasonTypeId(UUID reasonTypeId);


}



