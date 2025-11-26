package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementCreateRequestSpecial {
    private UUID encouragementId;
//    private List<UUID> encouragementRelatedPersonnelIds;
    private List<String> encouragementRelatedPersonnelOrganizationIds;
    private UUID reasonId;
    private String description;
    private EncouragementTypeRequestSpecial type;
    private UUID encouragementRegistrarPersonnelId;
    private Long amount;
    private Integer amountType;
    private Integer status;
//    private Date approvedAt;
    private LocalDate encouragementSentDraftDate;
    private LocalDate encouragementEffectiveDate;
    private Integer encouragementDraft;
}