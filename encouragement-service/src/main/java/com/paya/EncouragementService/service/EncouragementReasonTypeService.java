package com.paya.EncouragementService.service;

import com.netflix.config.validation.ValidationException;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.EncouragementReasonTypeSpecification;
import com.paya.EncouragementService.dto.*;
import com.paya.EncouragementService.dto.v2.ReasonTypeDTOV2;
import com.paya.EncouragementService.entity.Encouragement;
import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.entity.EncouragementReasonType;
import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.repository.EncouragementReasonRepository;
import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
import com.paya.EncouragementService.repository.EncouragementRepository;
import com.paya.EncouragementService.repository.EncouragementTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EncouragementReasonTypeService {


    private final EncouragementReasonTypeRepository reasonTypeRepository;
    private final EncouragementReasonRepository reasonRepository;
    private final EncouragementTypeRepository typeRepository;
    private final EncouragementRepository encouragementRepository;


    public EncouragementReasonTypeService(EncouragementReasonTypeRepository reasonTypeRepository, EncouragementReasonRepository reasonRepository, EncouragementTypeRepository typeRepository, EncouragementRepository encouragementRepository) {
        this.reasonTypeRepository = reasonTypeRepository;
        this.reasonRepository = reasonRepository;
        this.typeRepository = typeRepository;
        this.encouragementRepository = encouragementRepository;
    }

    public List<ResultDTO> getResults(String reasonTitle, String typeTitle, BigDecimal maxAmount, Integer maxDuration, String durationType, Boolean isActive) {
        Specification<EncouragementReasonType> specification = EncouragementReasonTypeSpecification.searchByFilters(
                reasonTitle, typeTitle, maxAmount, maxDuration, durationType, isActive
        );
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, EncouragementReasonType.Fields.encouragementReasonTypeUpdatedAt));
        Page<EncouragementReasonType> reasonTypes = reasonTypeRepository.findAll(specification, pageRequest);

        if (reasonTypes.isEmpty()) {
            System.out.println("No matching data found for the given filters.");
            return Collections.emptyList();
        }

        Map<UUID, EncouragementType> typeMap = typeRepository.findAll().stream()
                .collect(Collectors.toMap(EncouragementType::getEncouragementTypeId, types -> types));

        return reasonTypes.stream()
                .collect(Collectors.groupingBy(EncouragementReasonType::getEncouragementReasonId, LinkedHashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    UUID reasonId = entry.getKey();
                    List<EncouragementReasonType> filteredReasonTypes = entry.getValue();

                    EncouragementReason reason = reasonRepository.findById(reasonId).orElse(null);
                    if (reason == null) {
                        return null;
                    }

                    ResultDTO resultDTO = new ResultDTO();
                    ReasonDTO reasonDTO = new ReasonDTO();
                    reasonDTO.setReasonId(reason.getEncouragementReasonId());
                    reasonDTO.setReasonTitle(reason.getEncouragementReasonTitle());
                    resultDTO.setReason(reasonDTO);

                    List<TypeDTO> typeList = filteredReasonTypes.stream()
                            .map(rt -> {
                                EncouragementType type = typeMap.get(rt.getEncouragementTypeId());
                                if (type == null) {
                                    return null;
                                }
                                TypeDTO typeDTO = new TypeDTO();
                                typeDTO.setReasonTypeId(rt.getEncouragementReasonTypeId());
                                Optional<EncouragementReasonType> encouragementReasonType = reasonTypeRepository.findEncouragementReasonTypeByEncouragementReasonTypeId(rt.getEncouragementReasonTypeId());
                                encouragementReasonType.ifPresent(reasonType -> typeDTO.setActive(reasonType.isActive()));
                                typeDTO.setTypeId(type.getEncouragementTypeId());
                                typeDTO.setTypeTitle(type.getEncouragementTypeTitle());
                                typeDTO.setMaxAmount(rt.getMaxAmount() == null ? BigDecimal.valueOf(0) : rt.getMaxAmount());
                                typeDTO.setMaxDuration(rt.getMaxDuration() == null ? 0 : rt.getMaxDuration());
                                typeDTO.setDurationType(rt.getDurationType());
                                return typeDTO;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    resultDTO.setActive(filteredReasonTypes.stream().anyMatch(EncouragementReasonType::isActive));
                    resultDTO.setTypes(typeList);

                    if (typeTitle != null && !typeTitle.isEmpty()) {
                        List<TypeDTO> filteredTypes = typeList.stream()
                                .filter(t -> t.getTypeTitle() != null && t.getTypeTitle().contains(typeTitle)).toList();
                        if (filteredTypes.isEmpty()) {
                            return null;
                        }
                    }

                    return resultDTO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private void checkForDuplicateType(UUID reasonId, UUID typeId, UUID reasonTypeId) throws GeneralException {

        if (typeId == null || reasonId == null) {
            throw new GeneralException("reasonId and typeId must not be null.");
        }

        boolean exists;
        if (reasonTypeId == null) {
            exists = reasonTypeRepository.existsByEncouragementReasonIdAndEncouragementTypeId(reasonId, typeId);
        } else {
            exists = reasonTypeRepository.existsByEncouragementReasonIdAndEncouragementTypeIdAndEncouragementReasonTypeIdNot(reasonId, typeId, reasonTypeId);
        }

        if (exists) {
            throw new GeneralException("Duplicate typeId: " + typeId + " for reasonId: " + reasonId);
        }
    }


    public EncouragementReasonType toEntity(EncouragementReasonTypeDTO dto) throws GeneralException {
        checkForDuplicateType(dto.getReasonId(), dto.getTypeId(), dto.getReasonTypeId());

        EncouragementReasonType entity;

        if (dto.getReasonTypeId() == null) {
            entity = new EncouragementReasonType();
            entity.setEncouragementReasonTypeId(UUID.randomUUID());
        } else {
            entity = reasonTypeRepository.findById(dto.getReasonTypeId())
                    .orElseThrow(() -> new GeneralException("Record not found for id: " + dto.getReasonTypeId()));
        }

        updateEntityFields(entity, dto);
        return entity;
    }


    public ResponseDTO processEncouragementRequest(EncouragementRequestDTO requestDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setReasonId(requestDTO.getReasonId());
        Set<String> addedTypes = new HashSet<>();
        List<EncouragementReasonTypeDTO> typesToAddOrUpdate = requestDTO.getTypesToAddOrUpdate().stream()
                .map(type -> {
                    String reasonTypeKey = type.getReasonId() + "_" + type.getTypeId();
                    if (addedTypes.contains(reasonTypeKey)) {
                        throw new IllegalArgumentException("Duplicate typeId: " + type.getTypeId() + " for reasonId: " + type.getReasonId());
                    }
                    addedTypes.add(reasonTypeKey);

                    Optional<EncouragementReasonType> existingEntityOpt = reasonTypeRepository.findByEncouragementReasonIdAndEncouragementTypeId(type.getReasonId(), type.getTypeId());
                    EncouragementReasonType entity;

                    if (existingEntityOpt.isPresent()) {
                        entity = existingEntityOpt.get();
                        try {
                            validateThatCanBeUpdated(entity, type);
                        } catch (GeneralException e) {
                            throw new RuntimeException(e);
                        }
                        boolean isUpdated;
                        entity.setMaxAmount(type.getMaxAmount());
                        entity.setMaxDuration(type.getMaxDuration());
                        entity.setDurationType(type.getDurationType());
                        entity.setActive(type.isActive());

                        isUpdated = true;

                        if (isUpdated) {
                            reasonTypeRepository.save(entity);
                        }
                    } else {
                        try {
                            entity = toEntity(type);
                        } catch (GeneralException e) {
                            throw new RuntimeException(e);
                        }
                        entity.setActive(type.isActive());
                        reasonTypeRepository.save(entity);
                    }

                    type.setReasonTypeId(entity.getEncouragementReasonTypeId());
                    return type;
                }).collect(Collectors.toList());

        responseDTO.setTypesToAddOrUpdate(typesToAddOrUpdate);

        // بررسی و حذف انواع انتخابی
        if (requestDTO.getDeleteTheseTypes() != null && !requestDTO.getDeleteTheseTypes().isEmpty()) {
            requestDTO.getDeleteTheseTypes().forEach(typeId -> {
                if (typeId != null) {
                    // پیدا کردن رکوردهایی با typeId یکسان
                    List<EncouragementReasonType> reasonTypeList = reasonTypeRepository.findByEncouragementTypeId(typeId);

                    // حذف رکوردهای با typeId مشابه بدون بررسی reasonIdهای مختلف
                    reasonTypeList.forEach(reasonType -> {
                        // بررسی اینکه آیا این typeId در جدول Encouragement وجود دارد
                        List<Encouragement> encouragementOpt = encouragementRepository.findByEncouragementReasonTypeId(reasonType.getEncouragementReasonTypeId());
                        // اگر در جدول Encouragement موجود نباشد، رکورد را حذف می‌کنیم
                        if (encouragementOpt.isEmpty()) {
                            Optional<EncouragementReasonType> reasonTypeOpt = reasonTypeRepository.findByEncouragementReasonIdAndEncouragementTypeId(requestDTO.getReasonId(), typeId);
                            if (reasonTypeOpt.isPresent()) {
                                EncouragementReasonType reasonTypeToDelete = reasonTypeOpt.get();
                                reasonTypeRepository.delete(reasonTypeToDelete);
                            }
                        } else {
                            throw new ValidationException("تشویقی با این ارتباط نوع و علت تعریف شده است.");
//                              System.out.println("aaaaaaa");
//                            ErrorResponse errorResponse = new ErrorResponse(
//                                    "400", // یا کد خطای مناسب
//                                    "یک خطا در هنگام حذف منبع رخ داده است."
//                            );
//                            responseDTO.setErrorResponse(errorResponse);
                            //  System.out.println("gggggggg");
                        }
                    });
                }
            });
        }

        responseDTO.setDeleteTheseTypes(requestDTO.getDeleteTheseTypes());

        return responseDTO;
    }

    private void validateThatCanBeUpdated(EncouragementReasonType entity, EncouragementReasonTypeDTO type) throws GeneralException {
        if (!(Objects.equals(entity.getMaxDuration(), type.getMaxDuration()) &&
                Objects.equals(entity.getMaxAmount(), type.getMaxAmount()) &&
                Objects.equals(entity.getDurationType(), type.getDurationType()) &&
                Objects.equals(entity.isActive(), type.isActive()))) {
            List<Encouragement> encouragementOpt = encouragementRepository.findByEncouragementReasonTypeId(entity.getEncouragementReasonTypeId());
            if (!encouragementOpt.isEmpty()) {
                if (!(Objects.equals(entity.getMaxDuration(), type.getMaxDuration()) &&
                        Objects.equals(entity.getMaxAmount(), type.getMaxAmount()) &&
                        Objects.equals(entity.getDurationType(), type.getDurationType()) &&
                        !Objects.equals(entity.isActive(), type.isActive())))
                    throw new ValidationException("سند ارتباط نوع علت در یک تشویق استفاده شده است");
            }
            if (entity.isActive() == type.isActive()) {
                if (!entity.isActive()) {
                    if (!Objects.equals(entity.getMaxDuration(), type.getMaxDuration()) ||
                            !Objects.equals(entity.getMaxAmount(), type.getMaxAmount()) ||
                            !Objects.equals(entity.getDurationType(), type.getDurationType()))
                        throw new GeneralException("این ارتباط نوع علت غیر فعال است و امکان ویرایش آن وجود ندارد.");
                }
            }
        }
    }


    private void updateEntityFields(EncouragementReasonType entity, EncouragementReasonTypeDTO dto) throws GeneralException {
        if (entity == null || dto == null) {
            throw new GeneralException("Entity or DTO must not be null.");
        }

        entity.setEncouragementReasonId(dto.getReasonId());
        entity.setEncouragementTypeId(dto.getTypeId());
        entity.setMaxAmount(dto.getMaxAmount());
        entity.setMaxDuration(dto.getMaxDuration());
        entity.setDurationType(dto.getDurationType() != null ? dto.getDurationType().trim() : "");
        entity.setActive(dto.isActive());
    }

    private EncouragementReasonTypeDTO convertToDTO(EncouragementReasonType entity) {
        return EncouragementReasonTypeDTO.builder()
                .reasonTypeId(entity.getEncouragementReasonTypeId())
                .reasonId(entity.getEncouragementReasonId())
                .typeId(entity.getEncouragementTypeId())
                .maxAmount(entity.getMaxAmount())
                .maxDuration(entity.getMaxDuration())
                .durationType(entity.getDurationType())
                .build();
    }


    public List<EncouragementReasonTypeDTO> searchByReasonAndTypeTitles(String reasonTitle, String typeTitle) {
        Specification<EncouragementReasonType> spec = EncouragementReasonTypeSpecification.searchByReasonAndTypeTitles(reasonTitle, typeTitle);
        return reasonTypeRepository.findAll(spec).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<ReasonTypeDTOV2> getGroupedReasonTypes(String reasonTitle, String typeTitle) {
        List<ReasonTypeDTOV2> groupedReasonTypes = reasonTypeRepository.getTypesGroupedByReason();
        List<ReasonTypeDTOV2> finalResultList = new ArrayList<>();
        if (!groupedReasonTypes.isEmpty()) {
            //set titles with found IDs
            for (ReasonTypeDTOV2 reasonTypeDTOV2 : groupedReasonTypes) {
                String encouragementReasonTitle = reasonRepository.getTitleWithId(reasonTypeDTOV2.getEncouragementReasonId());
                if (encouragementReasonTitle != null) {
                    reasonTypeDTOV2.setEncouragementReasonTitle(encouragementReasonTitle);
                }
                List<EncouragementType> typeList = typeRepository.getAllExitInEncouragementReasonType(reasonTypeDTOV2.getEncouragementReasonTypeId());
                if (!typeList.isEmpty()) {
                    reasonTypeDTOV2.setEncouragementTypeList(typeList);
                }

            }
            //filtering with existing condition
            finalResultList.addAll(groupedReasonTypes);
            if (typeTitle != null && !typeTitle.isEmpty()) {
                for (ReasonTypeDTOV2 typeDTOV2 : finalResultList) {
                    List<EncouragementType> filteredList = typeDTOV2.getEncouragementTypeList()
                            .stream()
                            .filter(encouragementType -> typeTitle.contains(encouragementType.getEncouragementTypeTitle()))
                            .collect(Collectors.toList());
                    typeDTOV2.setEncouragementTypeList(filteredList);
                }
            }
            if (reasonTitle != null && !reasonTitle.isEmpty()) {
                finalResultList = finalResultList.stream()
                        .filter(encouragementReason -> reasonTitle.equals(encouragementReason.getEncouragementReasonTitle()))
                        .toList();
            }
            return finalResultList;
        }
        return null;
    }

    public EncouragementReasonType findById(UUID encouragementReasonTypeId) throws GeneralException {
        if (encouragementReasonTypeId != null) {
            return reasonTypeRepository.findById(encouragementReasonTypeId).orElseThrow(() -> new GeneralException("نوع و علت تشویق مورد نظر یافت نشد ."));
        } else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }

    public Integer getEncouragementTypeByReasonTypeId(UUID encouragementReasonTypeId) {
        return reasonTypeRepository.getEncouragementTypeByReasonTypeId(encouragementReasonTypeId);
    }
}
