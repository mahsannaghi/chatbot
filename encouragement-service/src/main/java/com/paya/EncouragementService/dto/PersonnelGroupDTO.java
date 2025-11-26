package com.paya.EncouragementService.dto;

// package com.paya.EncouragementService.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@FieldNameConstants
@Builder
public class PersonnelGroupDTO {
    private UUID personnelGroupId;
    private String personnelGroupName;
    private Boolean personnelGroupActive;
    private List<? extends BasePersonnelDTO> personnelGroupList;
    private List<String> personnelGroupOrgIdList;
    private List<String> personnelGroupOrgIdListToDelete;
    private LocalDateTime personnelGroupUpdatedAt;
    private LocalDateTime personnelGroupCreatedAt;
}
