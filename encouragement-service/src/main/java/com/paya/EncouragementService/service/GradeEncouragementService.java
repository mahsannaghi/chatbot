package com.paya.EncouragementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.BasePersonnelDTO;
import com.paya.EncouragementService.dto.FileAttachmentDTO;
import com.paya.EncouragementService.dto.GradeEncouragementInsertDTO;
import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.v2.GradeEncouragementDTOV2;
import com.paya.EncouragementService.dto.v2.GradeEncouragementFilterDTOV2;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.entity.Attachment;
import com.paya.EncouragementService.entity.GradeEncouragement;
import com.paya.EncouragementService.enumeration.DegreeEnum;
import com.paya.EncouragementService.repository.GradeEncouragementRepository;
import com.paya.EncouragementService.repository.v2.GradeEncouragementDAO;
import com.paya.EncouragementService.service.v2.AttachmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GradeEncouragementService {


    private final GradeEncouragementRepository repository;
    private final GradeEncouragementDAO gradeEncouragementDAO;
    private final PersonnelService personnelService;
    private final UpgradeDegreeSeniorityService upgradeDegreeSeniorityService;
    private final EducationalLevelGradeService educationalLevelGradeService;
    private final AttachmentService attachmentService;
    private final AuthService authService;

    public GradeEncouragementService(GradeEncouragementRepository repository, GradeEncouragementDAO gradeEncouragementDAO, PersonnelService personnelService, UpgradeDegreeSeniorityService upgradeDegreeSeniorityService, EducationalLevelGradeService educationalLevelGradeService, AttachmentService attachmentService, AuthService authService) {
        this.repository = repository;
        this.gradeEncouragementDAO = gradeEncouragementDAO;
        this.personnelService = personnelService;
        this.upgradeDegreeSeniorityService = upgradeDegreeSeniorityService;
        this.educationalLevelGradeService = educationalLevelGradeService;
        this.attachmentService = attachmentService;
        this.authService = authService;
    }

    @Transactional
    public GradeEncouragement createOrUpdateGradeEncouragement(String dto, List<MultipartFile> files, List<String> deleteFileIdList) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        GradeEncouragementInsertDTO encouragementDTO = objectMapper.readValue(dto, GradeEncouragementInsertDTO.class);
        GradeEncouragement entity;
        if (encouragementDTO != null) {
            if (encouragementDTO.getGradeEncouragementId() != null)
                entity = repository.findById(encouragementDTO.getGradeEncouragementId()).orElseThrow(EntityNotFoundException::new);
            else {
                entity = new GradeEncouragement();
                PersonnelDTO currentUser = authService.getCurrentUserProfile().getUserInfo();
                entity.setGradeEncouragementRegistrarPersonnelId(UUID.fromString(currentUser.getPersonnelId()));
            }
            if (encouragementDTO.getGradeEncouragementNewGrade() != null) {
                educationalLevelGradeService.getRankAndMaxAmount(encouragementDTO.getGradeEncouragementOldGrade(), encouragementDTO.getGradeEncouragementNewGrade());
                entity.setGradeEncouragementNewGrade(encouragementDTO.getGradeEncouragementNewGrade());
            }
            if (encouragementDTO.getGradeEncouragementRelatedPersonnelId() != null)
                entity.setGradeEncouragementRelatedPersonnelId(encouragementDTO.getGradeEncouragementRelatedPersonnelId());
            if (encouragementDTO.getGradeEncouragementEffectiveDate() != null)
                entity.setGradeEncouragementEffectiveDate(encouragementDTO.getGradeEncouragementEffectiveDate());
            if (encouragementDTO.getGradeEncouragementSeniorityAmount() != null)
                entity.setGradeEncouragementSeniorityAmount(encouragementDTO.getGradeEncouragementSeniorityAmount());
            else
                entity.setGradeEncouragementSeniorityAmount(upgradeDegreeSeniorityService.getMaxAmountWithLastDegreeAndNewDegree(encouragementDTO.getGradeEncouragementOldGrade(), encouragementDTO.getGradeEncouragementNewGrade()));
            GradeEncouragement gradeEncouragement = repository.save(entity);
            if (files != null) {
                attachmentService.addAttachmentForEncouragement(files, FileAttachmentDTO.builder().gradeEncouragementId(gradeEncouragement.getGradeEncouragementId()).build());
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
            return gradeEncouragement;
        }else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }


    public void deleteGradeEncouragement(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException();
        }else {
            repository.deleteById(id);
            List<Attachment> attachmentGetDTOList = attachmentService.getAllEncouragementAttachments(PageRequest.of(0, 9000), FileAttachmentDTO.builder().gradeEncouragementId(id).build(), true);
            attachmentGetDTOList.forEach(attachmentGetDTO -> {
                try {
                    attachmentService.deleteAttachment(attachmentGetDTO.getAttachmentId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public Optional<GradeEncouragement> getGradeEncouragementById(UUID id) {
        return repository.findById(id);
    }


    public Page<GradeEncouragementDTOV2> getList(GradeEncouragementFilterDTOV2 gradeEncouragementFilterDTO, Integer pageSize, Integer pageNumber) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Page<GradeEncouragementDTOV2> resultPage = gradeEncouragementDAO.getList(PageRequest.of(pageNumber, pageSize),
                gradeEncouragementFilterDTO.getFromDate() != null ? LocalDate.parse(gradeEncouragementFilterDTO.getFromDate(), formatter) : null,
                gradeEncouragementFilterDTO.getToDate() != null ? LocalDate.parse(gradeEncouragementFilterDTO.getToDate(), formatter) : null,
                gradeEncouragementFilterDTO.getGradeEncouragementNewGrade(), gradeEncouragementFilterDTO.getGradeEncouragementSeniorityAmount());

        if (gradeEncouragementFilterDTO.getRankTypeCodeList() != null && gradeEncouragementFilterDTO.getRankTypeCodeList().size() != 0) {
            gradeEncouragementFilterDTO.setPersonnel(PersonnelFilterDTOV2.builder().personnelRankCodeList(gradeEncouragementFilterDTO.getRankTypeCodeList()).type("basePersonnel").build());
        }
        List<GradeEncouragementDTOV2> resultList = resultPage.getContent();
        List<GradeEncouragementDTOV2> finalResultList = new ArrayList<>();
        //filtering personnel with entries
        if (gradeEncouragementFilterDTO.getPersonnel() != null) {
            List<? extends BasePersonnelDTO> personnelList = personnelService.getFilteredPersonnel(gradeEncouragementFilterDTO.getPersonnel());
            if (!personnelList.isEmpty()) {
                for (GradeEncouragementDTOV2 dto : resultList) {
                    for (BasePersonnelDTO person : personnelList) {
                        boolean condition = String.valueOf(dto.getGradeEncouragementRelatedPersonnelId()).equals(person.getPersonnelId());
                        if (condition) {
                            finalResultList.add(dto);
                        }
                    }
                }
            }
        } else {
            finalResultList.addAll(resultList);
        }
        if (!finalResultList.isEmpty()) {
//            filling Personnel Degree and files
            for (GradeEncouragementDTOV2 dto : finalResultList) {
                PersonnelDTO personnelDto = personnelService.findById(dto.getGradeEncouragementRelatedPersonnelId());
                List<Attachment> attachmentGetDTOList = attachmentService.getAllEncouragementAttachments(PageRequest.of(0, 9000), FileAttachmentDTO.builder().gradeEncouragementId(dto.getGradeEncouragementId()).build(), false);
                if (personnelDto != null) {
                    dto.setPersonnel(personnelDto);
                    Integer lastDegree = personnelDto.getPersonnelDegreeCode();
                    Integer newDegree = dto.getGradeEncouragementNewGrade();

                    if (lastDegree != null && newDegree != null) {
//                        Integer maxAmount = upgradeDegreeSeniorityService.getMaxAmountWithLastDegreeAndNewDegree(lastDegree, newDegree);
                        DegreeEnum newGradeTitle = Arrays.stream(DegreeEnum.values()).filter(item -> item.getDegreeCode().equals(newDegree)).findFirst().orElseThrow();
                        dto.setGradeEncouragementNewGradeTitle(newGradeTitle.getDegreeTitle());
                    }
                }
                if (!attachmentGetDTOList.isEmpty()) {
                    dto.setFiles(attachmentGetDTOList);
                }
            }
//            filtering with new grade title
            if (gradeEncouragementFilterDTO.getGradeEncouragementNewGradeTitle() != null) {
                finalResultList = finalResultList.stream()
                        .filter(dto -> dto.getGradeEncouragementNewGradeTitle().contains(gradeEncouragementFilterDTO.getGradeEncouragementNewGradeTitle()))
                        .toList();
            }
            //filtering with seniority amount
//            if (gradeEncouragementFilterDTO.getSeniorityAmount() != null) {
//                finalResultList = finalResultList.stream()
//                        .filter(dto -> dto.getGradeEncouragementSeniorityAmount() != null && dto.getGradeEncouragementSeniorityAmount().equals(gradeEncouragementFilterDTO.getSeniorityAmount()))
//                        .toList();
//            }
        }
        return new PageImpl<>(finalResultList, PageRequest.of(pageNumber, pageSize), resultPage.getTotalElements());
    }
}
