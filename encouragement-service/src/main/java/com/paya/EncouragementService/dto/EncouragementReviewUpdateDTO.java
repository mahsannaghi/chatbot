package com.paya.EncouragementService.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EncouragementReviewUpdateDTO {
    private UUID encouragementReviewId;
    private UUID encouragementId;
    private Integer encouragementReviewResult;
    private Integer encouragementReviewDraft;
    private Long encouragementAmount;
    private Integer encouragementAmountType;
    private UUID encouragementReasonTypeId;
    private UUID encouragementTypeId;
    private String encouragementReviewDescription;
    private LocalDateTime encouragementReviewSentDraftDate;
    private LocalDateTime encouragementReviewCreatedAt;
    private LocalDate encouragementReviewAppliedDate;

}
