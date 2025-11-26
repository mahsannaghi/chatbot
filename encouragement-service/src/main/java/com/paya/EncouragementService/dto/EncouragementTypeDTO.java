package com.paya.EncouragementService.dto;

import com.paya.EncouragementService.enumeration.TypeCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementTypeDTO {

    private UUID encouragementTypeId; // Mapped from encouragementTypeId
    private String encouragementTypeTitle; // Mapped from encouragementTypeTitle
    private boolean encouragementTypeIsActive; // Mapped from encouragementTypeIsActive
    private int encouragementTypeNatureType; // Mapped from encouragementTypeNatureType
    private int encouragementTypeCategory; // Mapped from encouragementTypeNatureType

    public EncouragementTypeDTO(UUID encouragementTypeId, String encouragementTypeTitle, TypeCategoryEnum typeCategoryEnum) {
        this.encouragementTypeId = encouragementTypeId;
        this.encouragementTypeTitle = encouragementTypeTitle;
        this.encouragementTypeCategory= typeCategoryEnum.getCode();
    }
}


