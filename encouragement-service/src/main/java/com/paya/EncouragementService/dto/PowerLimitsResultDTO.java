package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PowerLimitsResultDTO {
    private GradeDTO grade;
    private PositionDTO position;
    private List<PowerTypeDTO> types;
}