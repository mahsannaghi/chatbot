package com.paya.EncouragementService.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EncouragementDTO {
    private UUID encouragementId;
    private PersonnelDTO encouragementPersonnelDTO;
//    private List<UUID> encouragementRelatedPersonnelId;
    private PersonnelDTO encouragementRegistrarPersonnelDTO;
//    private UUID encouragementRegistrarPersonnelId;
    private PersonnelDTO encouragementApproverPersonnelDTO;
//    private UUID encouragementApproverPersonnelId;
    private UUID encouragementReasonTypeId;
    private String encouragementRegistrarOrganizationId;
    private EncouragementReasonTypeDetailDTO reasonTypeDetails;
    private String encouragementNumber;
    private Long encouragementAmount;
    private Integer encouragementAmountType;
    private String encouragementDescription;
    private Integer encouragementStatus;
    private Integer encouragementDraft;
    private String encouragementApprovedAt;
    private LocalDateTime encouragementCreatedAt;
    private LocalDate encouragementAppliedDate;
    private LocalDate encouragementSentDraftDate;
    private UUID encouragementReasonId;
    private UUID encouragementTypeId;
    private String encouragementReasonTitle;
    private String encouragementTypeTitle;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private String durationType;
    private LocalDate encouragementEffectiveDate;
}

