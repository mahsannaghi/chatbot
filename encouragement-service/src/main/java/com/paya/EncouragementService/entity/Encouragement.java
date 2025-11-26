package com.paya.EncouragementService.entity;

import com.paya.EncouragementService.enumeration.DraftEnum;
import com.paya.EncouragementService.enumeration.TypeCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_encouragement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Encouragement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID encouragementId;

    @Column(name = "encouragement_personnel_organization_id")
    private String encouragementPersonnelOrganizationId;

    @Column(name = "encouragement_registrar_organization_id")
    private String encouragementRegistrarOrganizationId;

    @Column(name = "encouragement_approver_organization_id")
    private String encouragementApproverOrganizationId;

    @Column(name = "encouragement_approver_type")
    private Integer encouragementApproverType;

    @Column(name = "encouragement_reason_type_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID encouragementReasonTypeId;

    @Column(name = "encouragement_number", nullable = false, length = 15)
    private String encouragementNumber;

    @Column(name = "encouragement_created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime encouragementCreatedAt;

    @Column(name = "encouragement_amount")
    private Long encouragementAmount;

    @Column(name = "encouragement_amount_type")
    private Integer encouragementAmountType;

    @Column(name = "encouragement_description")
    private String encouragementDescription;
    @Column(name = "encouragement_managers_concatenated_description", length = 5000)
    private String encouragementManagersConcatenatedDescription;

    @Column(name = "encouragement_status")
    private Integer encouragementStatus;

    @Column(name = "encouragement_applied_date")
    private LocalDate encouragementAppliedDate;

    @Column(name = "encouragement_sent_draft_date")
    private LocalDate encouragementSentDraftDate;

    @Column(name = "encouragement_draft")
    @Enumerated(EnumType.ORDINAL)
    private DraftEnum encouragementDraft;

    @Column(name = "encouragement_effective_date")
    private LocalDate encouragementEffectiveDate;

    @Column(name = "is_encouragement_seen")
    private Boolean isEncouragementSeen= Boolean.FALSE;

    @Column(name = "has_user_seen_encouragement")
    private Boolean hasUserSeenEncouragement= Boolean.FALSE;

    @Column(name = "encouragement_type_category", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TypeCategoryEnum encouragementTypeCategory = TypeCategoryEnum.NORMAL;
}
