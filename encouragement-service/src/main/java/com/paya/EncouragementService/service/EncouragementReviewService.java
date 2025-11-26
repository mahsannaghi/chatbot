package com.paya.EncouragementService.service;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.EncouragementReviewSpecification;
import com.paya.EncouragementService.dto.EncouragementReviewDTO;
import com.paya.EncouragementService.dto.EncouragementReviewSearchDTO;
import com.paya.EncouragementService.entity.Encouragement;
import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import com.paya.EncouragementService.entity.EncouragementReview;
import com.paya.EncouragementService.enumeration.ReviewResultEnum;
import com.paya.EncouragementService.enumeration.ReviewTypeEnum;
import com.paya.EncouragementService.repository.EncouragementReasonRepository;
import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
import com.paya.EncouragementService.repository.EncouragementReviewRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EncouragementReviewService {
    private final EncouragementReviewRepository repository;
    private final EncouragementReasonTypeRepository encouragementReasonTypeRepository;
    private final EncouragementReasonRepository encouragementReasonRepository;

    public Page<EncouragementReview> getEncouragementReviewsWithSpecification(EncouragementReviewSearchDTO dto, PageRequest pageRequest) {
        return repository.findAll(
                EncouragementReviewSpecification.filterByCriteria(dto), pageRequest
        );
    }

    public EncouragementReview add(EncouragementReview encouragement) {
        return repository.save(encouragement);
    }

    @Transactional
    public List<EncouragementReview> add(List<EncouragementReview> encouragementReviewList) {
        return repository.saveAll(encouragementReviewList);
    }

    @Transactional
    public void changeEncouragementReviewStatus(EncouragementReview encouragementReview, Integer result) {
        encouragementReview.setEncouragementReviewResult(result);
        encouragementReview.setEncouragementReviewAppliedDate(LocalDate.now());
        repository.markAsSeenWithoutUpdatingTimestamp(Collections.singletonList(encouragementReview.getEncouragementReviewId()));
    }

    public void deleteEncouragementReview(UUID id) {
        repository.deleteById(id);
    }

    public Optional<EncouragementReview> getAllCommissionReviewsForThisEncouragement(UUID encouragementId) {
        return repository.findAllByEncouragementReviewTypeAndEncouragementReviewEncouragementIdAndEncouragementReviewResult(ReviewTypeEnum.ORDINARY_COMMISSION.getCode(), encouragementId, ReviewResultEnum.UNDER_COMMISSION_REVIEW.getCode());
//        return this.convertToDTO(reviewList);
    }

    public Optional<EncouragementReview> getAllVedjaReviewsForThisEncouragement(UUID encouragementId) {
        return repository.findAllByEncouragementReviewTypeAndEncouragementReviewEncouragementIdAndEncouragementReviewResult(ReviewTypeEnum.VEDJA_COMMISSION.getCode(), encouragementId, ReviewResultEnum.UNDER_VEDJA_REVIEW.getCode());
//        return this.convertToDTO(reviewList);
    }

    public EncouragementReviewDTO convertToDTO(EncouragementReview entity) {
        UUID reasonTypeId = entity.getEncouragementReviewEncouragementReasonTypeId();
        UUID reasonTypeId2 = entity.getEncouragementReviewReasonTypeId();
        UUID encouragementReviewTypeId = null;
        Optional<EncouragementReasonType> optional = encouragementReasonTypeRepository.findEncouragementReasonTypeByEncouragementReasonTypeId(reasonTypeId);
        String reasonTitle = null;
        UUID reasonId = null;
        if (optional.isPresent()) {
            EncouragementReasonType encouragementReasonType = optional.get();
            Optional<EncouragementReason> optional1 = getEncouragementReason(encouragementReasonType.getEncouragementReasonId());
            if (optional1.isPresent()) {
                EncouragementReason encouragementReason = optional1.get();
                reasonId = encouragementReason.getEncouragementReasonId();
                reasonTitle = encouragementReason.getEncouragementReasonTitle();
            }
        }
        Optional<EncouragementReasonType> optional2 = encouragementReasonTypeRepository.findEncouragementReasonTypeByEncouragementReasonTypeId(reasonTypeId2);
        if (optional2.isPresent()) {
            encouragementReviewTypeId = encouragementReasonTypeRepository.getTypeIdTitleWithReasonTypeId(optional2.get().getEncouragementReasonTypeId());
        }
        String typeTitle = encouragementReasonTypeRepository.getTypeTitleWithReasonTypeId(reasonTypeId);
        return new EncouragementReviewDTO(
                entity.getEncouragementReviewId(),
                entity.getEncouragementReviewRegistrarOrganizationId(),
                entity.getEncouragementReviewPayingAuthorityId(),
                entity.getEncouragementReviewEncouragementTypeId(),
                entity.getEncouragementReviewResult(),
                entity.getEncouragementReviewDescription(),
                entity.getEncouragementReviewAmount(),
                entity.getEncouragementReviewAmountType(),
                entity.getEncouragementReviewPercentage(),
                entity.getEncouragementReviewType(),
                entity.getEncouragementReviewSentDraftDate(),
                entity.getEncouragementReviewRegistrarOrganizationId(),
                entity.getEncouragementReviewAppliedDate(),
                entity.getEncouragementReviewDraft() != null ? entity.getEncouragementReviewDraft().getCode() : 0,
                reasonTypeId,
                reasonTitle,
                reasonId,
                typeTitle,
                encouragementReviewTypeId,
                entity.getEncouragementReviewEncouragementAmount(),
                entity.getEncouragementReviewEncouragementDescription(),
                entity.getEncouragementReviewEncouragementAppliedDate(),
                entity.getEncouragementReviewEncouragementCreatedDate(),
                entity.getEncouragementReviewEncouragementAmountType(),
                entity.getIsEncouragementReviewSeen()
        );
    }

    public Optional<EncouragementReason> getEncouragementReason(UUID encouragementReasonTypeId) {
        return encouragementReasonRepository.findByEncouragementReasonId(encouragementReasonTypeId);
    }

    public EncouragementReview findById(UUID encouragementReviewId) throws GeneralException {
        return repository.findById(encouragementReviewId).orElseThrow(() -> new GeneralException("بررسی تشویق مورد نظر یافت نشد . "));
    }

    public Optional<EncouragementReview> getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(String encouragementRegistrarOrganizationId, UUID encouragementId, int reviewType) {
        return repository.findByEncouragementReviewRegistrarOrganizationIdAndEncouragementReviewEncouragementIdAndEncouragementReviewResultAndEncouragementReviewType(encouragementRegistrarOrganizationId, encouragementId, ReviewResultEnum.UNDER_REVIEW.getCode(), reviewType);
    }

    public List<EncouragementReview> getReviewOfThisRegistrarThisEncouragementWithReviewerType(String encouragementRegistrarOrganizationId, UUID encouragementId, int reviewType) {
        return repository.findAllByEncouragementReviewRegistrarOrganizationIdAndEncouragementReviewEncouragementIdAndEncouragementReviewTypeOrderByEncouragementReviewCreatedAtDesc(encouragementRegistrarOrganizationId, encouragementId, reviewType);
    }

    public Optional<EncouragementReview> getReviewOfThisRegistrarThisEncouragement(String encouragementRegistrarOrganizationId, UUID encouragementId) {
        return repository.findByEncouragementReviewRegistrarOrganizationIdAndEncouragementReviewEncouragementId(encouragementRegistrarOrganizationId, encouragementId);
    }

    public String getLastReviewForEncouragement(UUID encouragementId) {
        List<EncouragementReview> review = repository.findAllByEncouragementReviewEncouragementIdOrderByEncouragementReviewUpdatedAtAsc(encouragementId);
        if (review != null && review.size() != 0){
            return review.stream().map(EncouragementReview::getEncouragementReviewDescription).filter(description -> description != null && !description.trim().isEmpty()).collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

    public void updateEncouragementReviewHasBeenSeen(Encouragement encouragement, EncouragementReview thisEncouragementReview, String currentUserOrganizationID, PageRequest pageRequest) {
        Page<EncouragementReview> reviewPage = this.getEncouragementReviewsWithSpecification(EncouragementReviewSearchDTO.builder().encouragementReviewEncouragementId(encouragement.getEncouragementId()).build(), pageRequest);
        if (reviewPage.toList().size() > 0) {
            List<EncouragementReview> reviews = reviewPage.toList();
            List<EncouragementReview> reviewList = new ArrayList<>();
            Optional<EncouragementReview> optional1 = reviews.stream().filter(encouragementReview -> encouragementReview.getEncouragementReviewType().equals(ReviewTypeEnum.ORDINARY_REVIEWER.getCode()) &&
                    !encouragementReview.getIsEncouragementReviewSeen()).findAny();
            if (optional1.isPresent()) {
                Optional<EncouragementReview> optional = reviews.stream().filter(encouragementReview -> encouragementReview.getEncouragementReviewRegistrarOrganizationId() != null && encouragementReview.getEncouragementReviewRegistrarOrganizationId().equals(currentUserOrganizationID)).findAny();
                if (optional.isPresent()) {
                    EncouragementReview lastReview = optional.get();
                    if ((lastReview.getEncouragementReviewRegistrarOrganizationId() != null && lastReview.getEncouragementReviewRegistrarOrganizationId().equals(currentUserOrganizationID)) &&
                            ((!(lastReview.getEncouragementReviewType().equals(ReviewTypeEnum.VEDJA_COMMISSION.getCode()) ||
                                    lastReview.getEncouragementReviewType().equals(ReviewTypeEnum.ORDINARY_COMMISSION.getCode()))
                            ))) {
                        Optional<EncouragementReview> previousReview = reviews.stream().filter(encouragementReview -> encouragementReview.getEncouragementReviewPreviousOrganizationId() != null &&
                                encouragementReview.getEncouragementReviewPreviousOrganizationId().equals(currentUserOrganizationID) &&
                                encouragementReview.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_RECENT_MANAGER_CORRECTION.getCode()) &&
                                !encouragementReview.getEncouragementReviewRegistrarOrganizationId().equals(encouragement.getEncouragementRegistrarOrganizationId())).findAny();
                        previousReview.ifPresent(reviewList::add);
                        Optional<EncouragementReview> nextReview = reviews.stream().filter(encouragementReview -> encouragementReview.getEncouragementReviewRegistrarOrganizationId() != null &&
                                thisEncouragementReview.getEncouragementReviewPreviousOrganizationId() != null &&
                                encouragementReview.getEncouragementReviewRegistrarOrganizationId().equals(thisEncouragementReview.getEncouragementReviewPreviousOrganizationId()) &&
                                !encouragementReview.getEncouragementReviewResult().equals(ReviewResultEnum.UNDER_REVIEW.getCode()) &&
                                !encouragementReview.getEncouragementReviewRegistrarOrganizationId().equals(encouragement.getEncouragementRegistrarOrganizationId())).findAny();
                        nextReview.ifPresent(reviewList::add);
                    } else {
                        optional1.ifPresent(reviewList::add);
                    }
                }
                if (reviewList.size() != 0) {
                    List<UUID> uuidList = reviewList.stream().map(encouragementReview -> {
                        encouragementReview.setIsEncouragementReviewSeen(Boolean.TRUE);
                        return encouragementReview.getEncouragementReviewId();
                    }).collect(Collectors.toList());
                    repository.markAsSeenWithoutUpdatingTimestamp(uuidList);
                }
            }
        }
    }

    public void updateExistEncouragementReview(EncouragementReview encouragementReview, Encouragement encouragement) {
        if (encouragement.getEncouragementRegistrarOrganizationId().equals(encouragementReview.getEncouragementReviewPreviousOrganizationId())) {
            encouragementReview.setEncouragementReviewEncouragementAmount(encouragement.getEncouragementAmount());
            encouragementReview.setEncouragementReviewEncouragementReasonTypeId(encouragement.getEncouragementReasonTypeId());
        }
        encouragementReview.setEncouragementReviewReasonTypeId(encouragement.getEncouragementReasonTypeId());
        encouragementReview.setEncouragementReviewAmount(encouragement.getEncouragementAmount());
        repository.save(encouragementReview);
    }

    public void updateSentForRegistrarEncouragementReviewHasBeenSeen(Encouragement encouragement) {
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(EncouragementReview.Fields.encouragementReviewUpdatedAt).descending());
        Page<EncouragementReview> reviewPage = this.getEncouragementReviewsWithSpecification(EncouragementReviewSearchDTO.builder().encouragementReviewEncouragementId(encouragement.getEncouragementId()).build(), pageRequest);
        Optional<EncouragementReview> sentForRegistrarReview = Optional.empty();
        if (reviewPage.toList().size() > 0) {
            List<EncouragementReview> reviews = reviewPage.toList();
            sentForRegistrarReview = reviews.stream().filter(encouragementReview ->
                    encouragementReview.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_REGISTRAR_CORRECTION.getCode())).findAny();
        }
        if (sentForRegistrarReview.isPresent()) {
            EncouragementReview review = sentForRegistrarReview.get();
            repository.markAsSeenWithoutUpdatingTimestamp(Collections.singletonList(review.getEncouragementReviewId()));
        }
    }
}
