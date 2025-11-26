package com.paya.EncouragementService.dto.v2;

import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.entity.Attachment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GradeEncouragementDTOV2 {
    private UUID gradeEncouragementId;
    private LocalDateTime gradeEncouragementCreatedAt;
    private Integer gradeEncouragementNewGrade;
    private LocalDate gradeEncouragementEffectiveDate;
    private String gradeEncouragementNewGradeTitle;
    private UUID gradeEncouragementRelatedPersonnelId;
    private Integer gradeEncouragementSeniorityAmount;
    private PersonnelDTO personnel;
    private List<Attachment> files;

    public GradeEncouragementDTOV2(UUID gradeEncouragementId, LocalDateTime gradeEncouragementCreatedAt, Integer gradeEncouragementNewGrade, LocalDate gradeEncouragementEffectiveDate , UUID gradeEncouragementRelatedPersonnelId, Integer gradeEncouragementSeniorityAmount) {
        this.gradeEncouragementId = gradeEncouragementId;
        this.gradeEncouragementCreatedAt = gradeEncouragementCreatedAt;
        this.gradeEncouragementNewGrade = gradeEncouragementNewGrade;
        this.gradeEncouragementRelatedPersonnelId = gradeEncouragementRelatedPersonnelId;
        this.gradeEncouragementEffectiveDate = gradeEncouragementEffectiveDate;
        this.gradeEncouragementSeniorityAmount= gradeEncouragementSeniorityAmount;
    }
}
