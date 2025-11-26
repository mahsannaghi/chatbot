package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementReviewSearchDTO {

    private UUID encouragementReviewEncouragementId;
    private UUID encouragementReviewRegistrarPersonnelId;
    private String encouragementReviewRegistrarPersonnelOrganizationId;
    private String encouragementReviewEncouragedOrganizationId;
    private List<String> encouragementReviewEncouragedOrganizationIdList;
    private String encouragementReviewEncouragedRegistrarOrganizationId;
    private List<String> encouragementReviewEncouragedRegistrarOrganizationIdList;
    private String encouragementReviewEncouragedLastName;
    private String encouragementReviewEncouragedRegistrarLastName;
    private UUID encouragementReviewPayingAuthorityId;
    private UUID encouragementReviewEncouragementTypeId;
    private Integer encouragementReviewResult;
    private Integer encouragementReviewDraftNotSent;
    private List<Integer> encouragementReviewResultList;
    private List<Integer> encouragementReviewTypeList;
    private String encouragementReviewDescription;
    private Date encouragementReviewCreatedAt;
    private Double encouragementReviewAmount;
    private Integer encouragementReviewAmountType;
    private Integer encouragementReviewPercentage;
    private Integer encouragementReviewType;
    private Date encouragementReviewUpdatedAt;
    private Date encouragementReviewApprovalDate;
    private Integer encouragementStatusNot;
    private String encouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId;
    private String encouragementNumber;
    private String encouragementTypeTitle;
    private String encouragementReasonTitle;
    private Long encouragementAmount;
    private LocalDate encouragementReviewSentDraftDateFrom;
    private LocalDate encouragementReviewSentDraftDateTo;
}
