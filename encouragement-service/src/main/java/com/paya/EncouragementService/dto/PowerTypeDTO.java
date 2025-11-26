package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PowerTypeDTO {
    private UUID typeId;
    private String typeTitle;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private Integer durationType;
}