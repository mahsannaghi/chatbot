package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_registrar_power_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPowerLimits extends TblBase{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "registrar_power_limits_id",  nullable = false, columnDefinition = "uniqueidentifier" , unique = true)
    private UUID id;

    @Column(name = "registrar_power_limits_encouragement_type_id", nullable = false)
    private UUID encouragementTypeId;

    @Column(name = "registrar_power_limits_rank_type_code")
    private Integer rankTypeCode;

    @ManyToOne
    @JoinColumn(name = "personnel_group_id")
    private PersonnelGroup personnelGroup;

    @Column(name = "registrar_power_limits_max_amount")
    private BigDecimal maxAmount;

    @Column(name = "registrar_power_limits_max_duration")
    private Integer maxDuration;

    @Column(name = "registrar_power_limits_duration_type")
    private Integer durationType;

}


