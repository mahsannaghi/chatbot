package com.paya.EncouragementService.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AttachmentDTO {

    private UUID attachmentId;
    private MultipartFile attachmentFile;
    private UUID attachmentQuranicEncouragementId;
    private UUID attachmentGradeEncouragementId;
    private String attachmentName;


}
