package com.paya.EncouragementService.repository.v2;


import com.paya.EncouragementService.dto.v2.QuranicEncouragementDTOV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface QuranicEncouragementDAO {
    Page<QuranicEncouragementDTOV2> getList(Pageable pageable, LocalDate fromDate, LocalDate toDate, String quranicSeniorityType, Integer quranicEncouragementAmount, String quranicSeniorityAmount);

}
