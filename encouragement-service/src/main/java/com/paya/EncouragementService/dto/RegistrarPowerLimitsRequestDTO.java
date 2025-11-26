package com.paya.EncouragementService.dto;


import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.UUID;

@Data
@FieldNameConstants
public class RegistrarPowerLimitsRequestDTO {
    private UUID personnelGroupId;
    private List<UUID> personnelGroupIdList;
    private String typeTitle;
    private List<String> typeTitleList;
    private List<PowerTypeDTO> typesToAddOrUpdate;
    private List<UUID> deleteTheseTypes;
    private Integer pageSize;
    private Integer pageNumber;
}
