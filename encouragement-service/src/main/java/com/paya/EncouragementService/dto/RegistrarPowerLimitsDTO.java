package com.paya.EncouragementService.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegistrarPowerLimitsDTO {
    private UUID id;
    private UUID personnelGroupId;
    private String personnelGroupName;
    private List<PowerTypesDTO> types;


}

