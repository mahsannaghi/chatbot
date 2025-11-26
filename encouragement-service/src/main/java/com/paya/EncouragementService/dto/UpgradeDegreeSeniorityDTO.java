package com.paya.EncouragementService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpgradeDegreeSeniorityDTO {
    private UUID upgradeDegreeSeniorityId; // Nullable for new records
    private Integer upgradeDegreeSeniorityFromDegree;
    private Integer upgradeDegreeSeniorityToDegree;
    private Boolean upgradeDegreeSeniorityIsActive;
    private Integer upgradeDegreeSeniorityMaxAmount;
}
