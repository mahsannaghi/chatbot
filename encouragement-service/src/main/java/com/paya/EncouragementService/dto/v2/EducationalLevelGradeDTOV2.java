package com.paya.EncouragementService.dto.v2;

import com.paya.EncouragementService.dto.EducationalLevelGradeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EducationalLevelGradeDTOV2 {
    private Integer maxAmount;
    private EducationalLevelGradeDTO educationalLevelGrade;

}
