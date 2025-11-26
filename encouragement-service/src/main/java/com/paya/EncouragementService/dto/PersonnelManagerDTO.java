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
public class PersonnelManagerDTO {
    private String personnelManagerOrganizationIdLevel1;
    private String personnelManagerOrganizationIdLevel2;
    private String personnelManagerOrganizationIdLevel3;
    private String personnelManagerOrganizationIdLevel4;
    private String personnelManagerOrganizationIdLevel5;
    private String personnelManagerPositionLevel1;
    private String personnelManagerPositionLevel2;
    private String personnelManagerPositionLevel3;
    private String personnelManagerPositionLevel4;
    private String personnelManagerPositionLevel5;
    private List<String> personnelManagerPositionList;
    private List<String> personnelManagerOrganizationIdList;
}