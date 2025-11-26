package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.UpgradeDegreeSeniority;
import org.springframework.data.jpa.domain.Specification;

public class UpgradeDegreeSenioritySpecification {

    public static Specification<UpgradeDegreeSeniority> filterByFromDegree(Integer fromDegree) {
        return (root, query, criteriaBuilder) ->
                fromDegree == null ? null : criteriaBuilder.equal(root.get("upgradeDegreeSeniorityFromDegree"), fromDegree);
    }

    public static Specification<UpgradeDegreeSeniority> filterByToDegree(Integer toDegree) {
        return (root, query, criteriaBuilder) ->
                toDegree == null ? null : criteriaBuilder.equal(root.get("upgradeDegreeSeniorityToDegree"), toDegree);
    }

    public static Specification<UpgradeDegreeSeniority> filterByStatus(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                isActive == null ? null : criteriaBuilder.equal(root.get("upgradeDegreeSeniorityIsActive"), isActive);
    }

    public static Specification<UpgradeDegreeSeniority> filterByMaxAmount(Integer maxAmount) {
        return (root, query, criteriaBuilder) ->
                maxAmount == null ? null : criteriaBuilder.equal(root.get("upgradeDegreeSeniorityMaxAmount"), maxAmount);
    }


    public static Specification<UpgradeDegreeSeniority> filterByAmountRange(Integer fromAmount, Integer toAmount) {
        return (root, query, criteriaBuilder) -> {
            if (fromAmount == null && toAmount == null) {
                return null;
            }
            if (fromAmount != null && toAmount != null) {
                return criteriaBuilder.between(root.get("upgradeDegreeSeniorityMaxAmount"), fromAmount, toAmount);
            }
            if (fromAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("upgradeDegreeSeniorityMaxAmount"), fromAmount);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("upgradeDegreeSeniorityMaxAmount"), toAmount);
        };
    }

}
