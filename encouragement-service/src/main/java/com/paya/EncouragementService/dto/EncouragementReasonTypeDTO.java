package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementReasonTypeDTO {
    private UUID reasonTypeId;
    private UUID reasonId;
    private UUID typeId;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private String durationType;
    private boolean isActive;



}

