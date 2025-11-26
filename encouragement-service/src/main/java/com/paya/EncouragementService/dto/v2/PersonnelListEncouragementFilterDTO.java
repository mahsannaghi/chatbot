package com.paya.EncouragementService.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class PersonnelListEncouragementFilterDTO {
    private String encouragementPersonnelOrganizationId;
    private String encouragedPersonFirstName;
    private String encouragedPersonLastName;

}
