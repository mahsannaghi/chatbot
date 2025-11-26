//package com.paya.EncouragementService.service;
//
//import com.paya.EncouragementService.Specification.AttachmentSpecification;
//import com.paya.EncouragementService.dto.AttachmentDTO;
//import com.paya.EncouragementService.entity.Attachment;
//import com.paya.EncouragementService.repository.AttachmentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class AttachmentService {
//
//    @Autowired
//    private AttachmentRepository attachmentRepository;
//
//
//    private AttachmentDTO convertToDTO(Attachment attachment) {
//        AttachmentDTO dto = new AttachmentDTO();
//        dto.setAttachmentId(attachment.getAttachmentId());
//        dto.setAttachmentFile(attachment.getAttachmentFile());
//        dto.setAttachmentQuranicEncouragementId(attachment.getAttachmentQuranicEncouragementId());
//        dto.setAttachmentGradeEncouragementId(attachment.getAttachmentGradeEncouragementId());
//        dto.setAttachmentName(attachment.getAttachmentName());
//        return dto;
//    }
//
//
//    public AttachmentDTO createAttachment(AttachmentDTO attachmentDTO) {
//        Attachment attachment = new Attachment();
//        attachment.setAttachmentFile(attachmentDTO.getAttachmentFile());
//        attachment.setAttachmentQuranicEncouragementId(attachmentDTO.getAttachmentQuranicEncouragementId());
//        attachment.setAttachmentGradeEncouragementId(attachmentDTO.getAttachmentGradeEncouragementId());
//        attachment.setAttachmentName(attachmentDTO.getAttachmentName());
//
//        Attachment savedAttachment = attachmentRepository.save(attachment);
//        return convertToDTO(savedAttachment);
//    }
//
//
//    public List<AttachmentDTO> getFilteredAttachments(String file, String name, UUID quranicEncouragementId, UUID gradeEncouragementId) {
//        Specification<Attachment> spec = Specification
//                .where(AttachmentSpecification.attachmentFileContains(file))
//                .and(AttachmentSpecification.attachmentNameContains(name))
//                .and(AttachmentSpecification.attachmentQuranicEncouragementIdEquals(quranicEncouragementId))
//                .and(AttachmentSpecification.attachmentGradeEncouragementIdEquals(gradeEncouragementId));
//
//        List<Attachment> attachments = attachmentRepository.findAll(spec);
//        return attachments.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//
//    public AttachmentDTO getAttachmentById(UUID attachmentId) {
//        Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
//        return attachment.map(this::convertToDTO).orElse(null);
//    }
//
//
//    public AttachmentDTO updateAttachment(UUID attachmentId, AttachmentDTO attachmentDTO) {
//        Optional<Attachment> attachmentOpt = attachmentRepository.findById(attachmentId);
//        if (attachmentOpt.isPresent()) {
//            Attachment attachment = attachmentOpt.get();
//            attachment.setAttachmentFile(attachmentDTO.getAttachmentFile());
//            attachment.setAttachmentQuranicEncouragementId(attachmentDTO.getAttachmentQuranicEncouragementId());
//            attachment.setAttachmentGradeEncouragementId(attachmentDTO.getAttachmentGradeEncouragementId());
//            attachment.setAttachmentName(attachmentDTO.getAttachmentName());
//
//            Attachment updatedAttachment = attachmentRepository.save(attachment);
//            return convertToDTO(updatedAttachment);
//        } else {
//            return null;
//        }
//    }
//
//
//    public boolean deleteAttachment(UUID attachmentId) {
//        Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
//        if (attachment.isPresent()) {
//            attachmentRepository.delete(attachment.get());
//            return true;
//        }
//        return false;
//    }
//}
