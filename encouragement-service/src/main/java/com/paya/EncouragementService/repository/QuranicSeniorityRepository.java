package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.entity.QuranicSeniority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuranicSeniorityRepository extends JpaRepository<QuranicSeniority, UUID>, JpaSpecificationExecutor<QuranicSeniority>

{
    Optional<QuranicSeniority> findByQuranicSeniorityTypeAndQuranicSeniorityAmount(String type, String amount);

    List<QuranicSeniority> findAll();
}


