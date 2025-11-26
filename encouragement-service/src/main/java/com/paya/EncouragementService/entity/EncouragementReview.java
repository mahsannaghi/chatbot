package com.paya.EncouragementService.entity;

import com.paya.EncouragementService.enumeration.DraftEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_encouragement_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class EncouragementReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID encouragementReviewId;

    @Column(name = "encouragement_review_encouragement_id")
    private UUID encouragementReviewEncouragementId;

//    @Column(name = "encouragement_review_registrar_personnel_id")
//    private UUID encouragementReviewRegistrarPersonnelId;

    @Column(name = "encouragement_review_registrar_organization_id")
    private String encouragementReviewRegistrarOrganizationId;
    @Column(name = "encouragement_review_previous_organization_id")
    private String encouragementReviewPreviousOrganizationId;

    @Column(name = "encouragement_review_paying_authority_id")
    private UUID encouragementReviewPayingAuthorityId;

    @Column(name = "encouragement_review_encouragement_type_id")
    private UUID encouragementReviewEncouragementTypeId;

    @Column(name = "encouragement_review_result")
    private Integer encouragementReviewResult;

    @Column(name = "encouragement_review_description")
    private String encouragementReviewDescription;

    @Column(name = "encouragement_review_created_at")
    @CreationTimestamp
    private LocalDateTime encouragementReviewCreatedAt;

    @Column(name = "encouragement_review_amount")
    private Long encouragementReviewAmount;

    @Column(name = "encouragement_review_amount_type")
    private Integer encouragementReviewAmountType;

    @Column(name = "encouragement_review_percentage")
    private Integer encouragementReviewPercentage;

    @Column(name = "encouragement_review_type")
    private Integer encouragementReviewType;

    @Column(name = "encouragement_review_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime encouragementReviewUpdatedAt;

    @Column(name = "encouragement_review_sent_draft_date")
//    @Temporal(TemporalType.TIMESTAMP)
    private LocalDate encouragementReviewSentDraftDate;

    @Column(name = "encouragement_review__applied_date")
    private LocalDate encouragementReviewAppliedDate;

    @Column(name = "encouragement_review_draft")
    @Enumerated(EnumType.ORDINAL)
    private DraftEnum encouragementReviewDraft;

    @Column(name = "encouragement_review_encouragement_reason_type_id")
    private UUID encouragementReviewEncouragementReasonTypeId;
    @Column(name = "encouragement_review_reason_type_id")
    private UUID encouragementReviewReasonTypeId;

    @Column(name = "encouragement_review_encouragement_amount")
    private Long encouragementReviewEncouragementAmount;

    @Column(name = "encouragement_review_encouragement_description")
    private String encouragementReviewEncouragementDescription;

    @Column(name = "encouragement_review_encouragement_applied_date")
    private LocalDate encouragementReviewEncouragementAppliedDate;
    @Column(name = "encouragement_review_encouragement_created_date")
    private LocalDateTime encouragementReviewEncouragementCreatedDate;
    @Column(name = "encouragement_review_encouragement_amount_type")
    private Integer encouragementReviewEncouragementAmountType;

    @Column(name = "is_encouragement_review_seen")
    private Boolean isEncouragementReviewSeen= Boolean.FALSE;
}
