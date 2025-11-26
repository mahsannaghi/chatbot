package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.dto.EncouragementReviewSearchDTO;
import com.paya.EncouragementService.entity.*;
import com.paya.EncouragementService.enumeration.ReviewTypeEnum;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class EncouragementReviewSpecification {

    public static Specification<EncouragementReview> filterByCriteria(
            EncouragementReviewSearchDTO dto) {

        return (root, query, criteriaBuilder) -> {
            Specification<EncouragementReview> spec = Specification.where(null);

            if (dto.getEncouragementReviewEncouragementId() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewEncouragementId"), dto.getEncouragementReviewEncouragementId()));
            }
            if (dto.getEncouragementReviewRegistrarPersonnelId() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewRegistrarPersonnelId"), dto.getEncouragementReviewRegistrarPersonnelId()));
            }
            if (dto.getEncouragementReviewRegistrarPersonnelOrganizationId() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewRegistrarOrganizationId"), dto.getEncouragementReviewRegistrarPersonnelOrganizationId()));
            }
            if (dto.getEncouragementReviewPayingAuthorityId() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewPayingAuthorityId"), dto.getEncouragementReviewPayingAuthorityId()));
            }
            if (dto.getEncouragementReviewEncouragementTypeId() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewEncouragementTypeId"), dto.getEncouragementReviewEncouragementTypeId()));
            }
            if (dto.getEncouragementReviewResult() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewResult"), dto.getEncouragementReviewResult()));
            }
//            if (dto.getEncouragementReviewDraftNotSent() != null) {
//                spec = spec.and((root1, query1, criteriaBuilder1) ->
//                        criteriaBuilder1.notEqual(root1.get("encouragementReviewDraft"), dto.getEncouragementReviewDraftNotSent()));
//            }
            if (dto.getEncouragementReviewResultList() != null && dto.getEncouragementReviewDraftNotSent() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) -> criteriaBuilder1.or(root1.get("encouragementReviewResult").in(dto.getEncouragementReviewResultList()),
                        criteriaBuilder1.notEqual(root1.get("encouragementReviewDraft"), dto.getEncouragementReviewDraftNotSent())));
            }
            if (dto.getEncouragementReviewTypeList() != null) {

                spec = spec.and((root1, query1, criteriaBuilder1) -> {
                    Subquery<LocalDateTime> subQuery= query1.subquery(LocalDateTime.class);
                    Root<EncouragementReview> subRoot= subQuery.from(EncouragementReview.class);

                    subQuery.select(criteriaBuilder1.greatest(subRoot.get(EncouragementReview.Fields.encouragementReviewUpdatedAt).as(LocalDateTime.class))).where(
                            criteriaBuilder1.equal(subRoot.get(EncouragementReview.Fields.encouragementReviewEncouragementId), root1.get(EncouragementReview.Fields.encouragementReviewEncouragementId))
                    );

                    return criteriaBuilder1.and(
                            root1.get(EncouragementReview.Fields.encouragementReviewType).in(dto.getEncouragementReviewTypeList()),
                            criteriaBuilder1.equal(root1.get(EncouragementReview.Fields.encouragementReviewUpdatedAt), subQuery)
                    );
                });
            }
//            if (dto.getEncouragementReviewResultList() != null) {
//                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewResult").in(dto.getEncouragementReviewResultList()));
//            }
            if (dto.getEncouragementReviewDescription() != null && !dto.getEncouragementReviewDescription().isEmpty()) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.like(root1.get("encouragementReviewDescription"), "%" + dto.getEncouragementReviewDescription() + "%"));
            }
            if (dto.getEncouragementReviewCreatedAt() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewCreatedAt"), dto.getEncouragementReviewCreatedAt()));
            }
            if (dto.getEncouragementReviewAmount() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewAmount"), dto.getEncouragementReviewAmount()));
            }
            if (dto.getEncouragementReviewAmountType() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewAmountType"), dto.getEncouragementReviewAmountType()));
            }
            if (dto.getEncouragementReviewPercentage() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewPercentage"), dto.getEncouragementReviewPercentage()));
            }
            if (dto.getEncouragementReviewType() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewType"), dto.getEncouragementReviewType()));
            }
            if (dto.getEncouragementReviewUpdatedAt() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewUpdatedAt"), dto.getEncouragementReviewUpdatedAt()));
            }
            if (dto.getEncouragementReviewApprovalDate() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewApprovalDate"), dto.getEncouragementReviewApprovalDate()));
            }
            if (dto.getEncouragementReviewSentDraftDateFrom() != null) {
                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.greaterThanOrEqualTo(root1.get("encouragementReviewEncouragementCreatedDate"), dto.getEncouragementReviewSentDraftDateFrom().atStartOfDay()));
            }
            if (dto.getEncouragementReviewSentDraftDateTo() != null) {
                        spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.lessThanOrEqualTo(root1.get("encouragementReviewEncouragementCreatedDate"), dto.getEncouragementReviewSentDraftDateTo().atTime(LocalTime.MAX)));
            }
            if (dto.getEncouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId() != null) {

                spec = spec.and((root1, query1, criteriaBuilder1) -> {
                    Subquery<LocalDateTime> subQuery= query1.subquery(LocalDateTime.class);
                    Root<EncouragementReview> subRoot= subQuery.from(EncouragementReview.class);

                    Subquery<UUID> subQuery2= query1.subquery(UUID.class);
                    Root<Encouragement> subRoot2 = subQuery2.from(Encouragement.class);

                    subQuery2.select(subRoot2.get(Encouragement.Fields.encouragementId)).where(criteriaBuilder1.notEqual(subRoot2.get(Encouragement.Fields.encouragementRegistrarOrganizationId),
                            dto.getEncouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId()));

                    subQuery.select(criteriaBuilder1.greatest(subRoot.get(EncouragementReview.Fields.encouragementReviewUpdatedAt).as(LocalDateTime.class))).where(
                            criteriaBuilder1.equal(subRoot.get(EncouragementReview.Fields.encouragementReviewRegistrarOrganizationId), dto.getEncouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId()),
                            criteriaBuilder1.equal(subRoot.get(EncouragementReview.Fields.encouragementReviewEncouragementId), root1.get(EncouragementReview.Fields.encouragementReviewEncouragementId))
                    );

                    return criteriaBuilder1.and(
                            criteriaBuilder1.or(root1.get(EncouragementReview.Fields.encouragementReviewEncouragementId).in(subQuery2), criteriaBuilder1.equal(root1.get(EncouragementReview.Fields.encouragementReviewType), ReviewTypeEnum.ORDINARY_COMMISSION.getCode())),
                            criteriaBuilder1.equal(root1.get(EncouragementReview.Fields.encouragementReviewUpdatedAt), subQuery)
                    );
                });

//                spec = spec.and((root1, query1, criteriaBuilder1) -> criteriaBuilder1.equal(root1.get("encouragementReviewType"), ReviewTypeEnum.ORDINARY_COMMISSION.getCode()));

//                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementId").in(subquery));
//                spec = spec.and((root1, query1, criteriaBuilder1) -> criteriaBuilder1.or(root1.get("encouragementReviewEncouragementId").in(subquery), criteriaBuilder1.equal(root1.get("encouragementReviewType"), ReviewTypeEnum.ORDINARY_COMMISSION.getCode())));
//                spec = spec.and((root1, query1, criteriaBuilder1) -> criteriaBuilder1.equal(root1.get("encouragementReviewRegistrarOrganizationId"), dto.getEncouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId()));
            }
            if (dto.getEncouragementReviewEncouragedOrganizationId() != null) {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
                subquery.select(encouragementRoot.get("encouragementId"))
                        .where(criteriaBuilder.equal(encouragementRoot.get("encouragementPersonnelOrganizationId"), dto.getEncouragementReviewEncouragedOrganizationId()));

                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementId").in(subquery));
            }
            if (dto.getEncouragementReviewEncouragedOrganizationIdList() != null) {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
                subquery.select(encouragementRoot.get("encouragementId"))
                        .where(criteriaBuilder.in(encouragementRoot.get("encouragementPersonnelOrganizationId")).value(dto.getEncouragementReviewEncouragedOrganizationIdList()));

                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementId").in(subquery));
            }
            if (dto.getEncouragementReviewEncouragedRegistrarOrganizationId() != null) {

                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
                subquery.select(encouragementRoot.get("encouragementId"))
                        .where(criteriaBuilder.equal(encouragementRoot.get("encouragementRegistrarOrganizationId"), dto.getEncouragementReviewEncouragedRegistrarOrganizationId()));

                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementId").in(subquery));

            }
            if (dto.getEncouragementReviewEncouragedRegistrarOrganizationIdList() != null) {

                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
                subquery.select(encouragementRoot.get("encouragementId"))
                        .where(criteriaBuilder.in(encouragementRoot.get("encouragementRegistrarOrganizationId")).value(dto.getEncouragementReviewEncouragedRegistrarOrganizationIdList()));

                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementId").in(subquery));

            }
            if (dto.getEncouragementNumber() != null) {
                Subquery<UUID> subquery = query.subquery(UUID.class);
                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
                subquery.select(encouragementRoot.get("encouragementId"))
                        .where(criteriaBuilder.like(encouragementRoot.get("encouragementNumber"),"%" + dto.getEncouragementNumber().trim() + "%"));

                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewEncouragementId"), subquery));

//                return root.get("encouragementReviewRegistrarOrganizationId").in(subquery);
            }
            if (dto.getEncouragementAmount() != null) {
//                Subquery<UUID> subquery = query.subquery(UUID.class);
//                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
//                subquery.select(encouragementRoot.get("encouragementId"))
//                        .where(criteriaBuilder.equal(encouragementRoot.get("encouragementAmount"), dto.getEncouragementAmount()));

                spec = spec.and((root1, query1, criteriaBuilder1) ->
                        criteriaBuilder1.equal(root1.get("encouragementReviewEncouragementAmount"), dto.getEncouragementAmount()));

//                spec = spec.and((root1, query1, criteriaBuilder1) ->
//                        criteriaBuilder1.equal(root1.get("encouragementReviewEncouragementId"), subquery));
            }
            if (dto.getEncouragementTypeTitle() != null) {
                Subquery<UUID> typeSubquery = query.subquery(UUID.class);
                Root<EncouragementType> typeRoot = typeSubquery.from(EncouragementType.class);
                typeSubquery.select(typeRoot.get("encouragementTypeId"))
                        .where(criteriaBuilder.like(typeRoot.get("encouragementTypeTitle"), "%" + dto.getEncouragementTypeTitle() + "%"));

                Subquery<UUID> subquery1 = query.subquery(UUID.class);
                Root<EncouragementReasonType> encouragementRoot1 = subquery1.from(EncouragementReasonType.class);
                subquery1.select(encouragementRoot1.get("encouragementReasonTypeId"))
                        .where(encouragementRoot1.get("encouragementTypeId").in(typeSubquery));

//                Subquery<UUID> subquery = query.subquery(UUID.class);
//                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
//                subquery.select(encouragementRoot.get("encouragementId"))
//                        .where(encouragementRoot.get("encouragementReasonTypeId").in(subquery1));

                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementReasonTypeId").in(subquery1));

            }
            if (dto.getEncouragementReasonTitle() != null) {
                Subquery<UUID> typeSubquery = query.subquery(UUID.class);
                Root<EncouragementReason> typeRoot = typeSubquery.from(EncouragementReason.class);
                typeSubquery.select(typeRoot.get("encouragementReasonId"))
                        .where(criteriaBuilder.like(typeRoot.get("encouragementReasonTitle"), "%" + dto.getEncouragementReasonTitle() + "%"));

                Subquery<UUID> subquery1 = query.subquery(UUID.class);
                Root<EncouragementReasonType> encouragementRoot1 = subquery1.from(EncouragementReasonType.class);
                subquery1.select(encouragementRoot1.get("encouragementReasonTypeId"))
                        .where(encouragementRoot1.get("encouragementReasonId").in(typeSubquery));

//                Subquery<UUID> subquery = query.subquery(UUID.class);
//                Root<Encouragement> encouragementRoot = subquery.from(Encouragement.class);
//                subquery.select(encouragementRoot.get("encouragementId"))
//                        .where(encouragementRoot.get("encouragementReasonTypeId").in(subquery1));

                spec = spec.and((root1, query1, criteriaBuilder1) -> root1.get("encouragementReviewEncouragementReasonTypeId").in(subquery1));

            }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}
