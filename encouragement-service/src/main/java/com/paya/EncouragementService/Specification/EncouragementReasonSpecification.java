package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementType;
import org.springframework.data.jpa.domain.Specification;

public class EncouragementReasonSpecification {

    public static Specification<EncouragementReason> hasTitle(String title) {
        return (root, query, cb) -> title != null && !title.trim().isEmpty()
                ? cb.like(cb.lower(root.get("encouragementReasonTitle")), "%" + title.trim().toLowerCase() + "%")
                : null;
    }

    public static Specification<EncouragementReason> isActive(Boolean isActive) {
        return (root, query, cb) -> isActive != null
                ? cb.equal(root.get("encouragementReasonIsActive"), isActive)
                : null;
    }
}