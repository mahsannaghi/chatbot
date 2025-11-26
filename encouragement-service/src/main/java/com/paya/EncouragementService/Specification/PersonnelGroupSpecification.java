package com.paya.EncouragementService.Specification;

import com.paya.EncouragementService.dto.PersonnelGroupDTO;
import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.entity.PersonnelGroup;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersonnelGroupSpecification {

    public static Specification<PersonnelGroup> findBySpecification(PersonnelGroupDTO dto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getPersonnelGroupName() != null && !dto.getPersonnelGroupName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get(PersonnelGroupDTO.Fields.personnelGroupName), "%" + dto.getPersonnelGroupName() + "%"));
            }
            if (dto.getPersonnelGroupActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), dto.getPersonnelGroupActive()));
            }
            if (dto.getPersonnelGroupOrgIdList() != null && !dto.getPersonnelGroupOrgIdList().isEmpty()) {
                Join<PersonnelGroup, String> join = root.join(PersonnelGroupDTO.Fields.personnelGroupOrgIdList);
                predicates.add(join.in(dto.getPersonnelGroupOrgIdList()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}