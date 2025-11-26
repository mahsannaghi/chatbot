package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.QuranicEncouragement;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class QuranicEncouragementSpecification {

    public static Specification<QuranicEncouragement> filterBySeniorityId(String seniorityId) {
        return (root, query, criteriaBuilder) ->
                seniorityId == null ? null : criteriaBuilder.equal(root.get("quranicSeniorityId"), seniorityId);
    }

    public static Specification<QuranicEncouragement> filterByRegistrarId(UUID registrarId) {
        return (root, query, criteriaBuilder) ->
                registrarId == null ? null : criteriaBuilder.equal(root.get("registrarPersonnelId"), registrarId);
    }

    public static Specification<QuranicEncouragement> filterByRelatedPersonnelId(UUID relatedPersonnelId) {
        return (root, query, criteriaBuilder) ->
                relatedPersonnelId == null ? null : criteriaBuilder.equal(root.get("relatedPersonnelId"), relatedPersonnelId);
    }

    public static Specification<QuranicEncouragement> filterByAmount(Integer amount) {
        return (root, query, criteriaBuilder) ->
                amount == null ? null : criteriaBuilder.equal(root.get("amount"), amount);
    }

    public static Specification<QuranicEncouragement> filterByCreatedAt(java.sql.Date createdAt) {
        return (root, query, criteriaBuilder) ->
                createdAt == null ? null : criteriaBuilder.equal(root.get("createdAt"), createdAt);
    }
}
