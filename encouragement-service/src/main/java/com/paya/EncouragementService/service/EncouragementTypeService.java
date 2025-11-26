package com.paya.EncouragementService.service;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.EncouragementTypeSpecification;
import com.paya.EncouragementService.dto.EncouragementTypeDTO;
import com.paya.EncouragementService.dto.v2.EncouragementFilterDTOV2;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.enumeration.TypeCategoryEnum;
import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
import com.paya.EncouragementService.repository.EncouragementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncouragementTypeService {

    private final EncouragementTypeRepository repository;
    private final EncouragementReasonTypeRepository encouragementReasonTypeRepository;

    public List<EncouragementTypeDTO> getAllEncouragementTypes() {
        return repository.findAll().stream()
                .map(type -> new EncouragementTypeDTO(type.getEncouragementTypeId(), type.getEncouragementTypeTitle(),
                        type.isEncouragementTypeIsActive(), type.getEncouragementTypeNatureType(), type.getEncouragementTypeCategory().getCode()))
                .collect(Collectors.toList());
    }

    public EncouragementType create(EncouragementTypeDTO dto) throws GeneralException {
        if (dto.getEncouragementTypeTitle() == null || dto.getEncouragementTypeTitle().trim().isEmpty()) {
            throw new GeneralException("عنوان نباید خالی یا نال باشد!");
        }
        if (dto.getEncouragementTypeTitle() != null)
            dto.setEncouragementTypeTitle(dto.getEncouragementTypeTitle().replace("ی", "ي").trim());
        repository.findByEncouragementTypeTitle(dto.getEncouragementTypeTitle())
                .ifPresent(e -> {
                    try {
                        throw new GeneralException("عنوان نباید تکراری باشد!");
                    } catch (GeneralException ex) {
                        throw new RuntimeException(ex);
                    }
                });

        EncouragementType entity = EncouragementType.builder()
                .encouragementTypeTitle(dto.getEncouragementTypeTitle())
                .encouragementTypeIsActive(dto.isEncouragementTypeIsActive())
                .encouragementTypeNatureType(dto.getEncouragementTypeNatureType())
                .encouragementTypeCategory(TypeCategoryEnum.NORMAL)
                .build();

        return repository.save(entity);
    }

    public EncouragementType update(UUID id, EncouragementTypeDTO dto) throws GeneralException {
        if (!this.isTypeUsedInReasonType(id)) {
            EncouragementType entity = repository.findById(id)
                    .orElseThrow(() -> new GeneralException("EncouragementType not found with ID: " + id));
            repository.findByEncouragementTypeTitle(dto.getEncouragementTypeTitle())
                    .ifPresent(existingEntity -> {
                        if (existingEntity.getEncouragementTypeId().equals(id)) {
                            boolean isActiveChanged = existingEntity.isEncouragementTypeIsActive() != dto.isEncouragementTypeIsActive();
                            boolean natureTypeChanged = existingEntity.getEncouragementTypeNatureType() != dto.getEncouragementTypeNatureType();
                            if (!isActiveChanged && !natureTypeChanged) {
                                try {
                                    throw new GeneralException("شرح تکراری نمی‌تواند باشد و تغییرات دیگری ثبت نشده است!");
                                } catch (GeneralException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else {
                            try {
                                throw new GeneralException("عنوان وارد شده قبلا استفاده شده است!");
                            } catch (GeneralException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
            entity.setEncouragementTypeTitle(dto.getEncouragementTypeTitle());
            entity.setEncouragementTypeIsActive(dto.isEncouragementTypeIsActive());
            entity.setEncouragementTypeNatureType(dto.getEncouragementTypeNatureType());
            return repository.save(entity);
        }
        throw new GeneralException("امکان ویرایش این نوع تشویق به علت استفاده در قسمت ارتباط نوع و علت وجود ندارد .");
    }

    private boolean isTypeUsedInReasonType(UUID encouragementTypeId) {
        return encouragementReasonTypeRepository.existsByencouragementTypeId(encouragementTypeId);

    }

    public void delete(UUID typeId) throws Exception{
        if (!this.isTypeUsedInReasonType(typeId)) {
            repository.deleteById(typeId);
        }else
            throw new GeneralException("امکان حذف این نوع تشویق به علت استفاده در قسمت ارتباط  نوع و علت وجود ندارد .");
    }

    public List<EncouragementTypeDTO> filter(String title, Boolean isActive, Integer natureType) {
        Specification<EncouragementType> spec = Specification.where(null);

        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and(EncouragementTypeSpecification.hasTitle(title));
        }
        if (isActive != null) {
            spec = spec.and(EncouragementTypeSpecification.isActive(isActive));
        }
        if (natureType != null) {
            spec = spec.and(EncouragementTypeSpecification.hasNatureType(natureType));
        }
        Sort sort = Sort.by(Sort.Direction.DESC, EncouragementFilterDTOV2.Fields.updatedAt);
        return repository.findAll(spec, sort).stream()
                .map(type -> new EncouragementTypeDTO(
                        type.getEncouragementTypeId(),
                        type.getEncouragementTypeTitle(),
                        type.isEncouragementTypeIsActive(),
                        type.getEncouragementTypeNatureType(),
                        type.getEncouragementTypeCategory().getCode()))
                .collect(Collectors.toList());
    }


    public String findTypeTitleById(UUID reasonTypeId) {

        Optional<EncouragementReasonType> reasonType = encouragementReasonTypeRepository.findById(reasonTypeId);

        if (reasonType.isPresent() && reasonType.get().getEncouragementTypeId() != null) {

            UUID typeId = reasonType.get().getEncouragementTypeId();
            Optional<EncouragementType> type = repository.findById(typeId);
            return type.map(EncouragementType::getEncouragementTypeTitle).orElse(null);
        }
        return null;
    }

    public EncouragementType findById(UUID encouragementTypeId) throws GeneralException {
        if (encouragementTypeId != null) {
            return repository.findById(encouragementTypeId).orElseThrow(() -> new GeneralException( "نوع تشویق مورد نظر یافت نشد ."));
        } else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }

    public UUID findIdByReasonTypeId(UUID reasonTypeId) throws GeneralException {
        if (reasonTypeId != null) {
            return repository.findIdByReasonTypeId(reasonTypeId);
        } else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }


}
