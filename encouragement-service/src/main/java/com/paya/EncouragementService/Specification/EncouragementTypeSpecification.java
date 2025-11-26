package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.entity.EncouragementType;
import org.springframework.data.jpa.domain.Specification;

public class EncouragementTypeSpecification {

    public static Specification<EncouragementType> hasTitle(String title) {
        return (root, query, cb) -> title != null ? cb.like(cb.lower(root.get("encouragementTypeTitle")), "%" + title.toLowerCase() + "%") : null;
    }

    public static Specification<EncouragementType> isActive(Boolean active) {
        return (root, query, cb) -> active != null ? cb.equal(root.get("encouragementTypeIsActive"), active) : null;
    }

    public static Specification<EncouragementType> hasNatureType(Integer natureType) {
        return (root, query, cb) -> natureType != null ? cb.equal(root.get("encouragementTypeNatureType"), natureType) : null;
    }
}
