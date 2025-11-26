package com.paya.EncouragementService.repository.v2.impl;


import com.paya.EncouragementService.entity.Encouragement;
import com.paya.EncouragementService.repository.v2.EncouragementDAO;
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
public class EncouragementDAOImpl implements EncouragementDAO {
    private final EntityManager entityManager;

    public EncouragementDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<Encouragement> getList(Pageable pageable, String encouragementNumber, Long encouragementAmount, LocalDate fromDate, LocalDate toDate) {
        HashMap<String, Object> parameters = new HashMap<>();
        StringBuilder initialQuery = new StringBuilder("select e from Encouragement e where 1=1");
        if (encouragementNumber != null) {
            initialQuery.append(" and e.encouragementNumber like :encouragementNumber");
            parameters.put("encouragementNumber", "%" + encouragementNumber + "%");
        }
        if (encouragementAmount != null) {
            initialQuery.append(" and e.encouragementAmount = :encouragementAmount ");
            parameters.put("encouragementAmount", +encouragementAmount);
        }
        if (fromDate != null) {
            initialQuery.append(" and e.encouragementCreatedAt >= :fromDate ");
            parameters.put("fromDate", fromDate);
        }
        if (toDate != null) {
            initialQuery.append(" and e.encouragementCreatedAt <= :toDate ");
            parameters.put("toDate", toDate);
        }
        initialQuery.append(" order by e.encouragementCreatedAt desc");

        Query finalQuery = entityManager.createQuery(initialQuery.toString());
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            finalQuery.setParameter(entry.getKey(), entry.getValue());
        }
        List<Encouragement> finalResult = (List<Encouragement>) finalQuery
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(finalResult, pageable, finalResult.size());
    }
}
