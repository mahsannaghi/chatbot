package com.paya.EncouragementService.mapper;

import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.humanResource.HumanResourceBasePersonnelDTO;
import com.paya.EncouragementService.dto.humanResource.HumanResourcePersonnelDTO;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.enumeration.MembershipType;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class PersonnelMapper {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"); // Adjust the date format as needed

    public static PersonnelDTO convertToDTO(HumanResourcePersonnelDTO humanResourcePersonnelDTO) {
        if (humanResourcePersonnelDTO == null) {
            return null;
        }

        PersonnelDTO dto = PersonnelDTO.builder()
                .personnelOrganizationID(humanResourcePersonnelDTO.getPersonnelLogin())
                .personnelServiceUnit(humanResourcePersonnelDTO.getUnitName())
                .personnelJobFamilyCode(humanResourcePersonnelDTO.getJobFamilyCode())
                .personnelJobFamilyName(humanResourcePersonnelDTO.getJobFamilyName())
                .personnelJobFamilyName(humanResourcePersonnelDTO.getJobFamilyName())
//                .personnelContractType(humanResourcePersonnelDTO.getMembership()) // Mapped from personnelMembership
//                .personnelAverage(humanResourcePersonnelDTO.getAverage())
                .personnelBirthCertificateNumber(humanResourcePersonnelDTO.getShenaseh())
                .personnelBirthDate(humanResourcePersonnelDTO.getBirthDate())
                .personnelBirthplace(humanResourcePersonnelDTO.getBirthPlace())
                .personnelCaseStatus(humanResourcePersonnelDTO.getServiceStatus())
//                .personnelCaseStatusCode(humanResourcePersonnelDTO.getPersonnelCaseStatus() != null? humanResourcePersonnelDTO.getPersonnelCaseStatus().getCode() : null)
//                .personnelCaseStatusPersianName(humanResourcePersonnelDTO.getPersonnelCaseStatus() != null? humanResourcePersonnelDTO.getPersonnelCaseStatus().getPersianName() : null)
                .personnelDateOfAppointment(humanResourcePersonnelDTO.getDateOfAppointment())
                .personnelDiseaseName(humanResourcePersonnelDTO.getDiseaseName())
//                .personnelDegreeCode(humanResourcePersonnelDTO.getDegree() != null? humanResourcePersonnelDTO.getPersonnelDegreeCode().getCode() : null)
                .personnelDegree(humanResourcePersonnelDTO.getDegree())
//                .personnelDegreePersianName(humanResourcePersonnelDTO.getPersonnelDegreeCode() != null ? humanResourcePersonnelDTO.getPersonnelDegreeCode().getPersianName() : null)
                .personnelFatherName(humanResourcePersonnelDTO.getFatherName())
//                .personnelGrade(humanResourcePersonnelDTO.getGrade())
                .personnelMajor(humanResourcePersonnelDTO.getMajor())
                .personnelHireDate(humanResourcePersonnelDTO.getHireDate())
//                .personnelJobPosition(humanResourcePersonnelDTO.getPersonnelJobPosition())
//                .personnelMobileNumber(humanResourcePersonnelDTO.getN)
                .personnelNextPromotionDate(humanResourcePersonnelDTO.getNextPromotionDate())
                .personnelEmploymentStatus(humanResourcePersonnelDTO.getEmploymentStatus())
                .personnelPromotionDate(humanResourcePersonnelDTO.getPromotionDate())
                .personnelYearsOfService(humanResourcePersonnelDTO.getYearsOfService())
                .personnelEncouragementCount(humanResourcePersonnelDTO.getNumberOfEncouragement())
                .personnelPunishmentCount(humanResourcePersonnelDTO.getNumberOfPunishments())
                .personnelUnitCode(humanResourcePersonnelDTO.getUnitCode())
                .personnelSubUnitCode(humanResourcePersonnelDTO.getSubUnitCode())
                .personnelSubUnitName(humanResourcePersonnelDTO.getSubUnitName())
                .personnelDirectManager(humanResourcePersonnelDTO.getDirectManager())
                .personnelIndirectManager(humanResourcePersonnelDTO.getIndirectManager())
                .personnelExecutiveManager(humanResourcePersonnelDTO.getExecutiveManager())
                .personnelProgramManager(humanResourcePersonnelDTO.getProgramManager())
                .personnelMaritalStatus(humanResourcePersonnelDTO.getMaritalStatus())
//                .personnelMaritalStatusCode(humanResourcePersonnelDTO.getPersonnelMaritalStatus() != null ? humanResourcePersonnelDTO.getPersonnelMaritalStatus().getCode() : null)
//                .personnelMaritalStatusPersianName(humanResourcePersonnelDTO.getPersonnelMaritalStatus() != null ? humanResourcePersonnelDTO.getPersonnelMaritalStatus().getPersianName() : null)
                .personnelNumberOfChildren(humanResourcePersonnelDTO.getNumberOfChildren())
//                .personnelKindOfSacrifice(humanResourcePersonnelDTO.getKindOfSacrifice() != null? humanResourcePersonnelDTO.getPersonnelKindOfSacrifice().name(): null)
                .personnelKindOfSacrificeCode(humanResourcePersonnelDTO.getKindOfSacrifice())
//                .personnelKindOfSacrificePersianName(humanResourcePersonnelDTO.getPersonnelKindOfSacrifice() != null? humanResourcePersonnelDTO.getPersonnelKindOfSacrifice().getPersianName(): null)
//                .personnelRelativeToSelflessPerson(humanResourcePersonnelDTO.getRelativeToSelflessPerson())
                .personnelVeteranPercentage(humanResourcePersonnelDTO.getVeteranPercentage())
//                .personnelRelativeToSickPerson(humanResourcePersonnelDTO.getRelativeToSickPerson())
                .personnelKCoefficient(humanResourcePersonnelDTO.getKcoefficient())
                .personnelPersonnelCode(humanResourcePersonnelDTO.getPersonnelCode())
                .personnelMembership(humanResourcePersonnelDTO.getMembership() != null ? MembershipType.fromCode(humanResourcePersonnelDTO.getMembership()).name() : null)
               .personnelMembershipCode(humanResourcePersonnelDTO.getMembership())
                .personnelMembershipPersianName(humanResourcePersonnelDTO.getMembership() != null ? MembershipType.fromCode(humanResourcePersonnelDTO.getMembership()).getPersianName() : null)
                .personnelServiceStatus(humanResourcePersonnelDTO.getServiceStatus())
//                .personnelServiceStatusCode(humanResourcePersonnelDTO.getPersonnelServiceStatus() != null ? humanResourcePersonnelDTO.getPersonnelServiceStatus().getCode(): null)
//                .personnelServiceStatusPersianName(humanResourcePersonnelDTO.getPersonnelServiceStatus() != null ? humanResourcePersonnelDTO.getPersonnelServiceStatus().getPersianName(): null)
                .personnelHeight(humanResourcePersonnelDTO.getHeight())
                .personnelBloodType(humanResourcePersonnelDTO.getBloodType())
                .personnelWeight(humanResourcePersonnelDTO.getWeight())
                .personnelHousingStatus(humanResourcePersonnelDTO.getHousingStatus())
//                .personnelHousingStatusCode(humanResourcePersonnelDTO.getPersonnelHousingStatus() != null? humanResourcePersonnelDTO.getPersonnelHousingStatus().getCode(): null)
//                .personnelHousingStatusPersianName(humanResourcePersonnelDTO.getPersonnelHousingStatus() != null? humanResourcePersonnelDTO.getPersonnelHousingStatus().getPersianName(): null)
                .personnelEyeColor(humanResourcePersonnelDTO.getEyeColor())
                .personnelNumberOfDependents(humanResourcePersonnelDTO.getNumberOfDependents())
                .personnelRetirementDate(humanResourcePersonnelDTO.getRetirementDate())
                .personnelEmploymentLaw(humanResourcePersonnelDTO.getEmploymentLaw())
//                .personnelEmploymentLawCode(humanResourcePersonnelDTO.getPersonnelEmploymentLaw() != null ? humanResourcePersonnelDTO.getPersonnelEmploymentLaw().getCode() : null)
//                .personnelEmploymentLawPersianName(humanResourcePersonnelDTO.getPersonnelEmploymentLaw() != null ? humanResourcePersonnelDTO.getPersonnelEmploymentLaw().getPersianName() : null)
//                .personnelDateOfJoiningOrganization(humanResourcePersonnelDTO.getDateOfJoiningOrganization())
//                .personnelGuise(humanResourcePersonnelDTO.getGuise())
//                .personnelImageId(humanResourcePersonnelDTO.getImage())
//                .personnelEvaluationScore(humanResourcePersonnelDTO.getEvaluationScore())
                .personnelKCoefficientValue(humanResourcePersonnelDTO.getKcoefficient())
                .personnelHighestTenuredEmployee(humanResourcePersonnelDTO.getHighestTenuredEmployee() != null ? UUID.fromString(humanResourcePersonnelDTO.getHighestTenuredEmployee()) : null)
                .personnelRankType(humanResourcePersonnelDTO.getRankType())
//                .personnelRankTypeCode(humanResourcePersonnelDTO.getPersonnelRankType() != null ? RankType.fromRankCode(humanResourcePersonnelDTO.getPersonnelRankType().getRankCode()).getRankCode()  : null)
//                .personnelRankTypePersianName(humanResourcePersonnelDTO.getPersonnelRankType() != null ? RankType.fromRankCode(humanResourcePersonnelDTO.getPersonnelRankType().getRankCode()).getPersianName() : null)
//                .personnelRankTypeCivilianCode(humanResourcePersonnelDTO.getPersonnelRankType() != null ? RankType.fromRankCode(humanResourcePersonnelDTO.getPersonnelRankType().getRankCode()).getRankCode() + 11 : null)
//                .personnelRankCategory(humanResourcePersonnelDTO.getPersonnelRankCategory() != null ? RankCategory.fromCode(humanResourcePersonnelDTO.getPersonnelRankCategory().getCode()).name() : null)
//                .personnelRankCategoryCode(humanResourcePersonnelDTO.getPersonnelRankCategory() != null ? RankCategory.fromCode(humanResourcePersonnelDTO.getPersonnelRankCategory().getCode()).getCode() : null)
//                .personnelRankCategoryPersianName(humanResourcePersonnelDTO.getPersonnelRankCategory() != null ? RankCategory.fromCode(humanResourcePersonnelDTO.getPersonnelRankCategory().getCode()).getPersianName() : null)
//                .personnelJobPosition(humanResourcePersonnelDTO.getJobPosition() != null ? JobPositionType.fromCode(humanResourcePersonnelDTO.getPersonnelJobPosition().getCode()).name() : null)
//                .personnelJobPositionCode(humanResourcePersonnelDTO.getPersonnelJobPosition() != null ? JobPositionType.fromCode(humanResourcePersonnelDTO.getPersonnelJobPosition().getCode()).getCode()  : null)
//                .personnelJobPositionPersianName(humanResourcePersonnelDTO.getPersonnelJobPosition() != null ? JobPositionType.fromCode(humanResourcePersonnelDTO.getPersonnelJobPosition().getCode()).getPersianName() : null)
                .build();
                dto.setPersonnelId(humanResourcePersonnelDTO.getId());
                dto.setPersonnelFirstName(humanResourcePersonnelDTO.getFirstName());
                dto.setPersonnelLastName(humanResourcePersonnelDTO.getLastname());
                dto.setPersonnelOrganizationID(humanResourcePersonnelDTO.getPersonnelLogin());
                dto.setPersonnelNationalCode(humanResourcePersonnelDTO.getNationalCode());
//                dto.setPersonnelAge(humanResourcePersonnelDTO.getAge());

        return dto;
    }


    private static String formatDate(java.util.Date date) {
        return date != null ? DATE_FORMAT.format(date) : null; // Return formatted date or null
    }

    public static HumanResourceBasePersonnelDTO convertToHumanResourceBasePersonnelDTO(PersonnelFilterDTOV2 personnel) {
        return HumanResourceBasePersonnelDTO.builder().personnelOrganizationID(personnel.getPersonnelOrganizationId())
                .unitCode(personnel.getPersonnelUnitCode())
                .firstName(personnel.getPersonnelFirstName())
                .lastname(personnel.getPersonnelLastName())
                .rankTypeCode(Integer.valueOf(personnel.getPersonnelRankType()))
                .build();
    }
}