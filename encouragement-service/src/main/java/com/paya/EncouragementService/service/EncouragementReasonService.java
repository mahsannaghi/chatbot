package com.paya.EncouragementService.service;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.EncouragementReasonSpecification;
import com.paya.EncouragementService.dto.CustomMessage;
import com.paya.EncouragementService.dto.EncouragementReasonDTO;
import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.repository.EncouragementReasonRepository;
import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
import com.paya.EncouragementService.repository.EncouragementTypeRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.NotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class EncouragementReasonService {

    private final EncouragementReasonRepository reasonRepository;
    private final EncouragementTypeRepository typeRepository;
    private final EncouragementReasonTypeRepository encouragementReasonTypeRepository;

    public EncouragementReasonService(EncouragementReasonRepository reasonRepository, EncouragementTypeRepository typeRepository, EncouragementReasonTypeRepository encouragementReasonTypeRepository) {
        this.reasonRepository = reasonRepository;
        this.typeRepository = typeRepository;
        this.encouragementReasonTypeRepository = encouragementReasonTypeRepository;
    }


    public Page<EncouragementReason> filter(String title, Boolean active, Pageable pageable) {
        Specification<EncouragementReason> spec = Specification.where(
                EncouragementReasonSpecification.hasTitle(title)
        ).and(EncouragementReasonSpecification.isActive(active));

        return reasonRepository.findAll(spec, pageable);
    }

    public EncouragementReason create(EncouragementReasonDTO dto) {
        if (dto.getEncouragementReasonTitle() != null)
            dto.setEncouragementReasonTitle(dto.getEncouragementReasonTitle().replace("ی", "ي").trim());
        reasonRepository.findByEncouragementReasonTitle(dto.getEncouragementReasonTitle())
                .ifPresent(e -> {
                    try {
                        throw new Exception("عنوان باید تکراری نباشد!");
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });

        EncouragementReason entity = EncouragementReason.builder()
                .encouragementReasonTitle(dto.getEncouragementReasonTitle())
                .encouragementReasonIsActive(dto.getEncouragementReasonIsActive())

                .build();
        return reasonRepository.save(entity);
    }

    public EncouragementReason update(UUID id, EncouragementReasonDTO dto) throws GeneralException {
        if (!this.isReasonUsedInReasonType(id)) {
            EncouragementReason entity = reasonRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("EncouragementReason not found with ID: " + id));
            if (dto.getEncouragementReasonTitle() != null)
                dto.setEncouragementReasonTitle(dto.getEncouragementReasonTitle().replace("ی", "ي").trim());
            boolean isTitleSameAsBefore = entity.getEncouragementReasonTitle().equals(dto.getEncouragementReasonTitle());
            boolean isIsActiveSameAsBefore = entity.getEncouragementReasonIsActive().equals(dto.getEncouragementReasonIsActive());


            if (isTitleSameAsBefore && isIsActiveSameAsBefore) {
                throw new GeneralException("عنوان نمی تواند تکراری باشد!");
            }


            if (!isTitleSameAsBefore) {
                reasonRepository.findByEncouragementReasonTitle(dto.getEncouragementReasonTitle())
                        .ifPresent(e -> {
                            if (!e.getEncouragementReasonId().equals(id)) {
                                try {
                                    throw new GeneralException("عنوان نمی تواند تکراری باشد!");
                                } catch (GeneralException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
            }
            entity.setEncouragementReasonTitle(dto.getEncouragementReasonTitle());
            entity.setEncouragementReasonIsActive(dto.getEncouragementReasonIsActive());

            return reasonRepository.save(entity);
        }
        throw new GeneralException("امکان ویرایش این علت به دلیل استفاده در قسمت ارتباط نوع و علت وجود ندارد .");

    }

    private boolean isReasonUsedInReasonType(UUID encouragementReasonId) {
        return encouragementReasonTypeRepository.existsByencouragementReasonId(encouragementReasonId);

    }

    public CustomMessage delete(UUID reasonId) throws ResponseStatusException, GeneralException {
        if (!this.isReasonUsedInReasonType(reasonId)) {
            reasonRepository.deleteById(reasonId);
            return new CustomMessage(HttpStatus.ACCEPTED.value(), "حذف علت با موفقیت انجام شد .");
        } else
            throw new GeneralException("امکان حذف این علت به دلیل استفاده در قسمت ارتباط نوع و علت وجود ندارد .");

    }


    public String findReasonTitleById(UUID reasonTypeId) {

        Optional<EncouragementReasonType> reasonType = encouragementReasonTypeRepository.findById(reasonTypeId);

        if (reasonType.isPresent() && reasonType.get().getEncouragementReasonId() != null) {

            UUID reasonId = reasonType.get().getEncouragementReasonId();
            Optional<EncouragementReason> reason = reasonRepository.findById(reasonId);
            return reason.map(EncouragementReason::getEncouragementReasonTitle).orElse(null);
        }
        return null;
    }

    public UUID findReasonById(UUID reasonTypeId) {

        Optional<EncouragementReasonType> reasonType = encouragementReasonTypeRepository.findById(reasonTypeId);

        if (reasonType.isPresent() && reasonType.get().getEncouragementReasonId() != null) {

            UUID reasonId = reasonType.get().getEncouragementReasonId();
            Optional<EncouragementReason> reason = reasonRepository.findById(reasonId);
            return reason.map(EncouragementReason::getEncouragementReasonId).orElse(null);
        }
        return null;
    }

    public UUID findTypeById(UUID reasonTypeId) {

        Optional<EncouragementReasonType> reasonType = encouragementReasonTypeRepository.findById(reasonTypeId);

        if (reasonType.isPresent() && reasonType.get().getEncouragementTypeId() != null) {

            UUID typeId = reasonType.get().getEncouragementTypeId();
            Optional<EncouragementType> type = typeRepository.findById(typeId);
            return type.map(EncouragementType::getEncouragementTypeId).orElse(null);
        }
        return null;
    }


}
