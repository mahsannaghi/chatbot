package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.dto.v2.AttachmentGetDTO;
import com.paya.EncouragementService.entity.Attachment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID>, JpaSpecificationExecutor<Attachment> {


    @Query("select case when exists(select 1 from Attachment a join QuranicEncouragement qe on a.attachmentQuranicEncouragementId = qe.quranicEncouragementId where a.attachmentId = :attachmentId)" +
            "then true " +
            "else false end ")
    Boolean existsInQuranicEncouragement(UUID attachmentId);


    @Query("select case when exists(select 1 from Attachment a join GradeEncouragement ge on a.attachmentGradeEncouragementId = ge.gradeEncouragementId where a.attachmentId = :attachmentId)" +
            "then true " +
            "else false end ")
    Boolean existsInGradeEncouragement(UUID attachmentId);


    @Query("select new com.paya.EncouragementService.dto.v2.AttachmentGetDTO(" +
            "a.attachmentId , " +
            "a.attachmentGradeEncouragementId , " +
            "a.attachmentQuranicEncouragementId , " +
            "a.attachmentName , " +
            "a.createDate " +
            ") from Attachment a order by a.createDate desc")
    List<AttachmentGetDTO> getAllAttachmentsWithoutFileContent(Pageable pageable);
    @Query("select a from Attachment a where a.attachmentQuranicEncouragementId =:encouragementReviewId order by a.createDate desc ")
    List<Attachment> getAllQuranicEncouragementAttachmentsWithId(Pageable pageable, UUID encouragementReviewId);


    @Query("select a from Attachment a where a.attachmentGradeEncouragementId =:encouragementReviewId order by a.createDate desc ")
    List<Attachment> getAllGradeEncouragementAttachmentsWithId(Pageable pageable, UUID encouragementReviewId);


    @Query("select a from Attachment a where a.attachmentEncouragementReviewId =:encouragementReviewId order by a.createDate desc ")
    List<Attachment> getAllEncouragementReviewAttachmentWithId(Pageable pageable, UUID encouragementReviewId);

}