package com.paya.EncouragementService.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EncouragementFlowDetailDTO {
    private String managerFirstName;
    private String managerLastName;
    private Integer managerStatus;
    private Integer managerReviewType;
    private LocalDate managerSentDraftDate;
    private Integer managerDraft;
    private String managerDescription;
    private UUID encouragementReviewId;
    private String encouragementReviewRegistrarOrganizationId;
    private String encouragementReviewTypeTitle;
    private String encouragementReviewReasonTitle;
    private Long encouragementReviewEncouragementAmount;
}