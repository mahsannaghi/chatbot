package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tbl_encouragement_reason")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementReason {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID encouragementReasonId;

    @Column(name = "encouragement_reason_title", nullable = false)
    private String encouragementReasonTitle;

    @Column(name = "encouragement_reason_is_active", nullable = false)
    private Boolean encouragementReasonIsActive;





}
