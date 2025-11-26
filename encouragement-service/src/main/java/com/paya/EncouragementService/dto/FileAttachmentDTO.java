package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAttachmentDTO {
    private UUID gradeEncouragementId;
    private UUID quranicEncouragementId;
    private UUID encouragementReviewId;
    private UUID encouragementId;
}


