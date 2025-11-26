package com.paya.EncouragementService.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GradeEncouragementDTO {
    private UUID gradeEncouragementId; // nullable for new records
    private Integer gradeEncouragementNewGrade;
    private PersonnelDTO gradeEncouragementRegistrarPersonnelId;
    private PersonnelDTO gradeEncouragementRelatedPersonnelId;
    private LocalDateTime gradeEncouragementCreatedAt;
    private LocalDateTime gradeEncouragementUpdatedAt;
    private LocalDate gradeEncouragementEffectiveDate;
}
