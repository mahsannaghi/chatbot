package com.paya.EncouragementService.repository.v2.impl;


import com.paya.EncouragementService.dto.v2.GradeEncouragementDTOV2;
import com.paya.EncouragementService.repository.v2.GradeEncouragementDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GradeEncouragementDAOImpl implements GradeEncouragementDAO {

    private final EntityManager entityManager;

    public GradeEncouragementDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<GradeEncouragementDTOV2> getList(Pageable pageable, LocalDate fromDate, LocalDate toDate, Integer gradeEncouragementNewGrade, Integer gradeEncouragementSeniorityAmount) {
        StringBuilder query = new StringBuilder("select new com.paya.EncouragementService.dto.v2.GradeEncouragementDTOV2(" +
                "ge.gradeEncouragementId ," +
                "ge.gradeEncouragementCreatedAt ," +
                "ge.gradeEncouragementNewGrade ," +
                "ge.gradeEncouragementEffectiveDate ," +
                "ge.gradeEncouragementRelatedPersonnelId,  " +
                "ge.gradeEncouragementSeniorityAmount  " +
                ") " +
                " from GradeEncouragement ge where 1=1 ");
        HashMap<String, Object> parameters = new HashMap<>();

        if (fromDate != null) {
            query.append(" and ge.gradeEncouragementEffectiveDate >= :fromDate ");
            parameters.put("fromDate", fromDate);
        }
        if (toDate != null) {
            query.append(" and ge.gradeEncouragementEffectiveDate <= :toDate ");
            parameters.put("toDate", toDate);
        }
        if (gradeEncouragementNewGrade != null) {
            query.append(" and ge.gradeEncouragementNewGrade = :gradeEncouragementNewGrade ");
            parameters.put("gradeEncouragementNewGrade", gradeEncouragementNewGrade);
        }
        if (gradeEncouragementSeniorityAmount != null) {
            query.append(" and ge.gradeEncouragementSeniorityAmount = :gradeEncouragementSeniorityAmount ");
            parameters.put("gradeEncouragementSeniorityAmount", gradeEncouragementSeniorityAmount);
        }
        query.append(" order by ge.gradeEncouragementCreatedAt desc");

        Query finalQuery = entityManager.createQuery(query.toString());

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            finalQuery.setParameter(entry.getKey(), entry.getValue());
        }
        List<GradeEncouragementDTOV2> result = (List<GradeEncouragementDTOV2>) finalQuery
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        Long totalRows = (long) result.size();
        return new PageImpl<>(result, pageable, totalRows);
    }
}
