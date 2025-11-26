package com.paya.EncouragementService.service;

import com.paya.EncouragementService.Specification.EducationalLevelGradeSpecification;
import com.paya.EncouragementService.dto.EducationalLevelGradeDTO;
import com.paya.EncouragementService.dto.RankLevelDataDTO;
import com.paya.EncouragementService.dto.v2.EducationalLevelGradeDTOV2;
import com.paya.EncouragementService.entity.EducationalLevelGrade;
import com.paya.EncouragementService.enumeration.RankTypeEnum;
import com.paya.EncouragementService.repository.EducationalLevelGradeRepository;
import com.paya.EncouragementService.repository.RankLevelDataFeignClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EducationalLevelGradeService {

    private final EducationalLevelGradeRepository repository;
    private final RankLevelDataFeignClient rankLevelDataFeignClient;
    private final UpgradeDegreeSeniorityService upgradeDegreeSeniorityService;


    public EducationalLevelGradeService(EducationalLevelGradeRepository repository, RankLevelDataFeignClient rankLevelDataFeignClient, UpgradeDegreeSeniorityService upgradeDegreeSeniorityService) {
        this.repository = repository;
        this.rankLevelDataFeignClient = rankLevelDataFeignClient;
        this.upgradeDegreeSeniorityService = upgradeDegreeSeniorityService;
    }


    public List<EducationalLevelGradeDTO> getAllEducationalLevelGrades(Integer degree, Boolean isActive, String rankDegree, String rankRank) {
        Specification<EducationalLevelGrade> spec = EducationalLevelGradeSpecification.getFilteredSpec(degree, isActive, rankDegree, rankRank, rankLevelDataFeignClient);
        List<EducationalLevelGrade> educationalLevelGrades = repository.findAll(spec);
        List<EducationalLevelGradeDTO> result = new ArrayList<>();
        for (EducationalLevelGrade educationalLevelGrade : educationalLevelGrades) {
            Integer gradeId = educationalLevelGrade.getEducationalLevelGradeRankTypeCode();
            ResponseEntity<List<RankLevelDataDTO>> rankDataResponse;
            if (rankRank != null) {
                rankDataResponse = rankLevelDataFeignClient.getAllRankWithCivilian();
            } else {
                rankDataResponse = rankLevelDataFeignClient.getAllRankWithCivilian();
            }
            List<RankLevelDataDTO> rankDataList = rankDataResponse.getBody();
            RankLevelDataDTO rankData = null;
            if (rankDataList != null && !rankDataList.isEmpty()) {
                if (gradeId != null) {
                    // تطابق rankLevelDataId با gradeId
                    rankData = null;
                }
                if (rankRank != null && rankData == null) {
                    rankData = rankDataList.stream()
                            .filter(data -> data.getRankType().equals(rankRank)) // تطابق rankLevelDataRank با rankRank
                            .findFirst()
                            .orElse(null);
                }
            }
            if (rankData == null) {
                System.out.println("No matching rank data found for gradeId: " + gradeId + " and rankRank: " + rankRank);
            } else {
                System.out.println("Found matching rank data: " + rankData);
            }
            EducationalLevelGradeDTO dto = EducationalLevelGradeDTO.builder()
                    .educationalLevelGradeId(educationalLevelGrade.getEducationalLevelGradeId())
                    .educationalLevelGradeDegree(educationalLevelGrade.getEducationalLevelGradeDegree())
                    .educationalLevelGradeRankTypeCode(educationalLevelGrade.getEducationalLevelGradeRankTypeCode())
                    .educationalLevelGradeRankTypeCivilian(educationalLevelGrade.getEducationalLevelGradeRankTypeCode() + 11)
                    .educationalLevelGradeRankTypePersianName(RankTypeEnum.fromRankCode(educationalLevelGrade.getEducationalLevelGradeRankTypeCode()).getPersianName())
                    .educationalLevelGradeIsActive(educationalLevelGrade.getEducationalLevelGradeIsActive())
                    .educationalLevelGradeDegree(educationalLevelGrade.getEducationalLevelGradeDegree())
//                    .rankLevelDataRank(rankData != null ? rankData.getRankType() : null)
//                    .rankLevelDataId(rankData != null ? rankData.getJobGroupCode() : null)
//                    .rankLevelDataJobLevel(String.valueOf(rankData != null ? rankData.getJobGroupLevel() : null))
                    .build();
            result.add(dto);
        }
        return result;
    }


    public EducationalLevelGradeDTO createEducationalLevelGrade(EducationalLevelGradeDTO educationalLevelGradeDTO) throws Exception {
        Optional<EducationalLevelGrade> existingEntity = repository.findByEducationalLevelGradeDegree(educationalLevelGradeDTO.getEducationalLevelGradeDegree());
        if (existingEntity.isPresent()) {

            throw new Exception("مقطع تحصیلی تکراری است");
        }
        EducationalLevelGrade entity = new EducationalLevelGrade();
        entity.setEducationalLevelGradeDegree(educationalLevelGradeDTO.getEducationalLevelGradeDegree());
        entity.setEducationalLevelGradeRankTypeCode(educationalLevelGradeDTO.getEducationalLevelGradeRankTypeCode());
        entity.setEducationalLevelGradeIsActive(educationalLevelGradeDTO.getEducationalLevelGradeIsActive());
        EducationalLevelGrade savedEntity = repository.save(entity);
        return convertToDTO(savedEntity);
    }


    public EducationalLevelGradeDTO updateEducationalLevelGrade(UUID educationalLevelGradeId, EducationalLevelGradeDTO educationalLevelGradeDTO) throws Exception {
        Optional<EducationalLevelGrade> existingEntity = repository.findById(educationalLevelGradeId);
        if (existingEntity.isPresent()) {
            Optional<EducationalLevelGrade> existingDegree = repository.findByEducationalLevelGradeDegree(educationalLevelGradeDTO.getEducationalLevelGradeDegree());
            if (existingDegree.isPresent() && !existingDegree.get().getEducationalLevelGradeId().equals(educationalLevelGradeId)) {
                throw new Exception("مقطع تحصیلی تکراری است");
            }
            EducationalLevelGrade entity = existingEntity.get();
            if (educationalLevelGradeDTO.getEducationalLevelGradeDegree() != null)
                entity.setEducationalLevelGradeDegree(educationalLevelGradeDTO.getEducationalLevelGradeDegree());
            if (educationalLevelGradeDTO.getEducationalLevelGradeRankTypeCode() != null)
                entity.setEducationalLevelGradeRankTypeCode(educationalLevelGradeDTO.getEducationalLevelGradeRankTypeCode());
            if (educationalLevelGradeDTO.getEducationalLevelGradeRankTypeCivilian() != null)
                entity.setEducationalLevelGradeRankTypeCode(educationalLevelGradeDTO.getEducationalLevelGradeRankTypeCivilian() - 11);
            if (educationalLevelGradeDTO.getEducationalLevelGradeIsActive() != null)
                entity.setEducationalLevelGradeIsActive(educationalLevelGradeDTO.getEducationalLevelGradeIsActive());
            EducationalLevelGrade updatedEntity = repository.save(entity);
            return convertToDTO(updatedEntity);
        }
        throw new Exception("EducationalLevelGrade not found with ID: " + educationalLevelGradeId);
    }


    public void deleteEducationalLevelGrade(UUID educationalLevelGradeId) {
        repository.deleteById(educationalLevelGradeId);
    }


    private EducationalLevelGradeDTO convertToDTO(EducationalLevelGrade entity) {
//        ResponseEntity<List<RankLevelDataDTO>> rankDataResponse = rankLevelDataFeignClient.getRankLevelData();
//        RankLevelDataDTO rankData = null;
//        if (rankDataResponse.hasBody()) {
//             rankData = rankDataResponse.getBody().stream().filter(rank -> rank.getRankTypeCode().equals(entity.getEducationalLevelGradeRankTypeCode())).findFirst().orElseThrow();
//        }

        return EducationalLevelGradeDTO.builder()
                .educationalLevelGradeId(entity.getEducationalLevelGradeId())
                .educationalLevelGradeDegree(entity.getEducationalLevelGradeDegree())
                .educationalLevelGradeRankTypeCode(entity.getEducationalLevelGradeRankTypeCode())
                .educationalLevelGradeIsActive(entity.getEducationalLevelGradeIsActive())
//                .rankLevelDataDegree(rankData != null ? rankData.getJobGroup() : null)
                .educationalLevelGradeRankTypeCode(entity.getEducationalLevelGradeRankTypeCode())
                .educationalLevelGradeRankTypePersianName(RankTypeEnum.fromRankCode(entity.getEducationalLevelGradeRankTypeCode()).getPersianName())
                .educationalLevelGradeRankTypeCivilian(entity.getEducationalLevelGradeRankTypeCode() + 11)
//                .rankLevelDataId(rankData != null ? rankData.getRankLevelDataId() : null)
//                .rankLevelDataJobLevel(String.valueOf(rankData != null ? rankData.getJobGroupLevel() : null))
                .build();
    }


    public EducationalLevelGradeDTO getRankLevelWithDegree(Integer degree) throws Exception {
        EducationalLevelGrade initialEntity = repository.findByEducationalLevelGradeDegree(degree).orElse(null);
        if (initialEntity == null)
            throw new Exception("برای این مقطع یافت نشد.");
        return this.convertToDTO(initialEntity);

    }

    public EducationalLevelGradeDTOV2 getRankAndMaxAmount(Integer lastDegree, Integer newDegree) throws Exception {
        if (lastDegree != null && newDegree != null) {
            EducationalLevelGradeDTO educationalLevelGradeDTO = this.getRankLevelWithDegree(newDegree);
            Integer maxAmount = upgradeDegreeSeniorityService.getMaxAmountWithLastDegreeAndNewDegree(lastDegree, newDegree);
            if (maxAmount != null && educationalLevelGradeDTO.getEducationalLevelGradeId() != null) {
                return new EducationalLevelGradeDTOV2(maxAmount, educationalLevelGradeDTO);
            } else throw new Exception("ارشدیت تحصیلی یا میزان ارشدیت تحصیلی معادل در سیستم ثبت نشده است .");
        } else throw new Exception( "پارامتر ورودی صحیح نمی باشد .");
    }


}
