package com.paya.EncouragementService.dto.humanResource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@SuperBuilder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class HumanResourcePersonnelDTO extends HumanResourceBasePersonnelDTO{
    private String id;
    private String personnelLogin; // organizationId
    private String firstName;
    private String lastname;
    private String unitName;
    private String unitCode;
    private int gender;
    private String birthCertificateNumber;
    private String fatherName;
    private String jobFamilyName;
    private String jobFamilyCode;
    private String nationalCode;
    private String fileStatus;
    private String serviceStatus;
    private String birthDate;
    private String startWorkDate;
    private int yearsOfService;
    private String maritalStatus;
    private int numberOfChildren;
    private int kindOfSacrifice;
    private String relativeToVeteran;
    private Double veteranPercentage;
    private String diseaseName;
    private String personnelCode;
    private Integer membership;
    private String degree;
    private String dateOfAppointment;
    private Double height;
    private Double weight;
    private String major;
    private String bloodType;
    private String birthPlace;
    private String housingStatus;
    private String eyeColor;
    private Integer numberOfDependents;
    private Date retirementDate;
    private String hireDate;
    private String nextPromotionDate;
    private String promotionDate;
    private String dateOfRecruitment;
    private int numberOfEncouragement;
    private int numberOfPunishments;
    private String employmentStatus;
    private String rank;
    private String rankCode;
    private String employmentLaw;
    private String subUnitCode;
    private String subUnitName;
    private String rankType;
    private String elite;
    private String image;
    private String managers;
    private String directManager;
    private String indirectManager;
    private String executiveManager;
    private String programManager;
    private String highestTenuredEmployee;
    private String gpa;
    private Double kcoefficient;
}
