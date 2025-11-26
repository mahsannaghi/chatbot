package com.paya.EncouragementService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QuranicSeniorityDTO {
    private UUID quranicSeniorityId;
    private String quranicSeniorityType;
    private String quranicSeniorityAmount;
    private Integer quranicSeniorityMaxAmount;
    private Boolean quranicSeniorityIsActive;


}
