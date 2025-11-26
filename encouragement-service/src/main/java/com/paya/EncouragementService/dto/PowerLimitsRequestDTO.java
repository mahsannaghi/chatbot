package com.paya.EncouragementService.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PowerLimitsRequestDTO {

    private UUID gradeId;
    private UUID positionId;
    private List<PowerTypeDTO> typesToAddOrUpdate;
    private List<UUID> deleteTheseTypes;
}