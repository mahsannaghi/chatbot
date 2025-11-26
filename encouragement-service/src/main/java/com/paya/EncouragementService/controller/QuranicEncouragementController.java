package com.paya.EncouragementService.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paya.EncouragementService.dto.v2.QuranicEncouragementDTOV2;
import com.paya.EncouragementService.dto.v2.QuranicEncouragementFilterDTOV2;
import com.paya.EncouragementService.entity.QuranicEncouragement;
import com.paya.EncouragementService.service.QuranicEncouragementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quranic-encouragement")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
public class QuranicEncouragementController {

    @Autowired
    private QuranicEncouragementService service;


    @PostMapping
    public QuranicEncouragement createQuranicEncouragement(@RequestPart(name = "data") String dto,
                                                           @RequestPart(required = false, name = "files") List<MultipartFile> files) throws Exception {
        return service.createOrUpdateQuranicEncouragement(dto, files, null, true);
    }

    @PatchMapping
    public QuranicEncouragement updateQuranicEncouragement(@RequestPart("data") String dto,
                                                           @RequestPart(required = false, name = "files")  List<MultipartFile> files,
                                                           @RequestPart(required = false , name = "deleteFileIdList") String deleteFileIdList) throws Exception {
        List<String> finalDeleteFileIdList= null;
        if (deleteFileIdList != null)
            finalDeleteFileIdList = new ObjectMapper().readValue(deleteFileIdList, new TypeReference<>() {});
        return service.createOrUpdateQuranicEncouragement(dto, files, finalDeleteFileIdList, false);
    }

    @GetMapping("/{id}")
    public QuranicEncouragement getQuranicEncouragementById(@PathVariable UUID id) {
        return service.getQuranicEncouragementById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quranic Encouragement not found"));
    }

    @GetMapping("getQuranicEncouragementAmount/{id}")
    public Integer getQuranicEncouragementAmount(@PathVariable UUID id) {
        return service.getQuranicEncouragementAmountByPersonnelOrganizationId(id);
    }


    @DeleteMapping("/{id}")
    public void deleteQuranicEncouragement(@PathVariable UUID id) {
        service.deleteQuranicEncouragement(id);
    }

    //
//    @GetMapping
//    public ResponseEntity<Page<QuranicEncouragementDTOV2>> getQuranicEncouragements(@RequestParam(required = false, defaultValue = "5") Integer pageSize,
//                                                                                    @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
//                                                                                    @RequestParam(required = false) String fromDate,
//                                                                                    @RequestParam(required = false) String toDate,
//                                                                                    @RequestParam(required = false) String quranicSeniorityType,
//                                                                                    @RequestParam(required = false) Integer quranicEncouragementAmount,
//                                                                                    @RequestParam(required = false) String quranicSeniorityAmount
//    ) throws ExecutionException, InterruptedException {
//        Page<QuranicEncouragementDTOV2> res = service.getList(PageRequest.of(pageNumber, pageSize), fromDate, toDate, quranicSeniorityType, quranicEncouragementAmount, quranicSeniorityAmount);
//        return ResponseEntity.ok(res);
//    }
    @PostMapping("/filter")
    public ResponseEntity<Page<QuranicEncouragementDTOV2>> getQuranicEncouragements(@RequestBody QuranicEncouragementFilterDTOV2 quranicEncouragementFilterDTOV2,
                                                                                    @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                                                                    @RequestParam(required = false, defaultValue = "0") Integer pageNumber
    ) throws Exception {
        if (quranicEncouragementFilterDTOV2.getQuranicSeniorityType() != null)
            quranicEncouragementFilterDTOV2.setQuranicSeniorityType(quranicEncouragementFilterDTOV2.getQuranicSeniorityType().replace("ی", "ي").trim());
        Page<QuranicEncouragementDTOV2> res = service.getList(quranicEncouragementFilterDTOV2, pageSize, pageNumber);
        return ResponseEntity.ok(res);
    }

}
