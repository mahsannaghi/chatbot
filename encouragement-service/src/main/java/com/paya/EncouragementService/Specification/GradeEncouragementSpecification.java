package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.GradeEncouragement;
import org.springframework.data.jpa.domain.Specification;

public class GradeEncouragementSpecification {

    public static Specification<GradeEncouragement> filterByNewGrade(Integer newGrade) {
        return (root, query, criteriaBuilder) ->
                newGrade == null ? null : criteriaBuilder.equal(root.get("gradeEncouragementNewGrade"), newGrade);
    }

    public static Specification<GradeEncouragement> filterByRegistrarPersonnelId(String registrarPersonnelId) {
        return (root, query, criteriaBuilder) ->
                registrarPersonnelId == null ? null : criteriaBuilder.equal(root.get("gradeEncouragementRegistrarPersonnelId"), registrarPersonnelId);
    }

    public static Specification<GradeEncouragement> filterByRelatedPersonnelId(String relatedPersonnelId) {
        return (root, query, criteriaBuilder) ->
                relatedPersonnelId == null ? null : criteriaBuilder.equal(root.get("gradeEncouragementRelatedPersonnelId"), relatedPersonnelId);
    }

    public static Specification<GradeEncouragement> filterByCreatedAt(java.sql.Date createdAt) {
        return (root, query, criteriaBuilder) ->
                createdAt == null ? null : criteriaBuilder.equal(root.get("gradeEncouragementCreatedAt"), createdAt);
    }

    public static Specification<GradeEncouragement> filterByUpdatedAt(java.sql.Date updatedAt) {
        return (root, query, criteriaBuilder) ->
                updatedAt == null ? null : criteriaBuilder.equal(root.get("gradeEncouragementUpdatedAt"), updatedAt);
    }
}
