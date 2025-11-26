package com.paya.EncouragementService.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonnelDTO extends BasePersonnelDTO implements Serializable  {
    private String personnelJobFamilyName;
    private String personnelJobFamilyCode;
    private String personnelContractType;
    private Double personnelAverage;
    private String personnelBirthCertificateNumber;
    private String personnelBirthDate;
    private String personnelBirthplace;
    private String personnelDateOfAppointment;
    private String personnelDiseaseName;
    private String personnelFatherName;
    private String personnelGrade;
    private String personnelMajor;
    private String personnelHireDate;
    private Date personnelHireDateGreaterThan;
    private Date personnelHireDateLessThan;
    private String personnelMobileNumber;
    private String personnelNextPromotionDate;
    private String personnelEmploymentStatus;
    private String personnelPromotionDate;
    private Integer personnelYearsOfService;
    private Integer personnelEncouragementCount;
    private Integer personnelPunishmentCount;
    private String personnelSubUnitCode;
    private String personnelSubUnitName;
    private String personnelDirectManager;
    private String personnelIndirectManager;
    private String personnelExecutiveManager;
    private String personnelProgramManager;
    private String personnelRank;
    private String personnelUnitCode;
    private Integer personnelNumberOfChildren;
    private String personnelRelativeToSelflessPerson;
    private Double personnelVeteranPercentage;
    private String personnelRelativeToSickPerson;
    private Double personnelKCoefficient;
    private String personnelPersonnelCode;
    private Double personnelHeight;
    private String personnelBloodType;
    private Double personnelWeight;
    private String personnelEyeColor;
    private Integer personnelNumberOfDependents;
    private Date personnelRetirementDate;
    private Integer personnelEncourageMeantCount;
    private Date personnelDateOfJoiningOrganization;
    private String personnelGuise;
    private UUID personnelImageId;
    private PersonnelManagerDTO personnelManager;
    private Integer personnelEvaluationScore;
    private Date personnelDateOfJoiningTheOrganization;
    private Double personnelKCoefficientValue;
    private UUID personnelHighestTenuredEmployee;
    private String personnelHighestTenuredEmployeeOrgId;
    private String personnelRankType;          // درجه
    private Integer personnelRankTypeCode;    //رتبه
    private String personnelRankTypePersianName;
    private String personnelRankCategory;      // کسوت
    private Integer personnelRankCategoryCode;
    private String personnelRankCategoryPersianName;
    private String personnelCaseStatus;  // وضعیت پرونده
    private Integer personnelCaseStatusCode;
    private String personnelCaseStatusPersianName;
    private String personnelServiceStatus;   // وضعیت خدمتی
    private Integer personnelServiceStatusCode;
    private String personnelServiceStatusPersianName;
    private String personnelDegree;          // مقطع تحصیلی
    private Integer personnelDegreeCode;
    private String personnelDegreePersianName;
    private String personnelJobPosition;     // جایگاه شغلی
    private Integer personnelJobPositionCode;
    private String personnelJobPositionPersianName;
    private String personnelMaritalStatus;    // وضعیت تاهل
    private Integer personnelMaritalStatusCode;
    private String personnelMaritalStatusPersianName;
    private String personnelKindOfSacrifice;   // نوع ایثارگری
    private Integer personnelKindOfSacrificeCode;
    private String personnelKindOfSacrificePersianName;
    private String personnelMembership;        // عضویت
    private Integer personnelMembershipCode;
    private Integer personnelRankTypeCivilianCode;    //رتبه
    private String personnelMembershipPersianName;
    private Integer personnelMembershipNot;
    private String personnelHousingStatus;    // وضعیت مسکن
    private Integer personnelHousingStatusCode;
    private String personnelHousingStatusPersianName;
    private String personnelEmploymentLaw;    // قانون استخدام
    private Integer personnelEmploymentLawCode;
    private String personnelEmploymentLawPersianName;
    private Integer pageSize;
    private Integer pageNumber;
    private String correlationId;
    private String type;
    @JsonIgnore
    private String serviceNameForCaching;
}

