package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.dto.EncouragementReasonDTO;
import com.paya.EncouragementService.dto.EncouragementReasonTypeDetailDTO;
import com.paya.EncouragementService.dto.EncouragementTypeDTO;
import com.paya.EncouragementService.dto.v2.ReasonTypeDTOV2;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EncouragementReasonTypeRepository extends JpaRepository<EncouragementReasonType, UUID>, JpaSpecificationExecutor<EncouragementReasonType> {

    boolean existsByEncouragementReasonIdAndEncouragementTypeId(UUID reasonId, UUID typeId);

    boolean existsByEncouragementReasonIdAndEncouragementTypeIdAndEncouragementReasonTypeIdNot(
            UUID reasonId, UUID typeId, UUID reasonTypeId);

    Optional<EncouragementReasonType> findByEncouragementReasonIdAndEncouragementTypeId(UUID reasonId, UUID typeId);
    Optional<EncouragementReasonType> findByEncouragementReasonTypeId(UUID id);

    void deleteByEncouragementReasonIdAndEncouragementTypeId(UUID reasonId, UUID typeId);

    boolean existsByencouragementReasonId(UUID encouragementReasonId);

    boolean existsByencouragementTypeId(UUID encouragementTypeId);

    @Query("SELECT ert.encouragementReasonTypeId FROM EncouragementReasonType ert WHERE ert.encouragementTypeId = :encouragementTypeId")
    UUID findEncouragementReasonTypeIdByEncouragementTypeId(@Param("encouragementTypeId") UUID encouragementTypeId);

    Optional<EncouragementReasonType> findEncouragementReasonTypeByEncouragementReasonTypeId(UUID id);

    @Query("""
                SELECT new com.paya.EncouragementService.dto.EncouragementReasonTypeDetailDTO(
                    ert.encouragementReasonTypeId,
                    ert.encouragementReasonId,
                    ert.encouragementTypeId,
                    er.encouragementReasonTitle,
                    et.encouragementTypeTitle,
                    ert.maxAmount,
                    ert.maxDuration,
                    ert.durationType
                )
                FROM EncouragementReasonType ert
                LEFT JOIN EncouragementReason er ON ert.encouragementReasonId = er.encouragementReasonId
                LEFT JOIN EncouragementType et ON ert.encouragementTypeId = et.encouragementTypeId
                WHERE ert.encouragementReasonTypeId = :reasonTypeId
            """)
    Optional<EncouragementReasonTypeDetailDTO> findReasonTypeDetailsById(@Param("reasonTypeId") UUID reasonTypeId);

    // متد findByEncouragementTypeId به صورت صحیح با List
    List<EncouragementReasonType> findByEncouragementTypeId(UUID encouragementTypeId);

    @Query("select new com.paya.EncouragementService.dto.v2.ReasonTypeDTOV2(" +
            "ert.encouragementReasonTypeId , " +
            "ert.encouragementReasonId  " +
            ") " +
            "from EncouragementReasonType ert group by ert.encouragementReasonId order by count(ert.encouragementReasonId)")
    List<ReasonTypeDTOV2> getTypesGroupedByReason();


    @Query("select et.encouragementTypeTitle from EncouragementReasonType ert join EncouragementType et on ert.encouragementTypeId = et.encouragementTypeId where ert.encouragementReasonTypeId = :reasonTypeId ")
    String getTypeTitleWithReasonTypeId(UUID reasonTypeId);

    @Query("select et.encouragementTypeId from EncouragementReasonType ert join EncouragementType et on ert.encouragementTypeId = et.encouragementTypeId where ert.encouragementReasonTypeId = :reasonTypeId ")
    UUID getTypeIdTitleWithReasonTypeId(UUID reasonTypeId);
    @Query("select new com.paya.EncouragementService.dto.EncouragementReasonDTO(er.encouragementReasonId, er.encouragementReasonTitle) from EncouragementReasonType ert join EncouragementReason er on ert.encouragementReasonId= er.encouragementReasonId where ert.encouragementReasonTypeId = :reasonTypeId ")
    EncouragementReasonDTO getReasonWithReasonTypeId(UUID reasonTypeId);

    @Query("select new com.paya.EncouragementService.dto.EncouragementTypeDTO(et.encouragementTypeId, et.encouragementTypeTitle, et.encouragementTypeCategory) from EncouragementReasonType ert join EncouragementType et on ert.encouragementTypeId= et.encouragementTypeId where ert.encouragementReasonTypeId = :reasonTypeId ")
    EncouragementTypeDTO getTypeWithReasonTypeId(UUID reasonTypeId);
    @Query("select et.encouragementTypeCategory from EncouragementReasonType ert join EncouragementType et on ert.encouragementTypeId= et.encouragementTypeId where ert.encouragementReasonTypeId = :reasonTypeId ")
    Integer getEncouragementTypeByReasonTypeId(UUID reasonTypeId);


}
