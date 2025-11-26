package com.paya.EncouragementService.dto.v2;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GradeEncouragementFilterDTOV2 {
    private String fromDate;
    private String toDate;
    private Integer gradeEncouragementNewGrade;
    private String gradeEncouragementNewGradeTitle;
    private Integer gradeEncouragementSeniorityAmount;
    private PersonnelFilterDTOV2 personnel;
    private List<String> rankTypeCodeList;
}
