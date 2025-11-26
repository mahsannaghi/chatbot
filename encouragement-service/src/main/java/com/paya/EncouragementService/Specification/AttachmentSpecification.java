package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.Attachment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.UUID;


public class AttachmentSpecification {


    public static Specification<Attachment> attachmentFileContains(String file) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(file)) {
                return criteriaBuilder.like(root.get("attachmentFile"), "%" + file + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }


    public static Specification<Attachment> attachmentNameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(name)) {
                return criteriaBuilder.like(root.get("attachmentName"), "%" + name + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }

    // فیلتر بر اساس attachmentQuranicEncouragementId
    public static Specification<Attachment> attachmentQuranicEncouragementIdEquals(UUID quranicEncouragementId) {
        return (root, query, criteriaBuilder) -> {
            if (quranicEncouragementId != null) {
                return criteriaBuilder.equal(root.get("attachmentQuranicEncouragementId"), quranicEncouragementId);
            }
            return criteriaBuilder.conjunction();
        };
    }

    // فیلتر بر اساس attachmentGradeEncouragementId
    public static Specification<Attachment> attachmentGradeEncouragementIdEquals(UUID gradeEncouragementId) {
        return (root, query, criteriaBuilder) -> {
            if (gradeEncouragementId != null) {
                return criteriaBuilder.equal(root.get("attachmentGradeEncouragementId"), gradeEncouragementId);
            }
            return criteriaBuilder.conjunction();
        };
    }
}
