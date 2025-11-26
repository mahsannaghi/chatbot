package com.paya.EncouragementService.controller;

import com.paya.EncouragementService.dto.EncouragementReasonTypeDTO;
import com.paya.EncouragementService.dto.EncouragementRequestDTO;
import com.paya.EncouragementService.dto.ResponseDTO;
import com.paya.EncouragementService.dto.ResultDTO;
import com.paya.EncouragementService.dto.v2.ReasonTypeDTOV2;
import com.paya.EncouragementService.service.EncouragementReasonTypeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/encouragement-reason-types")
@Slf4j
public class EncouragementReasonTypeController {

    private final EncouragementReasonTypeService encouragementReasonTypeService;

    public EncouragementReasonTypeController(EncouragementReasonTypeService encouragementReasonTypeService) {
        this.encouragementReasonTypeService = encouragementReasonTypeService;
    }

    @GetMapping("/results")
    public ResponseEntity<List<ResultDTO>> getResults(
            @RequestParam(required = false) String reasonTitle,
            @RequestParam(required = false) String typeTitle,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String durationType,
            @RequestParam(required = false) Boolean isActive
    ) {
        if (reasonTitle != null)
            reasonTitle = reasonTitle.replace("ی", "ي").trim();
        if (typeTitle != null)
            typeTitle = typeTitle.replace("ی", "ي").trim();
        if (durationType != null)
            durationType = durationType.replace("ی", "ي").trim();
        List<ResultDTO> results = encouragementReasonTypeService.getResults(reasonTitle, typeTitle, maxAmount, maxDuration, durationType, isActive);
        return ResponseEntity.ok(results);
    }


    @PostMapping("/process")
    public ResponseEntity<?> processEncouragementRequest(@Valid @RequestBody EncouragementRequestDTO requestDTO) {
        ResponseDTO responseDTO = encouragementReasonTypeService.processEncouragementRequest(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }


    public ResponseEntity<List<EncouragementReasonTypeDTO>> searchByReasonAndTypeTitles(
            @RequestParam(required = false) String reasonTitle,
            @RequestParam(required = false) String typeTitle) {

        // If both reasonTitle and typeTitle are empty, return empty list
        if ((reasonTitle == null || reasonTitle.isEmpty()) && (typeTitle == null || typeTitle.isEmpty())) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<EncouragementReasonTypeDTO> results = encouragementReasonTypeService.searchByReasonAndTypeTitles(reasonTitle, typeTitle);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/getGroupedReasonTypes")
    public ResponseEntity<List<ReasonTypeDTOV2>> getGroupedReasonTypes(@RequestParam(required = false) String reasonTitle,
                                                                       @RequestParam(required = false) String typeTitle) throws Exception {
        try {
            return ResponseEntity.ok().body(encouragementReasonTypeService.getGroupedReasonTypes(reasonTitle, typeTitle));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


}
