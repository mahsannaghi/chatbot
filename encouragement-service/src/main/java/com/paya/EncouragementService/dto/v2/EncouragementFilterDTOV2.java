package com.paya.EncouragementService.dto.v2;

import com.paya.EncouragementService.entity.Attachment;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class EncouragementFilterDTOV2 {
    private UUID encouragementId;
    private String encouragementNumber;
    private String encouragementType;
    private Integer encouragementTypeCategory;
    private UUID encouragementTypeId;
    private String encouragementReason;
    private UUID encouragementReasonId;
    private Long encouragementAmount;
//    private String encouragerOrganizationId;
//    private String encouragementRelatedPersonnelOrganizationId;
    private String encouragementRegistrarOrEncouragedOrganizationId;
    private String encouragementRegistrarOrganizationId;
    private List<String> encouragementRegistrarOrganizationIdList;
    private List<String> encouragementApproverOrganizationIdList;
    private List<String> encouragementPersonnelOrganizationIdList;
    private String encouragementPersonnelOrganizationId;
    private String encouragedPersonFirstName;
    private String encouragedPersonLastName;
    private String registrarPersonFirstName;
    private String registrarPersonLastName;
    private String encouragedPersonUnitCode;
    private String encouragedPersonRankTypePersianName;
    private Integer encouragedPersonRankTypeCivilianCode;
    private Integer encouragementStatus;
    private Integer encouragementAmountType;
    private List<Integer> encouragementStatusList;
    private Integer encouragementStatusNot;
    private String encouragementDescription;
    private UUID encouragementReasonTypeId;
    private String encouragementManagersConcatenatedDescription;
    private LocalDate encouragementAppliedDate;
    private String encouragementApproverOrganizationId;
    private Integer encouragementApproverType;
    private String approverPersonFirstName;
    private String approverPersonLastName;
    private LocalDate encouragementSentDraftDate;
    private LocalDate encouragementCreatedAtTo;
    private LocalDate encouragementCreatedAtFrom;
    private LocalDate encouragementAppliedAtTo;
    private LocalDate encouragementAppliedAtFrom;
    private LocalDateTime encouragementCreatedAt;
    private LocalDate encouragementEffectiveDate;
    private Integer encouragementDraft;
//    @JsonIgnore
//    private LocalDate fromDate;
//    @JsonIgnore
//    private LocalDateTime toDate;
    private LocalDateTime createdAt;
    private Boolean withPunishment= Boolean.FALSE;
    private Boolean allServiceUnitPersonnelEncouragement= Boolean.FALSE;
    private Boolean isEncouragementSeen;
    private List<Attachment> attachmentList;
    private Boolean withFile= Boolean.FALSE;
    private LocalDateTime updatedAt;
//    private List<PunishmentDTO> punishmentList;

}
