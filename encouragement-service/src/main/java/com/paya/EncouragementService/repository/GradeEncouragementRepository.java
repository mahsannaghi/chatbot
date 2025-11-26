package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.GradeEncouragement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GradeEncouragementRepository extends JpaRepository<GradeEncouragement, UUID>, JpaSpecificationExecutor<GradeEncouragement> {
}