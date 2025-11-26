package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.QuranicSeniority;
import org.springframework.data.jpa.domain.Specification;

public class QuranicSenioritySpecification {

    public static Specification<QuranicSeniority> filterByType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.like(root.get("quranicSeniorityType"), "%" + type + "%");
    }

    public static Specification<QuranicSeniority> filterByAmount(String amount) {
        return (root, query, criteriaBuilder) ->
                amount == null ? null : criteriaBuilder.like(root.get("quranicSeniorityAmount"), "%" + amount + "%");
    }

    public static Specification<QuranicSeniority> filterByMaxAmountRange(Integer fromAmount, Integer toAmount) {
        return (root, query, criteriaBuilder) -> {
            if (fromAmount == null && toAmount == null) {
                return null;
            } else if (fromAmount != null && toAmount != null) {
                return criteriaBuilder.between(root.get("quranicSeniorityMaxAmount"), fromAmount, toAmount);
            } else if (fromAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("quranicSeniorityMaxAmount"), fromAmount);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("quranicSeniorityMaxAmount"), toAmount);
            }
        };
    }



    public static Specification<QuranicSeniority> filterByStatus(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                isActive == null ? null : criteriaBuilder.equal(root.get("quranicSeniorityIsActive"), isActive);
    }
}
