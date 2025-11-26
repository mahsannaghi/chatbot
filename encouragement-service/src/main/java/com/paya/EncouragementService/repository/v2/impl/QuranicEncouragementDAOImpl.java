package com.paya.EncouragementService.repository.v2.impl;


import com.paya.EncouragementService.dto.v2.QuranicEncouragementDTOV2;
import com.paya.EncouragementService.repository.v2.QuranicEncouragementDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class QuranicEncouragementDAOImpl implements QuranicEncouragementDAO {
    private final EntityManager entityManager;

    public QuranicEncouragementDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public Page<QuranicEncouragementDTOV2> getList(Pageable pageable, LocalDate fromDate, LocalDate toDate, String quranicSeniorityType, Integer quranicEncouragementAmount, String quranicSeniorityAmount) {
        StringBuilder query = new StringBuilder("select new com.paya.EncouragementService.dto.v2.QuranicEncouragementDTOV2(" +
                "qe.quranicEncouragementId , " +
                "qs.quranicSeniorityType , " +
                "qs.quranicSeniorityAmount ," +
                " qe.amount , " +
                "qe.createdAt , " +
                "qe.quranicEncouragementEffectiveDate , " +
                "qe.relatedPersonnelId , " +
                "qe.quranicSeniorityId " +
                ")" +
                " from QuranicSeniority qs join QuranicEncouragement qe on qe.quranicSeniorityId = qs.quranicSeniorityId where 1=1 ");

        HashMap<String, Object> parameters = new HashMap<>();


        if (fromDate != null) {
            query.append("and qe.quranicEncouragementEffectiveDate >= :fromDate ");
            parameters.put("fromDate", fromDate);
        }
        if (toDate != null) {
            query.append("and qe.quranicEncouragementEffectiveDate <= :toDate ");
            parameters.put("toDate", toDate);
        }
        if (quranicSeniorityType != null) {
            query.append("and qs.quranicSeniorityType like :quranicSeniorityType ");
            parameters.put("quranicSeniorityType", "%" + quranicSeniorityType + "%");

        }
        if (quranicSeniorityAmount != null) {
            query.append("and qs.quranicSeniorityAmount like :quranicSeniorityAmount ");
            parameters.put("quranicSeniorityAmount", "%" + quranicSeniorityAmount + "%");
        }
        if (quranicEncouragementAmount != null) {
            query.append("and qe.amount = :quranicEncouragementAmount ");
            parameters.put("quranicEncouragementAmount",quranicEncouragementAmount);
        }
        query.append("order by qe.createdAt desc");
        Query finalQuery = entityManager.createQuery(query.toString());
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            finalQuery.setParameter(entry.getKey(), entry.getValue());
        }
        List<QuranicEncouragementDTOV2> resultList = (List<QuranicEncouragementDTOV2>) finalQuery
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        Long total = (long) resultList.size();
        return new PageImpl<>(resultList, pageable, total);
    }
}
