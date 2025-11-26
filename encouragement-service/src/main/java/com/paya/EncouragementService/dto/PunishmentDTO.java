package com.paya.EncouragementService.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PunishmentDTO {

    private UUID punishmentId;
    private List<String> punishmentRelatedPersonnelIds;
    private String punishmentRegistrarPersonnelId;
    private UUID punishmentApproverPersonnelId;
    private UUID punishmentReasonTypeId;

//    private PunishmentReasonTypeDetailDTO reasonTypeDetails;

    private String punishmentNumber;
    private BigDecimal punishmentAmount;
    private Integer punishmentAmountType;
    private String punishmentDescription;
    private Integer punishmentStatus;
    private LocalDate punishmentApprovedAt;

    private Boolean punishmentHasProtest;
    private String punishmentProtestDescription;
    private LocalDateTime punishmentNotifiedAt;
    private LocalDateTime punishmentCreatedAt;

    private Integer punishmentTypeQuadType;


    private UUID punishmentReasonId;
    private UUID punishmentTypeId;
    private String punishmentReasonTitle;
    private String punishmentTypeTitle;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private Integer durationType;
//    private PunishmentPersonnelDTO punishmentPersonnelDTO;
    private String registrarOrganizationId;
    private String registrarPersonFirstName;
    private String registrarPersonLastName;
    private String punishedPersonFirstName;
    private String punishedPersonLastName;
    private String punishedPersonOrganizationId;

}


