package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_upgrade_degree_seniority")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class UpgradeDegreeSeniority extends TblBase{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "upgrade_degree_seniority_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID upgradeDegreeSeniorityId;

    @Column(name = "upgrade_degree_seniority_from_degree", nullable = false)
    private Integer upgradeDegreeSeniorityFromDegree;

    @Column(name = "upgrade_degree_seniority_to_degree", nullable = false)
    private Integer upgradeDegreeSeniorityToDegree;

    @Column(name = "upgrade_degree_seniority_is_active", nullable = false)
    private Boolean upgradeDegreeSeniorityIsActive;

    @Column(name = "upgrade_degree_seniority_max_amount", nullable = false)
    private Integer upgradeDegreeSeniorityMaxAmount;
}
