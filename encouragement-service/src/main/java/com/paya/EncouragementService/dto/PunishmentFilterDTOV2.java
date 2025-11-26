package com.paya.EncouragementService.dto;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class PunishmentFilterDTOV2 {
    private UUID punishmentId;
    private String punishmentNumber;
    private String punishmentType;
    private String punishmentReason;
    private Long punishmentAmount;
    private String punishmentRegistrarOrPunishedOrganizationId;
    private String punishmentRegistrarOrganizationId;
    private List<String> punishmentRegistrarOrganizationIdList;
    private List<String> punishmentPersonnelOrganizationIdList;
    private String punishmentPersonnelOrganizationId;
    private String punishedPersonFirstName;
    private String punishedPersonLastName;
    private String registrarPersonFirstName;
    private String registrarPersonLastName;
    private String punishedPersonUnitCode;
    private String punishedPersonRankTypePersianName;
    private Integer punishedPersonRankTypeCivilianCode;
    private Integer punishmentStatus;
    private Integer punishmentAmountType;
    private Integer punishmentStatusNot;
    private String punishmentDescription;
    private UUID punishmentReasonTypeId;
    private String punishmentManagersConcatenatedDescription;
    private LocalDate punishmentAppliedDate;
    private LocalDate punishmentSentDraftDate;
    private LocalDate punishmentCreatedAtTo;
    private LocalDate punishmentCreatedAtFrom;
    private LocalDate punishmentAppliedAtTo;
    private LocalDate punishmentAppliedAtFrom;
    private LocalDate punishmentCreatedAt;
    private Integer punishmentDraft;
    private LocalDate createdAt;

}
