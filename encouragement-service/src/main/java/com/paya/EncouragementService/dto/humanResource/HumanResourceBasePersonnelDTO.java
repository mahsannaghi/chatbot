package com.paya.EncouragementService.dto.humanResource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class HumanResourceBasePersonnelDTO {
    private String id;
    private String name;
    private String lastname;
    private String shenaseh;
    private String job;
    private String img;
    private String candiMellicode;
    private String personId;
    private String organCandidate;
    private Integer membership;
    private Integer rankTypeCode;
    private String personnelOrganizationID;

    private String personnelLogin;
    private String firstName;
    private String unitName;
    private String unitCode;
}