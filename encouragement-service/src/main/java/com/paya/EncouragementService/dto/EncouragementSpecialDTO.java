package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncouragementSpecialDTO {

    private UUID id;
    private List<PersonSpecialDTO> personnels;
    private EncouragementTypeSpecialDTO type;
    private LocalDateTime encouragementCreatedAt;
    private Integer encouragementStatus;
    private String description;
    private EncouragementReasonSpecialDTO reason;
    private PersonSpecialDTO person;


    public void setPerson(PersonSpecialDTO person) {
        this.person = person;
    }
}
