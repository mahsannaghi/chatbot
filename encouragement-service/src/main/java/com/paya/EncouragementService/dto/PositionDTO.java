package com.paya.EncouragementService.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDTO {
    private UUID positionid;
    private UUID rankLevelDataId;
    private Integer positionTypeCode;
    private String rankLevelDataDegree;
    private int rankLevelDataFieldNumber;
    private String rankLevelDataJobLevel;
    private String rankLevelDataRank;
}