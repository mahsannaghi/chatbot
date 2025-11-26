package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_educational_level_grade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationalLevelGrade {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "educational_level_grade_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID educationalLevelGradeId;

    @Column(name = "educational_level_grade_degree", nullable = false)
    private Integer educationalLevelGradeDegree;

    //    @Column(name = "educational_level_grade_grade_id", nullable = false, columnDefinition = "uniqueidentifier")
    @Column(name = "educational_level_grade_rank_code", nullable = false)
    private Integer educationalLevelGradeRankTypeCode;

    @Column(name = "educational_level_grade_is_active", nullable = false)
    private Boolean educationalLevelGradeIsActive;

    @Column(name = "educational_level_grade_creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime educationalLevelGradeCreationDate;
}
