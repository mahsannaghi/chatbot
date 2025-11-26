package com.paya.EncouragementService.repository.v2;

import com.paya.EncouragementService.dto.v2.GradeEncouragementDTOV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface GradeEncouragementDAO {
    Page<GradeEncouragementDTOV2> getList(Pageable pageable, LocalDate fromDate, LocalDate toDate, Integer gradeEncouragementNewGrade, Integer gradeEncouragementSeniorityAmount);

}
