package com.paya.EncouragementService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class GradeEncouragementInsertDTO {
    private UUID gradeEncouragementId;
    private Integer gradeEncouragementNewGrade;
    private Integer gradeEncouragementOldGrade;
    @JsonIgnore
    private UUID gradeEncouragementRegistrarPersonnelId;
    private UUID gradeEncouragementRelatedPersonnelId;
    private LocalDate gradeEncouragementEffectiveDate;
    private Integer gradeEncouragementSeniorityAmount;
}
