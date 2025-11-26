package com.paya.EncouragementService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class EncouragementCreateDTO {
    private UUID encouragementId;
    private List<String> encouragedPersonOrganizationId;
    private String encouragementRegistrarOrganizationId;
    private UUID encouragementReasonId;
    private UUID encouragementTypeId;
    private String encouragementDescription;
    private Long encouragementAmount;
    private Integer encouragementAmountType;
}
