package com.paya.EncouragementService.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paya.EncouragementService.dto.v2.GradeEncouragementDTOV2;
import com.paya.EncouragementService.dto.v2.GradeEncouragementFilterDTOV2;
import com.paya.EncouragementService.entity.GradeEncouragement;
import com.paya.EncouragementService.service.GradeEncouragementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grade-encouragements")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
public class GradeEncouragementController {

    @Autowired
    private GradeEncouragementService service;


    @PostMapping
    public GradeEncouragement createGradeEncouragement(@RequestPart("data") String dto,
                                                       @RequestPart(required = false, name = "files")  List<MultipartFile> files) throws Exception {
        return service.createOrUpdateGradeEncouragement(dto, files, null);
    }

    @PatchMapping
    public GradeEncouragement updateGradeEncouragement(@RequestPart("data") String dto,
                                                       @RequestPart(required = false, name = "files")  List<MultipartFile> files,
                                                       @RequestPart(required = false , name = "deleteFileIdList") String deleteFileIdList) throws Exception {
        List<String> finalDeleteFileIdList= null;
        if (deleteFileIdList != null)
            finalDeleteFileIdList = new ObjectMapper().readValue(deleteFileIdList, new TypeReference<>() {});
        return service.createOrUpdateGradeEncouragement(dto, files, finalDeleteFileIdList);
    }


    @GetMapping("/{id}")
    public GradeEncouragement getGradeEncouragement(@PathVariable UUID id) {
        return service.getGradeEncouragementById(id)
                .orElseThrow(() -> new IllegalArgumentException("GradeEncouragement not found"));
    }


    //    @GetMapping
//    public Page<GradeEncouragementDTOV2> getGradeEncouragements(@RequestParam(required = false, defaultValue = "5") Integer pageSize,
//                                                                @RequestParam(required = false, defaultValue = "0") Integer pageNumber) throws ExecutionException, InterruptedException {
//        return service.getList(PageRequest.of(pageNumber, pageSize));
//    }
    @PostMapping("/filter")
    public Page<GradeEncouragementDTOV2> getGradeEncouragements(
            @RequestBody GradeEncouragementFilterDTOV2 filterDTO,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) throws Exception {
        return service.getList(filterDTO, pageSize, pageNumber);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGradeEncouragement(@PathVariable UUID id) {
        service.deleteGradeEncouragement(id);
        return ResponseEntity.status(HttpStatus.OK.value()).body("با موفقیت حذف شد.");
    }


}
