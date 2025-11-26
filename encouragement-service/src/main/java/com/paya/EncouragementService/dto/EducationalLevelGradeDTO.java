package com.paya.EncouragementService.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EducationalLevelGradeDTO {

    private UUID educationalLevelGradeId;
    private Integer educationalLevelGradeDegree;
//    private UUID educationalLevelGradeGradeId;
    private Boolean educationalLevelGradeIsActive;
//    private String rankLevelDataDegree;
//    private String rankLevelDataRank;
//    private UUID rankLevelDataId;
    private Integer educationalLevelGradeRankTypeCode;
    private String educationalLevelGradeRankTypePersianName;
    private Integer educationalLevelGradeRankTypeCivilian;

}

