package com.paya.EncouragementService.service.v2;


import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.FileAttachmentDTO;
import com.paya.EncouragementService.dto.v2.AttachmentGetDTO;
import com.paya.EncouragementService.entity.Attachment;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface AttachmentService {
    Attachment addAttachment(MultipartFile multipartFile) throws Exception;

    List<MultipartFile> addAllAttachments(List<MultipartFile> fileList) throws Exception;

//    List<Attachment> addAttachmentForQuranicEncouragement(List<MultipartFile> fileList, UUID quranicEncouragementId) throws Exception;

    List<Attachment> addAttachmentForEncouragement(List<MultipartFile> fileList, FileAttachmentDTO gradeEncouragementId) throws Exception;

//    List<Attachment> addAttachmentForEncouragementReview(List<MultipartFile> fileList, UUID encouragementReviewId) throws Exception;

    Boolean deleteAttachment(UUID id) throws Exception;

    Attachment getAttachmentById(UUID id) throws Exception;

    Boolean checkExistsInQuranicEncouragement(UUID attachmentId) throws Exception;

    Boolean checkExistsInGradeEncouragement(UUID attachmentId) throws Exception;

    List<AttachmentGetDTO> getAllAttachmentsWithoutFileContent(Pageable pageable);
    List<Attachment> getAllEncouragementAttachments(Pageable pageable, FileAttachmentDTO fileAttachmentDTO, boolean b);

    void deleteAttachmentFile(UUID id) throws GeneralException;
}
