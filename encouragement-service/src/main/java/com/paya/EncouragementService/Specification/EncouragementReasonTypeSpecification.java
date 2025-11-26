package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import com.paya.EncouragementService.entity.EncouragementType;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EncouragementReasonTypeSpecification {

    public static Specification<EncouragementReasonType> searchByReasonAndTypeTitles(String reasonTitle, String typeTitle) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Subquery for EncouragementReason if reasonTitle is provided
            if (reasonTitle != null && !reasonTitle.isEmpty()) {
                Subquery<UUID> reasonSubquery = query.subquery(UUID.class);
                Root<EncouragementReason> reasonRoot = reasonSubquery.from(EncouragementReason.class);
                reasonSubquery.select(reasonRoot.get("encouragementReasonId"))
                        .where(criteriaBuilder.like(reasonRoot.get("encouragementReasonTitle"), "%" + reasonTitle + "%"));
                predicates.add(root.get("encouragementReasonId").in(reasonSubquery));
            }

            // Subquery for EncouragementType if typeTitle is provided
            if (typeTitle != null && !typeTitle.isEmpty()) {
                Subquery<UUID> typeSubquery = query.subquery(UUID.class);
                Root<EncouragementType> typeRoot = typeSubquery.from(EncouragementType.class);
                typeSubquery.select(typeRoot.get("encouragementTypeId"))
                        .where(criteriaBuilder.like(typeRoot.get("encouragementTypeTitle"), "%" + typeTitle + "%"));
                predicates.add(root.get("encouragementTypeId").in(typeSubquery));
            }

            // Return all predicates as AND combination
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<EncouragementReasonType> searchByFilters(
            String reasonTitle, String typeTitle, BigDecimal maxAmount, Integer maxDuration, String durationType,Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Subquery for EncouragementReason if reasonTitle is provided
            if (reasonTitle != null && !reasonTitle.trim().isEmpty()) {
                Subquery<UUID> reasonSubquery = query.subquery(UUID.class);
                Root<EncouragementReason> reasonRoot = reasonSubquery.from(EncouragementReason.class);
                reasonSubquery.select(reasonRoot.get("encouragementReasonId"))
                        .where(criteriaBuilder.like(
                                criteriaBuilder.lower(reasonRoot.get("encouragementReasonTitle")),
                                "%" + reasonTitle.toLowerCase().trim() + "%"
                        ));
                predicates.add(root.get("encouragementReasonId").in(reasonSubquery));
            }

            // Subquery for EncouragementType if typeTitle is provided
//            if (typeTitle != null && !typeTitle.trim().isEmpty()) {
//                Subquery<UUID> typeSubquery = query.subquery(UUID.class);
//                Root<EncouragementType> typeRoot = typeSubquery.from(EncouragementType.class);
//                typeSubquery.select(typeRoot.get("encouragementTypeId"))
//                        .where(criteriaBuilder.like(
//                                criteriaBuilder.lower(typeRoot.get("encouragementTypeTitle")),
//                                "%" + typeTitle.toLowerCase().trim() + "%"
//                        ));
//                predicates.add(root.get("encouragementTypeId").in(typeSubquery));
//            }

            // Filter by maxAmount
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.equal(root.get("maxAmount"), maxAmount));
            }

            // Filter by maxDuration
            if (maxDuration != null) {
                predicates.add(criteriaBuilder.equal(root.get("maxDuration"), maxDuration));
            }

            // Filter by durationType
            if (durationType != null && !durationType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("durationType")),
                        "%" + durationType.toLowerCase().trim() + "%"
                ));
            }

            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}