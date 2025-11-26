package com.paya.EncouragementService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class QuranicEncouragementInsertDTO {
    private UUID quranicEncouragementId;
    private UUID quranicSeniorityId;
    @JsonIgnore
    private UUID registrarPersonnelId;
    private UUID relatedPersonnelId;
    private Integer amount;
    private LocalDate quranicEncouragementEffectiveDate;

}
