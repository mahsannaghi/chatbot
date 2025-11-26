package com.paya.EncouragementService.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TypeDTO {
    private UUID typeId;
    private UUID reasonTypeId;
    private String typeTitle;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private String durationType;
    private Boolean active;
}
