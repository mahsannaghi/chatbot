package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID attachmentId;

    @Column(name = "attachment_file", columnDefinition = "VARBINARY(MAX)")
    private byte[] attachmentFile;

    @Column(name = "attachment_quranic_encouragement_id", columnDefinition = "uniqueidentifier")
    private UUID attachmentQuranicEncouragementId;

    @Column(name = "attachment_grade_encouragement_id", columnDefinition = "uniqueidentifier")
    private UUID attachmentGradeEncouragementId;

    @Column(name = "attachment_encouragement_id", columnDefinition = "uniqueidentifier")
    private UUID attachmentEncouragementReviewId;

    @Column(name = "attachment_name", columnDefinition = "nvarchar(255)")
    private String attachmentName;

    @Transient
    private double attachmentFileSize;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private LocalDateTime createDate;
}
