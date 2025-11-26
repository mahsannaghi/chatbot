package com.paya.EncouragementService.controller;


import com.paya.EncouragementService.dto.FileAttachmentDTO;
import com.paya.EncouragementService.dto.v2.AttachmentGetDTO;
import com.paya.EncouragementService.entity.Attachment;
import com.paya.EncouragementService.service.v2.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("file")
@Slf4j
@PreAuthorize("hasAnyRole('EXECUTIVE_MANAGER', 'ENCOURAGEMENT_SPECIALIST')")
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("upload")
    public ResponseEntity<Attachment> uploadFile(@RequestPart MultipartFile file) throws Exception {
        try {

            return ResponseEntity.ok().body(attachmentService.addAttachment(file));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    @GetMapping("download")
    public ResponseEntity<Attachment> getFile(@RequestParam String fileId) throws Exception {
        try {
            Attachment attachment = attachmentService.getAttachmentById(UUID.fromString(fileId));
            String fileName = attachment.getAttachmentName();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(attachment);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("خطا در دانلود فایل ");
        }
    }

    @GetMapping("quranicEncouragementFiles/{quranicEncouragementId}")
    public ResponseEntity<List<Attachment>> getAllQuranicEncouragementFiles(@PathVariable String quranicEncouragementId,
                                                                                  @RequestParam(defaultValue = "5") Integer pageSize,
                                                                                  @RequestParam(defaultValue = "0") Integer pageNumber) throws Exception {
        try {
            return ResponseEntity.ok().body(attachmentService.getAllEncouragementAttachments(PageRequest.of(pageSize, pageNumber), FileAttachmentDTO.builder().quranicEncouragementId(UUID.fromString(quranicEncouragementId)).build(), false));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("gradeEncouragementFiles/{gradeEncouragementId}")
    public ResponseEntity<List<Attachment>> getAllGradeEncouragementFiles(@PathVariable String gradeEncouragementId,
                                                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                                                @RequestParam(defaultValue = "0") Integer pageNumber) throws Exception {
        try {
            return ResponseEntity.ok().body(attachmentService.getAllEncouragementAttachments(PageRequest.of(pageSize, pageNumber), FileAttachmentDTO.builder().gradeEncouragementId(UUID.fromString(gradeEncouragementId)).build(), false));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("encouragementReviewFiles/{encouragementReviewId}")
    public ResponseEntity<List<Attachment>> getAllEncouragementReviewFiles(@PathVariable String encouragementReviewId,
                                                                           @RequestParam(defaultValue = "5") Integer pageSize,
                                                                           @RequestParam(defaultValue = "0") Integer pageNumber) throws Exception {
        try {
            return ResponseEntity.ok().body(attachmentService.getAllEncouragementAttachments(PageRequest.of(pageSize, pageNumber), FileAttachmentDTO.builder().encouragementReviewId(UUID.fromString(encouragementReviewId)).build(), false));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<AttachmentGetDTO>> getAllQuranicEncouragementFilesWithId(@RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNumber) throws Exception {
        try {
            return ResponseEntity.ok().body(attachmentService.getAllAttachmentsWithoutFileContent(PageRequest.of(pageSize, pageNumber)));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    @DeleteMapping
    public ResponseEntity<Boolean> deleteFile(@RequestParam String fileId) throws Exception {
        try {
            return ResponseEntity.ok().body(attachmentService.deleteAttachment(UUID.fromString(fileId)));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

}
