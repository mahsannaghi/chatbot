package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.dto.v2.EncouragementFilterDTOV2;
import com.paya.EncouragementService.entity.Encouragement;
import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import com.paya.EncouragementService.entity.EncouragementType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EncouragementSpecifications {





/*    public static Specification<Encouragement> hasRelatedPersonnelIds(List<UUID> relatedPersonnelIds) {
        return (root, query, criteriaBuilder) -> {
            if (relatedPersonnelIds != null && !relatedPersonnelIds.isEmpty()) {
                return root.get("encouragementRelatedPersonnelIds").in(relatedPersonnelIds);
            }
            return null;
        };
    }*/




    /////////////

    public static Specification<Encouragement> hasRelatedPersonnelId2(UUID relatedPersonnelIds) {
        return (root, query, criteriaBuilder) -> {
            if (relatedPersonnelIds != null) {
                return criteriaBuilder.equal(root.get("encouragementRelatedPersonnelId"), relatedPersonnelIds);
            }
            return null;
        };
    }

    ////////////
    public static Specification<Encouragement> hasRegistrarPersonnelId(UUID registrarPersonnelId) {
        return (root, query, criteriaBuilder) -> {
            if (registrarPersonnelId != null) {
                return criteriaBuilder.equal(root.get("encouragementRegistrarPersonnelId"), registrarPersonnelId);
            }
            return null;
        };
    }



    public static Specification<Encouragement> hasApproverPersonnelId(UUID approverPersonnelId) {
        return (root, query, criteriaBuilder) -> {
            if (approverPersonnelId != null) {
                return criteriaBuilder.equal(root.get("encouragementApproverPersonnelId"), approverPersonnelId);
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasReasonTypeId(UUID reasonTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (reasonTypeId != null) {
                return criteriaBuilder.equal(root.get("encouragementReasonTypeId"), reasonTypeId);
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasEncouragementNumber(String encouragementNumber) {
        return (root, query, criteriaBuilder) -> {
            if (encouragementNumber != null && !encouragementNumber.isEmpty()) {
                return criteriaBuilder.like(root.get("encouragementNumber"), "%" + encouragementNumber + "%");
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasCreatedAt(Date createdAt) {
        return (root, query, criteriaBuilder) -> {
            if (createdAt != null) {
                LocalDateTime localDateTime = new java.sql.Timestamp(createdAt.getTime()).toLocalDateTime();
                return criteriaBuilder.equal(root.get("encouragementCreatedAt"), localDateTime);
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasApprovedAt(Date approvedAt) {
        return (root, query, criteriaBuilder) -> {
            if (approvedAt != null) {
                LocalDateTime localDateTime = new java.sql.Timestamp(approvedAt.getTime()).toLocalDateTime();
                return criteriaBuilder.equal(root.get("encouragementApprovedAt"), localDateTime);
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasMinAmount(Double minAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("encouragementAmount"), minAmount);
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasMaxAmount(Double maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (maxAmount != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("encouragementAmount"), maxAmount);
            }
            return null;
        };
    }

    public static Specification<Encouragement> hasEncouragementStatus(Integer encouragementStatus) {
        return (root, query, criteriaBuilder) -> {
            if (encouragementStatus != null) {
                return criteriaBuilder.equal(root.get("encouragementStatus"), encouragementStatus);
            }
            return null;
        };
    }



    // اضافه کردن فیلتر برای punishmentQuadType
// برای زمانی که isQuad = 0 باشد (غیر چهارگانه ها را نشان ندهد)
    public static Specification<Encouragement> isNotQuad() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("punishmentQuadType"), 0);
    }

    // برای زمانی که isQuad = 1 باشد (تمام رکوردها را نشان بدهد)
    public static Specification<Encouragement> isQuad() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("punishmentQuadType"), 1);
    }

    public static Specification<Encouragement> filterBySpecification(EncouragementFilterDTOV2 dto) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getEncouragementNumber() != null) {
                predicates.add(criteriaBuilder.like(root.get(EncouragementFilterDTOV2.Fields.encouragementNumber),
                        "%" + dto.getEncouragementNumber().trim()  + "%"));
            }
            if (dto.getEncouragementAmount() != null) {
                predicates.add(criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementAmount),
                        dto.getEncouragementAmount()));
            }
            if (dto.getEncouragementRegistrarOrganizationIdList() != null && dto.getEncouragementRegistrarOrganizationIdList().size() > 0) {
                Path<String> path = root.get(EncouragementFilterDTOV2.Fields.encouragementRegistrarOrganizationId);
                predicates.add(path.in(dto.getEncouragementRegistrarOrganizationIdList()));
            }
            if (dto.getEncouragementApproverOrganizationIdList() != null && dto.getEncouragementApproverOrganizationIdList().size() > 0) {
                Path<String> path = root.get(EncouragementFilterDTOV2.Fields.encouragementApproverOrganizationId);
                predicates.add(path.in(dto.getEncouragementApproverOrganizationIdList()));
            }
            if (dto.getEncouragementPersonnelOrganizationIdList() != null && dto.getEncouragementPersonnelOrganizationIdList().size() > 0) {
                Path<String> path = root.get(EncouragementFilterDTOV2.Fields.encouragementPersonnelOrganizationId);
                predicates.add(path.in(dto.getEncouragementPersonnelOrganizationIdList()));
            }
            if (dto.getEncouragementRegistrarOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementRegistrarOrganizationId),
                        dto.getEncouragementRegistrarOrganizationId()));
            }
            if (dto.getEncouragementStatusList() != null && dto.getEncouragementStatusList().size() > 0) {
                Path<Object> path = root.get(EncouragementFilterDTOV2.Fields.encouragementStatus);
                predicates.add(path.in(dto.getEncouragementStatusList()));
            }
            if (dto.getEncouragementStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementStatus),
                        dto.getEncouragementStatus()));
            }
            if (dto.getEncouragementStatusNot() != null) {
                predicates.add(criteriaBuilder.notEqual(root.get(EncouragementFilterDTOV2.Fields.encouragementStatus),
                        dto.getEncouragementStatusNot()));
            }
            if (dto.getEncouragementPersonnelOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementPersonnelOrganizationId),
                        dto.getEncouragementPersonnelOrganizationId()));
            }
            if (dto.getEncouragementCreatedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(EncouragementFilterDTOV2.Fields.encouragementCreatedAt),
                        dto.getEncouragementCreatedAtTo().atTime(LocalTime.MAX)));
            }
            if (dto.getEncouragementCreatedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(EncouragementFilterDTOV2.Fields.encouragementCreatedAt),
                        dto.getEncouragementCreatedAtFrom().atStartOfDay()));
            }
            if (dto.getEncouragementAppliedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(EncouragementFilterDTOV2.Fields.encouragementAppliedDate),
                        dto.getEncouragementAppliedAtTo()));
            }
            if (dto.getEncouragementAppliedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(EncouragementFilterDTOV2.Fields.encouragementAppliedDate),
                        dto.getEncouragementAppliedAtFrom()));
            }
            if (dto.getEncouragementRegistrarOrEncouragedOrganizationId() != null) {
                Predicate registrar = criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementRegistrarOrganizationId),
                        dto.getEncouragementRegistrarOrEncouragedOrganizationId());
                Predicate encouraged = criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementPersonnelOrganizationId),
                        dto.getEncouragementRegistrarOrEncouragedOrganizationId());
                predicates.add(criteriaBuilder.or(registrar, encouraged));
            }
            if (dto.getEncouragementType() != null) {
                dto.setEncouragementType(dto.getEncouragementType().replace("ی", "ي").trim());
                Subquery<UUID> typeSubquery = query.subquery(UUID.class);
                Root<EncouragementType> typeRoot = typeSubquery.from(EncouragementType.class);
                typeSubquery.select(typeRoot.get("encouragementTypeId"))
                        .where(criteriaBuilder.like(typeRoot.get("encouragementTypeTitle"), "%" + dto.getEncouragementType() + "%"));

                Subquery<UUID> subquery1 = query.subquery(UUID.class);
                Root<EncouragementReasonType> encouragementRoot1 = subquery1.from(EncouragementReasonType.class);
                subquery1.select(encouragementRoot1.get("encouragementReasonTypeId"))
                        .where(encouragementRoot1.get("encouragementTypeId").in(typeSubquery));
                predicates.add(root.get(EncouragementFilterDTOV2.Fields.encouragementReasonTypeId).in(subquery1));
            }
            if (dto.getEncouragementReason() != null) {
                dto.setEncouragementReason(dto.getEncouragementReason().replace("ی", "ي").trim());
                Subquery<UUID> typeSubquery = query.subquery(UUID.class);
                Root<EncouragementReason> typeRoot = typeSubquery.from(EncouragementReason.class);
                typeSubquery.select(typeRoot.get("encouragementReasonId"))
                        .where(criteriaBuilder.like(typeRoot.get("encouragementReasonTitle"), "%" + dto.getEncouragementReason() + "%"));

                Subquery<UUID> subquery1 = query.subquery(UUID.class);
                Root<EncouragementReasonType> encouragementRoot1 = subquery1.from(EncouragementReasonType.class);
                subquery1.select(encouragementRoot1.get("encouragementReasonTypeId"))
                        .where(encouragementRoot1.get("encouragementReasonId").in(typeSubquery));
                predicates.add(root.get(EncouragementFilterDTOV2.Fields.encouragementReasonTypeId).in(subquery1));
            }
            if (dto.getEncouragementAmount() != null) {
                predicates.add(criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementAmount), dto.getEncouragementAmount()));
            }
            if (dto.getEncouragementTypeCategory() != null) {
                predicates.add(criteriaBuilder.equal(root.get(EncouragementFilterDTOV2.Fields.encouragementTypeCategory),
                        dto.getEncouragementTypeCategory()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}
