package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_grade_encouragement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeEncouragement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "grade_encouragement_id", columnDefinition = "uniqueidentifier")
    private UUID gradeEncouragementId;

    @Column(name = "grade_encouragement_new_grade")
    private Integer gradeEncouragementNewGrade;

    @Column(name = "grade_encouragement_registrar_personnel_id", columnDefinition = "uniqueidentifier")
    private UUID gradeEncouragementRegistrarPersonnelId;


    @Column(name = "grade_encouragement_related_personnel_id", columnDefinition = "uniqueidentifier")
    private UUID gradeEncouragementRelatedPersonnelId;

    @Column(name = "grade_encouragement_created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime gradeEncouragementCreatedAt;

    @Column(name = "grade_encouragement_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime gradeEncouragementUpdatedAt;

    @Column(name = "grade_encouragement_effective_date")
    private LocalDate gradeEncouragementEffectiveDate;
    @Column(name = "grade_encouragement_seniority_amount")
    private Integer gradeEncouragementSeniorityAmount;
}
