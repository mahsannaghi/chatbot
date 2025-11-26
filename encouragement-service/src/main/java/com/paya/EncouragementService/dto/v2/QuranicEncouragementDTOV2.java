package com.paya.EncouragementService.dto.v2;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paya.EncouragementService.dto.BasePersonnelDTO;
import com.paya.EncouragementService.entity.Attachment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class QuranicEncouragementDTOV2 {
    private UUID quranicEncouragementId;
    private LocalDateTime quranicEncouragementCreatedAt;
    private String quranicSeniorityType;
    private String quranicSeniorityAmount;
    private UUID quranicSeniorityId;
    private Integer quranicEncouragementAmount;
    private LocalDate quranicEncouragementEffectiveDate;
    @JsonIgnore
    private UUID quranicPersonnelId;
    private String quranicPersonnelOrganizationId;
    private BasePersonnelDTO personnel;
    private List<Attachment> files;

    public QuranicEncouragementDTOV2(UUID quranicEncouragementId, String quranicSeniorityType, String quranicSeniorityAmount, Integer quranicEncouragementAmount, LocalDateTime quranicEncouragementCreatedAt,LocalDate quranicEncouragementEffectiveDate, UUID quranicPersonnelId,UUID quranicSeniorityId) {
        this.quranicEncouragementId = quranicEncouragementId;
        this.quranicSeniorityType = quranicSeniorityType;
        this.quranicSeniorityAmount = quranicSeniorityAmount;
        this.quranicEncouragementAmount = quranicEncouragementAmount;
        this.quranicEncouragementCreatedAt = quranicEncouragementCreatedAt;
        this.quranicEncouragementEffectiveDate = quranicEncouragementEffectiveDate;
        this.quranicPersonnelId = quranicPersonnelId;
        this.quranicSeniorityId = quranicSeniorityId;
    }
}
