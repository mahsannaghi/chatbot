package com.paya.EncouragementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netflix.config.validation.ValidationException;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.QuranicEncouragementSpecification;
import com.paya.EncouragementService.dto.*;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.dto.v2.QuranicEncouragementDTOV2;
import com.paya.EncouragementService.dto.v2.QuranicEncouragementFilterDTOV2;
import com.paya.EncouragementService.entity.Attachment;
import com.paya.EncouragementService.entity.QuranicEncouragement;
import com.paya.EncouragementService.repository.QuranicEncouragementRepository;
import com.paya.EncouragementService.repository.v2.QuranicEncouragementDAO;
import com.paya.EncouragementService.service.v2.AttachmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuranicEncouragementService {


    private final QuranicEncouragementRepository repository;
    private final PersonnelService personnelService;
    private final QuranicEncouragementDAO quranicEncouragementDAO;
    private final AttachmentService attachmentService;
    private final AuthService authService;

    public QuranicEncouragementService(QuranicEncouragementRepository repository, PersonnelService personnelService, QuranicEncouragementDAO quranicEncouragementDAO, AttachmentService attachmentService, AuthService authService) {
        this.repository = repository;
        this.personnelService = personnelService;
        this.quranicEncouragementDAO = quranicEncouragementDAO;
        this.attachmentService = attachmentService;
        this.authService = authService;
    }

    @Transactional
    public QuranicEncouragement createOrUpdateQuranicEncouragement(String quranicDTO, List<MultipartFile> files, List<String> deleteFileIdList, boolean isCreateMode) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        QuranicEncouragementInsertDTO encouragementDTO = objectMapper.readValue(quranicDTO, QuranicEncouragementInsertDTO.class);
        QuranicEncouragement entity;
        if (encouragementDTO != null) {
            if (encouragementDTO.getQuranicEncouragementId() != null)
                entity = repository.findById(encouragementDTO.getQuranicEncouragementId()).orElseThrow(EntityNotFoundException::new);
            else if (isCreateMode){
                entity = new QuranicEncouragement();
                PersonnelDTO currentUser = authService.getCurrentUserProfile().getUserInfo();
                entity.setRegistrarPersonnelId(UUID.fromString(currentUser.getPersonnelId()));
            }else
                throw new ValidationException("برای ویرایش ای دی را وارد کنید.");
            if (encouragementDTO.getQuranicSeniorityId() != null)
                entity.setQuranicSeniorityId(encouragementDTO.getQuranicSeniorityId());
            if (encouragementDTO.getRelatedPersonnelId() != null)
                entity.setRelatedPersonnelId(encouragementDTO.getRelatedPersonnelId());
            // entity.setCreatedAt(dto.getCreatedAt());
            if (encouragementDTO.getAmount() != null)
                entity.setAmount(encouragementDTO.getAmount());
            //  entity.setUpdatedAt(dto.getUpdatedAt());
            if (encouragementDTO.getQuranicEncouragementEffectiveDate() != null)
                entity.setQuranicEncouragementEffectiveDate(encouragementDTO.getQuranicEncouragementEffectiveDate());
            QuranicEncouragement quranicEncouragement = repository.save(entity);
            if (files != null) {
                attachmentService.addAttachmentForEncouragement(files, FileAttachmentDTO.builder().quranicEncouragementId(quranicEncouragement.getQuranicEncouragementId()).build());
            }
            if (deleteFileIdList != null) {
                deleteFileIdList.forEach(id -> {
                    try {
                        attachmentService.deleteAttachmentFile(UUID.fromString(id));
                    } catch (GeneralException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return quranicEncouragement;
        }else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }

    public List<QuranicEncouragementDTO> getQuranicEncouragements(String seniorityId, UUID registrarId, UUID relatedPersonnelId, Integer amount, java.sql.Date createdAt) {

        Specification<QuranicEncouragement> spec = Specification.where(null);

        if (seniorityId != null) {
            spec = spec.and(QuranicEncouragementSpecification.filterBySeniorityId(seniorityId));
        }
        if (amount != null) {
            spec = spec.and(QuranicEncouragementSpecification.filterByAmount(amount));
        }
        if (createdAt != null) {
            spec = spec.and(QuranicEncouragementSpecification.filterByCreatedAt(createdAt));
        }
        if (registrarId != null) {
            spec = spec.and(QuranicEncouragementSpecification.filterByRegistrarId(registrarId));
        }
        if (relatedPersonnelId != null) {
            spec = spec.and(QuranicEncouragementSpecification.filterByRelatedPersonnelId(relatedPersonnelId));
        }

        // اجرای جستجو با فیلترهای اعمال شده
        List<QuranicEncouragement> encouragements = repository.findAll(spec);

        // تبدیل موجودیت‌ها به DTO
        return encouragements.stream().map(this::convertToQuranicEncouragementDTO).collect(Collectors.toList());
    }


    private QuranicEncouragementDTO convertToQuranicEncouragementDTO(QuranicEncouragement quranicEncouragement) {
        QuranicEncouragementDTO dto = new QuranicEncouragementDTO();

        dto.setQuranicEncouragementId(quranicEncouragement.getQuranicEncouragementId());
        dto.setQuranicSeniorityId(quranicEncouragement.getQuranicSeniorityId());
        dto.setAmount(quranicEncouragement.getAmount());
        dto.setCreatedAt(quranicEncouragement.getCreatedAt());
        dto.setUpdatedAt(quranicEncouragement.getUpdatedAt());
        dto.setQuranicEncouragementEffectiveDate(quranicEncouragement.getQuranicEncouragementEffectiveDate());
        // مقداردهی registrarPersonnelId اگر مقدار نال نبود
        Optional.ofNullable(quranicEncouragement.getRegistrarPersonnelId()).map(id -> personnelService.findByPersonnelIdList(Collections.singletonList(id))).filter(list -> !list.isEmpty()).map(list -> list.get(0)).ifPresent(dto::setRegistrarPersonnelId);

        // مقداردهی relatedPersonnelId اگر مقدار نال نبود
        Optional.ofNullable(quranicEncouragement.getRelatedPersonnelId()).map(id -> personnelService.findByPersonnelIdList(Collections.singletonList(id))).filter(list -> !list.isEmpty()).map(list -> list.get(0)).ifPresent(dto::setRelatedPersonnelId);

        return dto;
    }


    public void deleteQuranicEncouragement(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException();
        }else {
            repository.deleteById(id);
            List<Attachment> attachmentGetDTOList = attachmentService.getAllEncouragementAttachments(PageRequest.of(0, 9000), FileAttachmentDTO.builder().quranicEncouragementId(id).build(), true);
            attachmentGetDTOList.forEach(attachmentGetDTO -> {
                try {
                    attachmentService.deleteAttachment(attachmentGetDTO.getAttachmentId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public Optional<QuranicEncouragement> getQuranicEncouragementById(UUID id) {
        return repository.findById(id);
    }

    public Page<QuranicEncouragementDTOV2> getList(QuranicEncouragementFilterDTOV2 quranicEncouragementDTO, Integer pageSize, Integer pageNumber) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (quranicEncouragementDTO.getRankTypeCodeList() != null && quranicEncouragementDTO.getRankTypeCodeList().size() != 0) {
            quranicEncouragementDTO.setPersonnel(PersonnelFilterDTOV2.builder().personnelRankCodeList(quranicEncouragementDTO.getRankTypeCodeList()).type("basePersonnel").build());
        }
        Page<QuranicEncouragementDTOV2> resultPage = quranicEncouragementDAO.getList(PageRequest.of(pageNumber, pageSize),
                quranicEncouragementDTO.getFromDate() != null ? LocalDate.parse(quranicEncouragementDTO.getFromDate(), formatter) : null,
                quranicEncouragementDTO.getToDate() != null ? LocalDate.parse(quranicEncouragementDTO.getToDate(), formatter) : null,
                quranicEncouragementDTO.getQuranicSeniorityType(), quranicEncouragementDTO.getQuranicEncouragementAmount(), quranicEncouragementDTO.getQuranicSeniorityAmount());
        List<QuranicEncouragementDTOV2> resultList = resultPage.getContent();
        List<QuranicEncouragementDTOV2> finalResultList = new ArrayList<>();
        //filtering result with personnel entry
        if (quranicEncouragementDTO.getPersonnel() != null) {
            List<? extends BasePersonnelDTO> personnelList = personnelService.getFilteredPersonnel(quranicEncouragementDTO.getPersonnel());
            if (!personnelList.isEmpty()) {
                for (QuranicEncouragementDTOV2 dto : resultList) {
                    for (BasePersonnelDTO person : personnelList) {
                        boolean condition = String.valueOf(dto.getQuranicPersonnelId()).equals(person.getPersonnelId());
                        if (condition) {
                            finalResultList.add(dto);
                        }
                    }
                }
            }
        } else {
            finalResultList.addAll(resultList);
        }
        if (!resultList.isEmpty()) {
            //filling person information and files
            for (QuranicEncouragementDTOV2 dto : resultList) {
                PersonnelDTO personnel = personnelService.findById(dto.getQuranicPersonnelId());
                List<Attachment> attachmentGetDTOList = attachmentService.getAllEncouragementAttachments(PageRequest.of(0, 9000), FileAttachmentDTO.builder().quranicEncouragementId(dto.getQuranicEncouragementId()).build(), false);
                dto.setFiles(attachmentGetDTOList);
                dto.setPersonnel(personnel);
            }
        }

        return new PageImpl<>(finalResultList, PageRequest.of(pageNumber, pageSize), resultPage.getTotalElements());
    }

    public Integer getQuranicEncouragementAmountByPersonnelOrganizationId(UUID organizationId) {
        return Optional.ofNullable(repository.findByRelatedPersonnelId(organizationId)).orElse(Collections.emptyList()).stream().filter(Objects::nonNull).map(QuranicEncouragement::getAmount).mapToInt(Integer::intValue).sum();
    }
}
