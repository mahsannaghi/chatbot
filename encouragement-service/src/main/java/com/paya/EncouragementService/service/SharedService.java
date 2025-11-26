package com.paya.EncouragementService.service;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.controller.ErrorResponse;
import com.paya.EncouragementService.repository.EncouragementRepository;
import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharedService {

    private final EncouragementRepository encouragementRepository;
    private final EncouragementReasonTypeRepository reasonTypeRepository;

    public ResponseEntity<ErrorResponse> checkIfReasonTypeUsedInEncouragement(UUID encouragementReasonTypeEncouragementTypeId) throws GeneralException {

        if (encouragementReasonTypeEncouragementTypeId == null) {
            throw new GeneralException("نوع دلیل معتبر نیست");
        }

        UUID reasonTypeId = reasonTypeRepository.findEncouragementReasonTypeIdByEncouragementTypeId(encouragementReasonTypeEncouragementTypeId);
        if (reasonTypeId == null) {
        throw new GeneralException("نوع دلیل یافت نشد");
        }

        boolean isUsed = encouragementRepository.existsByEncouragementReasonTypeId(reasonTypeId);
        if (isUsed) {
            throw new GeneralException("این نوع و علت در سیستم استفاده شده است");
        }

        return ResponseEntity.ok().build();
    }


}
