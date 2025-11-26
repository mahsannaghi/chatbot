package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@FieldNameConstants
@Table(name = "tbl_encouragement_reason_type")
public class EncouragementReasonType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "encouragement_reason_type_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID encouragementReasonTypeId;

    @Column(name = "encouragement_reason_type_encouragement_reason_id", columnDefinition = "uniqueidentifier")
    private UUID encouragementReasonId;

    @Column(name = "encouragement_reason_type_encouragement_type_id", columnDefinition = "uniqueidentifier")
    private UUID encouragementTypeId;

    @Column(name = "encouragement_reason_type_max_amount", precision = 18, scale = 0)
    private BigDecimal maxAmount;

    @Column(name = "encouragement_reason_type_max_duration")
    private Integer maxDuration;

    @Column(name = "encouragement_reason_type_duration_type", length = 255)
    private String durationType;

    @Column(name = "encouragement_reason_type_is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "encouragement_reason_type_created_at")
    @CreationTimestamp
    private LocalDateTime encouragementReasonTypeCreatedAt;

    @Column(name = "encouragement_reason_type_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime encouragementReasonTypeUpdatedAt;
}
