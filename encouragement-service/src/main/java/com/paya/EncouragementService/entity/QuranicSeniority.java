package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_quranic_seniority")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuranicSeniority {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "quranic_seniority_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID quranicSeniorityId;

    @Column(name = "quranic_seniority_type", nullable = false, length = 50)
    private String quranicSeniorityType;

    @Column(name = "quranic_seniority_amount", nullable = false, length = 50)
    private String quranicSeniorityAmount;

    @Column(name = "quranic_seniority_max_amount", nullable = false)
    private Integer quranicSeniorityMaxAmount;

    @Column(name = "quranic_seniority_is_active", nullable = false)
    private Boolean quranicSeniorityIsActive = true;


}
