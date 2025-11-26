package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDTO {
    private UUID gradeid;
    private Integer rankTypeCode;
    private UUID rankLevelDataId;
    private String rankLevelDataDegree;
    private int rankLevelDataFieldNumber;
    private String rankLevelDataJobLevel;
    private String rankLevelDataRank;
}


