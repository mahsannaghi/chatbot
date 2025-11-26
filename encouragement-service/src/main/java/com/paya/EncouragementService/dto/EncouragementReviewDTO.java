package com.paya.EncouragementService.dto;

import com.paya.EncouragementService.entity.Attachment;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Data
public class EncouragementReviewDTO {
    private UUID encouragementReviewId; // nullable for new records
    @NotNull(message = "Encouragement ID is required")
    private UUID encouragementReviewEncouragementId;
//    @NotNull(message = "Registrar Personnel ID is required")
//    private UUID encouragementReviewRegistrarPersonnelId;

    @NotNull(message = "Registrar Personnel ID is required")
    private String encouragementReviewRegistrarOrganizationId;
    @NotNull(message = "Paying Authority ID is required")
    private UUID encouragementReviewPayingAuthorityId;
    @NotNull(message = "Encouragement Type ID is required")
    private UUID encouragementReviewEncouragementTypeId;
    private UUID encouragementReviewTypeId;
    private PersonnelDTO encouragementReviewRegistrarPersonnelDTO;
    private PersonnelDTO encouragementReviewEncouragementPersonnelDTO;
//    private EncouragementDTO encouragementDTO;
    private String encouragementNumber;

    @NotNull(message = "Review Result is required")
    private Integer encouragementReviewResult;
    private String encouragementReviewDescription;
    @NotNull(message = "Created At is required")
    private LocalDateTime encouragementReviewCreatedAt;
    private Long encouragementReviewAmount;
    private Integer encouragementReviewAmountType;
    private Integer encouragementReviewPercentage;
    @NotNull(message = "Review Type is required")
    private Integer encouragementReviewType;
    private LocalDateTime encouragementReviewUpdatedAt;
    private LocalDate encouragementReviewSentDraftDate;
    private String encouragementReviewOrganizationId;
    private Integer encouragementReviewDraft;
    private LocalDate encouragementReviewAppliedDate;

    private UUID encouragementReviewEncouragementReasonTypeId;
    private String encouragementReviewEncouragementReasonTitle;
    private UUID encouragementReviewEncouragementReasonId;
    private String encouragementReviewEncouragementTypeTitle;
    private Long encouragementReviewEncouragementAmount;
    private String encouragementReviewEncouragementDescription;
    private LocalDate encouragementReviewEncouragementAppliedDate;
    private LocalDateTime encouragementReviewEncouragementCreatedDate;
    private Integer encouragementReviewEncouragementAmountType;

    private List<Attachment> attachmentList;
    private Boolean isEncouragementReviewSeen;

    public EncouragementReviewDTO(UUID encouragementReviewId, String encouragementReviewRegistrarOrganizationId, UUID encouragementReviewPayingAuthorityId, UUID encouragementReviewEncouragementTypeId, Integer encouragementReviewResult, String encouragementReviewDescription, Long encouragementReviewAmount, Integer encouragementReviewAmountType, Integer encouragementReviewPercentage, Integer encouragementReviewType, LocalDate encouragementReviewSentDraftDate, String encouragementReviewOrganizationId, LocalDate encouragementReviewAppliedDate, Integer encouragementReviewDraft
    , UUID encouragementReviewEncouragementReasonTypeId, String encouragementReviewEncouragementReasonTitle, UUID encouragementReviewEncouragementReasonId, String encouragementReviewEncouragementTypeTitle, UUID encouragementReviewTypeId,
                                  Long encouragementReviewEncouragementAmount, String encouragementReviewEncouragementDescription, LocalDate encouragementReviewEncouragementAppliedDate, LocalDateTime encouragementReviewEncouragementCreatedDate, Integer encouragementReviewEncouragementAmountType, Boolean isEncouragementReviewSeen) {
        this.encouragementReviewId = encouragementReviewId;
        this.encouragementReviewRegistrarOrganizationId = encouragementReviewRegistrarOrganizationId;
        this.encouragementReviewPayingAuthorityId = encouragementReviewPayingAuthorityId;
        this.encouragementReviewEncouragementTypeId = encouragementReviewEncouragementTypeId;
        this.encouragementReviewResult = encouragementReviewResult;
        this.encouragementReviewDescription = encouragementReviewDescription;
        this.encouragementReviewAmount = encouragementReviewAmount;
        this.encouragementReviewAmountType = encouragementReviewAmountType;
        this.encouragementReviewPercentage = encouragementReviewPercentage;
        this.encouragementReviewType = encouragementReviewType;
        this.encouragementReviewSentDraftDate = encouragementReviewSentDraftDate;
        this.encouragementReviewOrganizationId = encouragementReviewOrganizationId;
        this.encouragementReviewAppliedDate = encouragementReviewAppliedDate;
        this.encouragementReviewDraft = encouragementReviewDraft;
        this.encouragementReviewEncouragementReasonTypeId = encouragementReviewEncouragementReasonTypeId;
        this.encouragementReviewEncouragementReasonTitle = encouragementReviewEncouragementReasonTitle;
        this.encouragementReviewEncouragementReasonId = encouragementReviewEncouragementReasonId;
        this.encouragementReviewEncouragementTypeTitle = encouragementReviewEncouragementTypeTitle;
        this.encouragementReviewTypeId = encouragementReviewTypeId;
        this.encouragementReviewEncouragementAmount = encouragementReviewEncouragementAmount;
        this.encouragementReviewEncouragementDescription= encouragementReviewEncouragementDescription;
        this.encouragementReviewEncouragementAppliedDate= encouragementReviewEncouragementAppliedDate;
        this.encouragementReviewEncouragementCreatedDate= encouragementReviewEncouragementCreatedDate;
        this.encouragementReviewEncouragementAmountType= encouragementReviewEncouragementAmountType;
        this.isEncouragementReviewSeen= isEncouragementReviewSeen;
    }



}
