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
@Table(name = "tbl_quranic_encouragement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuranicEncouragement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "quranic_encouragement_id", nullable = false, columnDefinition = "uniqueidentifier" , unique = true)
    private UUID quranicEncouragementId;

    @Column(name = "quranic_encouragement_quranic_seniority_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID quranicSeniorityId;

    @Column(name = "quranic_encouragement_registrar_personnel_id", columnDefinition = "uniqueidentifier")
    private UUID registrarPersonnelId;

    @Column(name = "quranic_encouragement_related_personnel_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID relatedPersonnelId;


    @Column(name = "quranic_encouragement_amount")
    private Integer amount;


    @Column(name = "quranic_encouragement_created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;


    @Column(name = "quranic_encouragement_updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "quranic_encouragement_effective_date")
    private LocalDate quranicEncouragementEffectiveDate;

}
