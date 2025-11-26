package com.paya.EncouragementService.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paya.EncouragementService.dto.EncouragementReviewDTO;
import com.paya.EncouragementService.dto.EncouragementReviewSearchDTO;
import com.paya.EncouragementService.dto.UnSeenCountDTO;
import com.paya.EncouragementService.entity.EncouragementReview;
import com.paya.EncouragementService.service.EncouragementReviewService;
import com.paya.EncouragementService.service.EncouragementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/encouragement-review")
@AllArgsConstructor
public class EncouragementReviewController {


    private final EncouragementReviewService encouragementReviewService;
    private final EncouragementService encouragementService;


    @PostMapping("search")
    public ResponseEntity<Page<EncouragementReviewDTO>> getAllEncouragementReviews(
            EncouragementReviewSearchDTO dto,
            @RequestParam(required = false, defaultValue = "100") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber) throws Exception {
        try {
            if (dto.getEncouragementReasonTitle() != null)
                dto.setEncouragementReasonTitle(dto.getEncouragementReasonTitle().replace("ی", "ي").trim());
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(EncouragementReview.Fields.encouragementReviewUpdatedAt).descending());
            return ResponseEntity.ok().body(encouragementService.getAllEncouragementReviews(dto, pageRequest));
        }catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }


//    @PostMapping
//    public ResponseEntity<EncouragementReviewDTO> createEncouragementReview(@RequestBody EncouragementReviewDTO encouragementReviewDTO) {
//        EncouragementReviewDTO createdReview = encouragementReviewService.createEncouragementReview(encouragementReviewDTO);
//        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
//    }


    //    @PutMapping("/{id}")
//    public ResponseEntity<EncouragementReviewDTO> updateEncouragementReview(
//            @PathVariable UUID id,
//            @RequestBody EncouragementReviewDTO encouragementReviewDTO) {
//
//        EncouragementReviewDTO updatedReview = encouragementReviewService.updateEncouragementReview(id, encouragementReviewDTO);
//        return ResponseEntity.ok(updatedReview);
//    }
    @PatchMapping
    public ResponseEntity<String> updateEncouragementReview(@RequestPart("encouragementReview") String encouragementReviewDTO,
                                                                            @RequestPart(required = false , name = "files") List<MultipartFile> fileList,
                                                                            @RequestPart(required = false , name = "deleteFileIdList") String deleteFileIdList) throws Exception {
        try {
            List<String> finalDeleteFileIdList = null;
            if (deleteFileIdList != null)
                finalDeleteFileIdList = new ObjectMapper().readValue(deleteFileIdList, new TypeReference<>() {
                });
            encouragementService.updateEncouragementReview(encouragementReviewDTO, fileList, finalDeleteFileIdList);
            return ResponseEntity.ok("ویرایش با موفقیت انجام شد.");
        } catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncouragementReview(@PathVariable UUID id) throws Exception {
        try {
            encouragementReviewService.deleteEncouragementReview(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }

    @GetMapping("getCountOfEncouragementReviewIncoming")
    public ResponseEntity<UnSeenCountDTO> getCountOfEncouragementReviewIncoming(@RequestParam(required = false, defaultValue = "100") Integer pageSize,
                                                                                @RequestParam(required = false, defaultValue = "0") Integer pageNumber) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(EncouragementReview.Fields.encouragementReviewCreatedAt).descending());
            return ResponseEntity.ok().body(encouragementService.getCountOfEncouragementReviewIncoming(pageRequest));
        }catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }

}
