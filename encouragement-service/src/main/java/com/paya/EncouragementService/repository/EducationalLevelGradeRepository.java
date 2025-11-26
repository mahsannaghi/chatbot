package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.EducationalLevelGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EducationalLevelGradeRepository extends JpaRepository<EducationalLevelGrade, UUID>, JpaSpecificationExecutor<EducationalLevelGrade> {

    Optional<EducationalLevelGrade> findByEducationalLevelGradeDegree(Integer educationalLevelGradeDegree);

    @Query("select elg from EducationalLevelGrade elg where elg.educationalLevelGradeDegree =:degree order by elg.educationalLevelGradeCreationDate desc limit 1 ")
    EducationalLevelGrade findByEducationalLevelGradeDegreeOrderByCreationDateDesc(Integer degree);
//    EducationalLevelGrade findByEducationalLevelGradeDegree(Integer level)
}
