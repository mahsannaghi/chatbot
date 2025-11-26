package com.paya.EncouragementService.dto.v2;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentGetDTO {
    private UUID attachmentId;
    private UUID attachmentQuranicEncouragementId;
    private UUID attachmentGradeEncouragementId;
    private String attachmentName;
    private LocalDateTime createDate;

    public AttachmentGetDTO(UUID attachmentId, UUID attachmentQuranicEncouragementId, String attachmentName, LocalDateTime createDate) {
        this.attachmentId = attachmentId;
        this.attachmentQuranicEncouragementId = attachmentQuranicEncouragementId;
        this.attachmentName = attachmentName;
        this.createDate = createDate;
    }

    public AttachmentGetDTO(UUID attachmentId, String attachmentName, UUID attachmentGradeEncouragementId, LocalDateTime createDate) {
        this.attachmentId = attachmentId;
        this.attachmentName = attachmentName;
        this.attachmentGradeEncouragementId = attachmentGradeEncouragementId;
        this.createDate = createDate;
    }

    public AttachmentGetDTO(UUID attachmentId, UUID attachmentQuranicEncouragementId, UUID attachmentGradeEncouragementId, String attachmentName, LocalDateTime createDate) {
        this.attachmentId = attachmentId;
        this.attachmentQuranicEncouragementId = attachmentQuranicEncouragementId;
        this.attachmentGradeEncouragementId = attachmentGradeEncouragementId;
        this.attachmentName = attachmentName;
        this.createDate = createDate;
    }
}
