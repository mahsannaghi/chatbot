package com.paya.EncouragementService.repository.v2;

import com.paya.EncouragementService.entity.Encouragement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EncouragementDAO {
    Page<Encouragement> getList(Pageable pageable , String encouragementNumber , Long encouragementAmount , LocalDate fromDate , LocalDate toDate);
}
