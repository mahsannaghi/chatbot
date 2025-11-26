package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.dto.RankLevelDataDTO;
import com.paya.EncouragementService.entity.EducationalLevelGrade;
import com.paya.EncouragementService.repository.RankLevelDataFeignClient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EducationalLevelGradeSpecification {

    public static Specification<EducationalLevelGrade> getFilteredSpec(Integer degree, Boolean isActive, String rankDegree, String rankRank, RankLevelDataFeignClient rankLevelDataFeignClient) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();


            if (degree != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("educationalLevelGradeDegree"), degree));
            }


            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("educationalLevelGradeIsActive"), isActive));
            }


            ResponseEntity<List<RankLevelDataDTO>> rankDataResponse = rankLevelDataFeignClient.getAllRankWithCivilian();
            List<RankLevelDataDTO> rankDataList = rankDataResponse.getBody();

            if (rankDataList != null && !rankDataList.isEmpty()) {

                if (rankDegree != null) {
                    rankDataList = rankDataList.stream()
                            .filter(rankData -> rankDegree.equals(rankData.getRankTypeCivilian()))
                            .collect(Collectors.toList());
                }

                if (rankRank != null) {
                    rankDataList = rankDataList.stream()
                            .filter(rankData -> rankRank.equals(rankData.getRankType()))
                            .collect(Collectors.toList());
                }


            }

            return predicate;
        };
    }

}
