package com.paya.EncouragementService.mapper;


import com.paya.EncouragementService.dto.AttachmentDTO;
import com.paya.EncouragementService.entity.Attachment;
import org.springframework.stereotype.Component;


@Component
public interface AttachmentMapper {
    AttachmentDTO convertToDTO(Attachment attachment);

    Attachment convertToEntity(AttachmentDTO attachmentDTO);


}
