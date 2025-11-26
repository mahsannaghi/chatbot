package com.paya.EncouragementService.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncouragementTypeRequestSpecial {

    private UUID typeId;
    private Long maxAmount;
    private Integer maxDuration;
    private String durationAmount;
}