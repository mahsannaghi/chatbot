package com.paya.EncouragementService.mapper.impl;


import com.paya.EncouragementService.dto.AttachmentDTO;
import com.paya.EncouragementService.entity.Attachment;
import com.paya.EncouragementService.mapper.AttachmentMapper;
import org.springframework.stereotype.Component;


@Component
public class AttachmentMapperImpl implements AttachmentMapper {
    public AttachmentDTO convertToDTO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        AttachmentDTO dto = new AttachmentDTO();
        dto.setAttachmentId(attachment.getAttachmentId());
        dto.setAttachmentFile(null);
        dto.setAttachmentQuranicEncouragementId(attachment.getAttachmentQuranicEncouragementId());
        dto.setAttachmentGradeEncouragementId(attachment.getAttachmentGradeEncouragementId());
        dto.setAttachmentName(attachment.getAttachmentName());

        return dto;
    }

    public Attachment convertToEntity(AttachmentDTO dto) {
        if (dto == null) {
            return null;
        }
        Attachment attachment = new Attachment();
        attachment.setAttachmentId(dto.getAttachmentId());
        attachment.setAttachmentFile(null);
        attachment.setAttachmentQuranicEncouragementId(dto.getAttachmentQuranicEncouragementId());
        attachment.setAttachmentGradeEncouragementId(dto.getAttachmentGradeEncouragementId());
        attachment.setAttachmentName(dto.getAttachmentName());

        return attachment;
    }

}
