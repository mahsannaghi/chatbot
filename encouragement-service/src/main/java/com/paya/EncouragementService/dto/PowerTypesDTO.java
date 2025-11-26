package com.paya.EncouragementService.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PowerTypesDTO {
    private UUID typeId;
    private String typeTitle;
    private BigDecimal maxAmount;
    private Integer maxDuration;
    private Integer durationType;
}
