package com.paya.EncouragementService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
@SuperBuilder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonnelDTO.class, name = "personnel"),
        @JsonSubTypes.Type(value = BasePersonnelDTO.class, name = "basePersonnel"),
        @JsonSubTypes.Type(value = PersonnelDTO.class, name = "personnelWithManager")
})
public class BasePersonnelDTO {
    private String personnelId;
    private String personnelFirstName;
    private String personnelLastName;
    private String personnelOrganizationID;
    private String personnelNationalCode;
    private Integer personnelAge;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> personnelIdList;
    private List<String> personnelOrganizationIdList;
    private List<String> personnelUnitCodeList;
    private List<String> personnelRankCodeList;
    private String personnelServiceUnit;
    private String consumerName;
    private String personnelRankTypePersianName;
    private String personnelUnitCode;
    private Integer personnelRankTypeCivilianCode;    //رتبه
    private String personnelDegreePersianName;
    private String personnelNextPromotionDate;
    private Integer personnelDegreeCode;
    @Value("${evaluation.typeOfBasePersonnelDTOSending}")
    private String typeOfBasePersonnelDTOSending;

    private String type= typeOfBasePersonnelDTOSending;
}