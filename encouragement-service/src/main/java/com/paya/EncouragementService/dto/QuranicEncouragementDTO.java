package com.paya.EncouragementService.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuranicEncouragementDTO {

    private UUID quranicEncouragementId;
    private UUID quranicSeniorityId;
    private PersonnelDTO registrarPersonnelId;
    private PersonnelDTO relatedPersonnelId;
    // private java.sql.Date createdAt;
    private Integer amount;
    //   private java.sql.Date updatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate quranicEncouragementEffectiveDate;
}