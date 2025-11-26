package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementReasonDTO {

    private UUID encouragementReasonId;
    private String encouragementReasonTitle;
    private Boolean encouragementReasonIsActive;

    public EncouragementReasonDTO(UUID encouragementReasonId, String encouragementReasonTitle) {
        this.encouragementReasonId = encouragementReasonId;
        this.encouragementReasonTitle = encouragementReasonTitle;
    }
}
