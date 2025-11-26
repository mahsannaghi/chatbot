package com.paya.EncouragementService.service.v2.impl;


import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.FileAttachmentDTO;
import com.paya.EncouragementService.dto.v2.AttachmentGetDTO;
import com.paya.EncouragementService.entity.Attachment;
import com.paya.EncouragementService.mapper.AttachmentMapper;
import com.paya.EncouragementService.repository.AttachmentRepository;
import com.paya.EncouragementService.service.v2.AttachmentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, AttachmentMapper attachmentMapper) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
    }


    @Override
    @Transactional
    public Attachment addAttachment(MultipartFile multipartFile) throws Exception {
        if (multipartFile != null) {
            Attachment entity = new Attachment();
            entity.setAttachmentName(multipartFile.getOriginalFilename());
            entity.setAttachmentFile(multipartFile.getBytes());
            entity.setAttachmentFile(Base64.getEncoder().encode(multipartFile.getBytes()));
            return attachmentRepository.save(entity);
        } else throw new Exception("پارامتر ورودی صحیح نمی باشد .");
    }


    @Override
    @Transactional
    public List<MultipartFile> addAllAttachments(List<MultipartFile> fileList) throws Exception {
        if (fileList != null) {
            Attachment entity = new Attachment();
            for (MultipartFile file : fileList) {
                entity.setAttachmentName(file.getOriginalFilename());
                entity.setAttachmentFile(Base64.getEncoder().encode(file.getBytes()));
                attachmentRepository.save(entity);
            }
            return fileList;
        } else throw new Exception("پارامتر ورودی صحیح نمی باشد .");
    }

    @Override
    @Transactional
    public List<Attachment> addAttachmentForEncouragement(List<MultipartFile> fileList, FileAttachmentDTO fileAttachmentDTO) throws Exception {
        if (fileList != null && fileAttachmentDTO != null) {
            List<Attachment> attachmentList = new ArrayList<>();
            for (MultipartFile file : fileList) {
                Attachment entity = new Attachment();
                if (fileAttachmentDTO.getGradeEncouragementId() != null)
                    entity.setAttachmentGradeEncouragementId(fileAttachmentDTO.getGradeEncouragementId());
                if (fileAttachmentDTO.getQuranicEncouragementId() != null)
                    entity.setAttachmentQuranicEncouragementId(fileAttachmentDTO.getQuranicEncouragementId());
                if (fileAttachmentDTO.getEncouragementReviewId() != null)
                    entity.setAttachmentEncouragementReviewId(fileAttachmentDTO.getEncouragementReviewId());
                if (fileAttachmentDTO.getEncouragementId() != null)
                    entity.setAttachmentEncouragementReviewId(fileAttachmentDTO.getEncouragementId());
                entity.setAttachmentName(file.getOriginalFilename());
                entity.setAttachmentFile(file.getBytes());
                attachmentList.add(entity);
            }
            attachmentRepository.saveAll(attachmentList);

            return attachmentList;

        } else throw new Exception("پارامتر های ورودی صحیح نمی باشند . ");
    }

    @Override
    @Transactional
    public Boolean deleteAttachment(UUID id) throws Exception {
        if (id != null) {
            if (!this.checkExistsInGradeEncouragement(id) && !this.checkExistsInQuranicEncouragement(id)) {
                attachmentRepository.deleteById(id);
                return true;
            } else return false;
        } else throw new Exception("پارامتر ورودی صحیح نمی باشد .");
    }

    @Override
    public Attachment getAttachmentById(UUID id) throws Exception {
        if (id != null) {
            Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new Exception("فایل مورد نظر یافت نشد ."));
            attachment.setAttachmentFile(Base64.getDecoder().decode(attachment.getAttachmentFile()));
            return attachment;
        } else throw new Exception("پارامتر ورودی صحیح نمی باشد .");
    }

    @Override
    public Boolean checkExistsInQuranicEncouragement(UUID attachmentId) throws Exception {
        if (attachmentId != null) {
            if (!attachmentRepository.existsInQuranicEncouragement(attachmentId)) {
                return false;
            } else
                throw new Exception("این فایل قبلا در قسمت تشویقات قرآنی استفاده شده است و امکان حذف و ویرایش ندارد .");
        } else throw new Exception("پارامتر ورودی صحیح نمی باشد .");

    }

    @Override
    public Boolean checkExistsInGradeEncouragement(UUID attachmentId) throws Exception {
        if (attachmentId != null) {
            if (!attachmentRepository.existsInGradeEncouragement(attachmentId)) {
                return false;
            } else
                throw new Exception("این فایل قبلا در قسمت تشویقات تحصیلی استفاده شده است و امکان حذف و ویرایش ندارد .");
        } else throw new Exception("پارامتر ورودی صحیح نمی باشد .");

    }

    @Override
    public List<AttachmentGetDTO> getAllAttachmentsWithoutFileContent(Pageable pageable) {
        return attachmentRepository.getAllAttachmentsWithoutFileContent(pageable);
    }

    @Override
    public List<Attachment> getAllEncouragementAttachments(Pageable pageable, FileAttachmentDTO fileAttachmentDTO, boolean isDeleteMode) {
        List<Attachment> attachmentList = null;
        if (fileAttachmentDTO.getGradeEncouragementId() != null)
            attachmentList = attachmentRepository.getAllGradeEncouragementAttachmentsWithId(pageable, fileAttachmentDTO.getGradeEncouragementId());
        if (fileAttachmentDTO.getQuranicEncouragementId() != null)
            attachmentList = attachmentRepository.getAllQuranicEncouragementAttachmentsWithId(pageable, fileAttachmentDTO.getQuranicEncouragementId());
        if (fileAttachmentDTO.getEncouragementReviewId() != null)
            attachmentList = attachmentRepository.getAllEncouragementReviewAttachmentWithId(pageable, fileAttachmentDTO.getEncouragementReviewId());
        if (fileAttachmentDTO.getEncouragementId() != null)
            attachmentList = attachmentRepository.getAllEncouragementReviewAttachmentWithId(pageable, fileAttachmentDTO.getEncouragementId());
        if (attachmentList != null && !attachmentList.isEmpty() && !isDeleteMode) {
            attachmentList.forEach(file -> {
                file.setAttachmentFile(file.getAttachmentFile());
                file.setAttachmentFileSize(file.getAttachmentFile() != null ? file.getAttachmentFile().length / (1024.0 * 1024.0) : 0.0);
            });
        }
        return attachmentList;
    }

    @Override
    public void deleteAttachmentFile(UUID id) throws GeneralException {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(() -> new GeneralException("فایل مورد نظر یافت نشد ."));
        attachmentRepository.delete(attachment);

    }
}
