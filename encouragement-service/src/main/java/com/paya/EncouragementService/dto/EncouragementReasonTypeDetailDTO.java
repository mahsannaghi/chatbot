package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncouragementReasonTypeDetailDTO {
    private UUID encouragementReasonTypeId;
    private UUID encouragementReasonId;
    private UUID encouragementTypeId;
    private String encouragementReasonTitle;
    private String encouragementTypeTitle;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private String durationType;


}


