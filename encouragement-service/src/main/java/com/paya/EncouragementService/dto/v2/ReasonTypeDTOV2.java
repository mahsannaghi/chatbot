package com.paya.EncouragementService.dto.v2;

import com.paya.EncouragementService.entity.EncouragementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReasonTypeDTOV2 {
    private UUID encouragementReasonTypeId;
    private UUID encouragementReasonId;
    //constructor ignore
    private String encouragementReasonTitle;

    //constructor ignore
    private List<EncouragementType> encouragementTypeList;

    public ReasonTypeDTOV2(UUID encouragementReasonTypeId, UUID encouragementReasonId) {
        this.encouragementReasonTypeId = encouragementReasonTypeId;
        this.encouragementReasonId = encouragementReasonId;

    }
}
