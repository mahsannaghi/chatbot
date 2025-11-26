package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncouragementReasonSpecialDTO {
    private UUID reasonId;
    private String reasonTitle;
}
