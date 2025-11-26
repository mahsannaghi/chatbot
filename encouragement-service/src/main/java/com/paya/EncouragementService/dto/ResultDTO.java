package com.paya.EncouragementService.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultDTO {
    private ReasonDTO reason;
    private List<TypeDTO> types;
    private boolean isActive;

}
