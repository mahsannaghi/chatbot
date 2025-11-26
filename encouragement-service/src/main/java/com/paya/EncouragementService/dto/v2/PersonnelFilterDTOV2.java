package com.paya.EncouragementService.dto.v2;


import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelFilterDTOV2 {
    private String personnelFirstName;
    private String personnelLastName;
    private String personnelUnitCode;
    private String personnelOrganizationId;
    private String personnelRankType;
    private List<String> personnelRankCodeList;
    private String type;
}
