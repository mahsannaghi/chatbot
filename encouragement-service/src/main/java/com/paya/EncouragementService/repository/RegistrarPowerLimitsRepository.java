package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.PersonnelGroup;
import com.paya.EncouragementService.entity.RegistrarPowerLimits;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrarPowerLimitsRepository extends JpaRepository<RegistrarPowerLimits, UUID>, JpaSpecificationExecutor<RegistrarPowerLimits> {

    void deleteByEncouragementTypeId(UUID encouragementTypeId);
    List<RegistrarPowerLimits> findByEncouragementTypeId(UUID encouragementTypeId);

    Optional<RegistrarPowerLimits> findByPersonnelGroupAndEncouragementTypeId(PersonnelGroup personnelGroup, UUID encouragementTypeId);
    List<RegistrarPowerLimits> findByRankTypeCode(Integer rankCode);
    Boolean existsRegistrarPowerLimitsByPersonnelGroup(PersonnelGroup personnelGroup);

    @Query("""
    SELECT rpl
    FROM RegistrarPowerLimits rpl
    WHERE (:typeTitle IS NULL 
           OR rpl.personnelGroup.personnelGroupId IN (
               SELECT DISTINCT rpl2.personnelGroup.personnelGroupId
               FROM RegistrarPowerLimits rpl2
               JOIN EncouragementType pt 
                 ON rpl2.encouragementTypeId = pt.encouragementTypeId
               WHERE LOWER(pt.encouragementTypeTitle) LIKE LOWER(CONCAT('%', :typeTitle, '%'))
           ))
      AND (:personnelGroupIdList IS NULL OR rpl.personnelGroup.personnelGroupId IN :personnelGroupIdList)
""")
    Page<RegistrarPowerLimits> findByFilter(
            @Param("typeTitle") @Nullable String typeTitle,
            @Param("personnelGroupIdList") @Nullable List<UUID> personnelGroupIdList,
            Pageable pageable);}
