package com.paya.EncouragementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.netflix.config.validation.ValidationException;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.EncouragementSpecifications;
import com.paya.EncouragementService.dto.*;
import com.paya.EncouragementService.dto.v2.EncouragementFilterDTOV2;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.dto.v2.PersonnelListEncouragementFilterDTO;
import com.paya.EncouragementService.entity.*;
import com.paya.EncouragementService.enumeration.*;
import com.paya.EncouragementService.feign.auth.PunishmentFeign;
import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
import com.paya.EncouragementService.repository.EncouragementRepository;
import com.paya.EncouragementService.service.v2.AttachmentService;
import com.paya.EncouragementService.utility.PaginationUtils;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;
import com.github.javaparser.*;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EncouragementService {
    private final EncouragementRepository encouragementRepository;
    private final EncouragementReasonTypeRepository encouragementReasonTypeRepository;
    private final PersonnelService personnelService;
    private final EncouragementReviewService encouragementReviewService;
    private final RegistrarPowerLimitsService registrarPowerLimitsService;
    private final EncouragementTypeService encouragementTypeService;
    private final AuthService authService;
    private final PunishmentFeign punishmentFeign;
    private final EncouragementReasonTypeService encouragementReasonTypeService;
    private final AttachmentService attachmentService;

    private final List<Map<String, List<String>>> personnelCacheList = new ArrayList<>();
    @Value("${encouragement.typeOfPersonnelDTOSending}")
    private String typeOfPersonnelDTOSending;

    @Value("${encouragement.typeOfBasePersonnelDTOSending}")
    private String typeOfBasePersonnelDTOSending;
    @Value("${encouragement.typeOfManagerDTOSending}")
    private String typeOfPersonnelDTOWithManagerSending;

    public PaginationResponseDTO<? extends BasePersonnelDTO> getPersonnelListEncouragement(PersonnelListEncouragementFilterDTO dto, PageRequest pageRequest) throws ExecutionException, InterruptedException {
        PersonnelDTO personnelDTO = authService.getCurrentUserProfile().getUserInfo();
        PersonnelResponseDTO resultResponse = new PersonnelResponseDTO();
        List<? extends BasePersonnelDTO> filteredList = new ArrayList<>();
        String currentUserRole = authService.getCurrentUserProfile().getCurrentRole();
        if (Objects.equals(RoleConstant.ROLE.fromValue(currentUserRole), RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST)) {
            PersonnelDTO personnelDto = PersonnelDTO.builder().type(typeOfPersonnelDTOSending).build();
            resultResponse = personnelService.getPersonnel(personnelDto);
        } else if (Objects.equals(RoleConstant.ROLE.fromValue(currentUserRole), RoleConstant.ROLE.EXECUTIVE_MANAGER)) {
            PersonnelDTO personnelDto = PersonnelDTO.builder().personnelUnitCode(personnelDTO.getPersonnelUnitCode()).type(typeOfPersonnelDTOSending).build();
            resultResponse = personnelService.getPersonnel(personnelDto);
        }
        if (resultResponse.getPersonnelDTOList() != null) {
            filteredList = resultResponse.getPersonnelDTOList().stream().filter(eachPersonnelDTO -> isMatching((PersonnelDTO) eachPersonnelDTO, dto)).collect(Collectors.toList());
            resultResponse.setPersonnelDTOList(filteredList);
        }
        return PaginationUtils.paginate(filteredList, pageRequest);
    }


    private boolean isMatching(PersonnelDTO person, PersonnelListEncouragementFilterDTO filter) {
        if (filter.getEncouragedPersonFirstName() != null) {
            filter.setEncouragedPersonFirstName(filter.getEncouragedPersonFirstName().replace("ی", "ي").trim());
//            filter.setPersonnelFirstName(filter.getPersonnelFirstName().replace("گ", "گ"));
            if (!person.getPersonnelFirstName().contains(filter.getEncouragedPersonFirstName())) return false;
        }
        if (filter.getEncouragedPersonLastName() != null) {
            filter.setEncouragedPersonLastName(filter.getEncouragedPersonLastName().replace("ی", "ي").trim());
            if (!person.getPersonnelLastName().contains(filter.getEncouragedPersonLastName())) return false;
        }
        return filter.getEncouragementPersonnelOrganizationId() == null || filter.getEncouragementPersonnelOrganizationId().equals(person.getPersonnelOrganizationID());
    }

    public EncouragementAndPunishmentDTO getPunishmentListOfThisServiceUnit(PunishmentFilterDTOV2 filterDTO, PageRequest pageable) throws Exception {
        List<String> personnelOrgIdList = setSameUnitPersonnelOrganizationId();
        List<String> personnelOrgIdRegistrarLastName;
        List<String> personnelOrgIdPunishedLastName;
        if (filterDTO.getRegistrarPersonLastName() != null) {
            personnelOrgIdRegistrarLastName = personnelService.getPersonnel(PersonnelDTO.builder().personnelLastName(filterDTO.getRegistrarPersonLastName()).type(typeOfBasePersonnelDTOSending).build()).getPersonnelDTOList().stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
            if (personnelOrgIdRegistrarLastName.isEmpty())
                return EncouragementAndPunishmentDTO.builder().punishmentList(new PageImpl<>(new ArrayList<>(), pageable, 0)).build();
            else {
                personnelOrgIdRegistrarLastName.retainAll(personnelOrgIdList);
                filterDTO.setPunishmentRegistrarOrganizationIdList(personnelOrgIdRegistrarLastName);
            }
        }
        if (filterDTO.getPunishedPersonLastName() != null) {
            personnelOrgIdPunishedLastName = personnelService.getPersonnel(PersonnelDTO.builder().personnelLastName(filterDTO.getPunishedPersonLastName()).type(typeOfBasePersonnelDTOSending).build()).getPersonnelDTOList().stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
            if (personnelOrgIdPunishedLastName.isEmpty())
                return EncouragementAndPunishmentDTO.builder().punishmentList(new PageImpl<>(new ArrayList<>(), pageable, 0)).build();
            else {
                personnelOrgIdPunishedLastName.retainAll(personnelOrgIdList);
                filterDTO.setPunishmentPersonnelOrganizationIdList(personnelOrgIdPunishedLastName);
            }
        }
        filterDTO.setPunishmentPersonnelOrganizationIdList(personnelOrgIdList);

        List<PunishmentDTO> punishmentDTOList = setPunishment(filterDTO, pageable.getPageSize(), pageable.getPageNumber());
        return EncouragementAndPunishmentDTO.builder().punishmentList(new PageImpl<>(punishmentDTOList == null ? new ArrayList<>() : punishmentDTOList, pageable, punishmentDTOList != null ? punishmentDTOList.size() : 0)).build();
    }

    private List<String> setSameUnitPersonnelOrganizationId() throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_".concat(RoleConstant.ROLE.EXECUTIVE_MANAGER.name())))) {
            PersonnelDTO personnelDto = authService.getCurrentUserProfile().getUserInfo();
            List<String> personnel;
            if (personnelDto != null) {
                String personnelUnitCode = personnelDto.getPersonnelUnitCode();
                boolean hasThisKey = personnelCacheList.stream().anyMatch(map -> map.containsKey(personnelUnitCode));
                if (!hasThisKey) {
                    personnel = personnelService.getPersonnel(PersonnelDTO.builder().personnelUnitCode(personnelUnitCode).type(typeOfBasePersonnelDTOSending).build()).getPersonnelDTOList().stream().map(BasePersonnelDTO::getPersonnelOrganizationID).toList();
                    personnelCacheList.add(Map.of(personnelUnitCode, personnel));
                } else
                    personnel = personnelCacheList.stream().filter(map -> map.containsKey(personnelUnitCode)).map(map2 -> map2.get(personnelUnitCode)).findAny().orElse(null);
                return personnel;
            } else
                throw new ValidationException("لطفا دوباره لاگین کنید. ");
        }
        throw new ValidationException("این سرویس برای شما مجاز نمی باشد .");
    }

    public EncouragementAndPunishmentDTO getEncouragementList(EncouragementFilterDTOV2 filterDTO, Pageable pageable) throws Exception {
        if (filterDTO != null) {
            List<? extends BasePersonnelDTO> basePersonnelWithFirstName = null;
            List<? extends BasePersonnelDTO> basePersonnelWithLastName = null;
            List<String> firstNamePersonnelOrgId = null;
            List<String> lastNamePersonnelOrgId = null;
            if (filterDTO.getEncouragedPersonFirstName() != null) {
                basePersonnelWithFirstName = personnelService.getFilteredPersonnel(PersonnelFilterDTOV2.builder().personnelFirstName(filterDTO.getEncouragedPersonFirstName()).type(typeOfBasePersonnelDTOSending).build());
                if (basePersonnelWithFirstName == null || basePersonnelWithFirstName.size() == 0)
                    return EncouragementAndPunishmentDTO.builder().encouragementList(new PageImpl<>(new ArrayList<>())).build();
                else {
                    firstNamePersonnelOrgId = basePersonnelWithFirstName.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                    filterDTO.setEncouragementPersonnelOrganizationIdList(firstNamePersonnelOrgId);
                }
            }
            if (filterDTO.getEncouragedPersonLastName() != null) {
                basePersonnelWithLastName = personnelService.getFilteredPersonnel(PersonnelFilterDTOV2.builder().personnelLastName(filterDTO.getEncouragedPersonLastName()).type(typeOfBasePersonnelDTOSending).build());
                if (basePersonnelWithLastName == null || basePersonnelWithLastName.size() == 0)
                    return EncouragementAndPunishmentDTO.builder().encouragementList(new PageImpl<>(new ArrayList<>())).build();
                else {
                    lastNamePersonnelOrgId = basePersonnelWithLastName.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                    filterDTO.setEncouragementPersonnelOrganizationIdList(lastNamePersonnelOrgId);
                }
            }
            if (filterDTO.getRegistrarPersonLastName() != null) {
                basePersonnelWithLastName = personnelService.getFilteredPersonnel(PersonnelFilterDTOV2.builder().personnelLastName(filterDTO.getRegistrarPersonLastName()).type(typeOfBasePersonnelDTOSending).build());
                if (basePersonnelWithLastName == null || basePersonnelWithLastName.size() == 0)
                    return EncouragementAndPunishmentDTO.builder().encouragementList(new PageImpl<>(new ArrayList<>())).build();
                else {
                    lastNamePersonnelOrgId = basePersonnelWithLastName.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                    filterDTO.setEncouragementRegistrarOrganizationIdList(lastNamePersonnelOrgId);
                }
            }
            if (filterDTO.getApproverPersonLastName() != null) {
                basePersonnelWithLastName = personnelService.getFilteredPersonnel(PersonnelFilterDTOV2.builder().personnelLastName(filterDTO.getApproverPersonLastName()).type(typeOfBasePersonnelDTOSending).build());
                if (basePersonnelWithLastName == null || basePersonnelWithLastName.size() == 0)
                    return EncouragementAndPunishmentDTO.builder().encouragementList(new PageImpl<>(new ArrayList<>())).build();
                else {
                    lastNamePersonnelOrgId = basePersonnelWithLastName.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                    filterDTO.setEncouragementApproverOrganizationIdList(lastNamePersonnelOrgId);
                }
            }
            if (basePersonnelWithFirstName != null && basePersonnelWithFirstName.size() > 0 && basePersonnelWithLastName != null) {
                if (firstNamePersonnelOrgId.size() > 0 && lastNamePersonnelOrgId.size() > 0)
                    firstNamePersonnelOrgId.retainAll(lastNamePersonnelOrgId);
                filterDTO.setEncouragementPersonnelOrganizationIdList(firstNamePersonnelOrgId);
            }
            if (filterDTO.getAllServiceUnitPersonnelEncouragement()) {
                List<String> sameUnitPersonnelOrganizationId = setSameUnitPersonnelOrganizationId();
                if (sameUnitPersonnelOrganizationId != null && !sameUnitPersonnelOrganizationId.isEmpty())
                    filterDTO.setEncouragementPersonnelOrganizationIdList(sameUnitPersonnelOrganizationId);
            }
            List<PunishmentDTO> punishmentDTOList = null;
            if (filterDTO.getWithPunishment())
                punishmentDTOList = setPunishment(PunishmentFilterDTOV2.builder().punishmentPersonnelOrganizationId(filterDTO.getEncouragementPersonnelOrganizationId()).punishmentRegistrarOrganizationId(filterDTO.getEncouragementRegistrarOrganizationId()).punishmentRegistrarOrPunishedOrganizationId(filterDTO.getEncouragementRegistrarOrEncouragedOrganizationId()).punishmentStatusNot(EncouragementResultEnum.DRAFT.getCode()).build(), pageable.getPageSize(), pageable.getPageNumber());
            Page<Encouragement> encouragementPage = encouragementRepository.findAll(EncouragementSpecifications.filterBySpecification(filterDTO), pageable);
            if (filterDTO.getEncouragementPersonnelOrganizationId() != null)
                encouragementRepository.changeHasUserSeenEncouragementTrue(encouragementPage.toList());
            return setPageDTOFromEncouragementPage(filterDTO, pageable, encouragementPage, punishmentDTOList);
        }
        return EncouragementAndPunishmentDTO.builder().encouragementList(new PageImpl<>(new ArrayList<>())).build();
    }

    private EncouragementAndPunishmentDTO setPageDTOFromEncouragementPage(EncouragementFilterDTOV2 filterDTO, Pageable pageable, Page<Encouragement> encouragementPage, List<PunishmentDTO> punishmentDTOList) throws ExecutionException, InterruptedException {
        List<EncouragementFilterDTOV2> encouragementList = new ArrayList<>();
        setEncouragementDtoList(encouragementPage.getContent(), encouragementList, filterDTO.getWithFile());
        if (filterDTO.getEncouragementReason() != null) {
            encouragementList = encouragementList.stream().filter(item -> item.getEncouragementReason().contains(filterDTO.getEncouragementReason())).collect(Collectors.toList());
        }
        if (filterDTO.getEncouragementType() != null) {
            encouragementList = encouragementList.stream().filter(item -> item.getEncouragementType().contains(filterDTO.getEncouragementType())).collect(Collectors.toList());
        }
        return EncouragementAndPunishmentDTO.builder().encouragementList(new PageImpl<>(encouragementList, pageable, encouragementList.size()))
                .punishmentList(filterDTO.getAllServiceUnitPersonnelEncouragement() ? null :
                        new PageImpl<>
                                (punishmentDTOList == null ? new ArrayList<>() : punishmentDTOList,
                                        pageable,
                                        punishmentDTOList == null ? 0 : punishmentDTOList.size())).build();
    }

    private void setEncouragementDtoList(List<Encouragement> list, List<EncouragementFilterDTOV2> encouragementList, Boolean withFile) throws ExecutionException, InterruptedException {
        for (Encouragement encouragement : list) {
            EncouragementFilterDTOV2 encouragementDTO = new EncouragementFilterDTOV2();
            EncouragementReasonDTO reasonDTO = encouragementReasonTypeRepository.getReasonWithReasonTypeId(encouragement.getEncouragementReasonTypeId());
            EncouragementTypeDTO typeDTO = encouragementReasonTypeRepository.getTypeWithReasonTypeId(encouragement.getEncouragementReasonTypeId());
            PersonnelDTO encouragedPerson = (PersonnelDTO) personnelService.findByOrganizationId(encouragement.getEncouragementPersonnelOrganizationId(), typeOfPersonnelDTOSending);
            if (encouragedPerson != null) {
                encouragementDTO.setEncouragedPersonFirstName(encouragedPerson.getPersonnelFirstName());
                encouragementDTO.setEncouragedPersonLastName(encouragedPerson.getPersonnelLastName());
                encouragementDTO.setEncouragementPersonnelOrganizationId(encouragedPerson.getPersonnelOrganizationID());
                encouragementDTO.setEncouragedPersonUnitCode(encouragedPerson.getPersonnelUnitCode());
                encouragementDTO.setEncouragedPersonRankTypePersianName(encouragedPerson.getPersonnelRankTypePersianName());
                encouragementDTO.setEncouragedPersonRankTypeCivilianCode(encouragedPerson.getPersonnelRankTypeCivilianCode());
            }
            PersonnelDTO registrarPerson = (PersonnelDTO) personnelService.findByOrganizationId(encouragement.getEncouragementRegistrarOrganizationId(), typeOfPersonnelDTOSending);
            if (registrarPerson != null) {
                encouragementDTO.setRegistrarPersonFirstName(registrarPerson.getPersonnelFirstName());
                encouragementDTO.setRegistrarPersonLastName(registrarPerson.getPersonnelLastName());
            }
            PersonnelDTO approverPerson = (PersonnelDTO) personnelService.findByOrganizationId(encouragement.getEncouragementApproverOrganizationId(), typeOfPersonnelDTOSending);
            if (approverPerson != null) {
                encouragementDTO.setApproverPersonFirstName(approverPerson.getPersonnelFirstName());
                encouragementDTO.setApproverPersonLastName(approverPerson.getPersonnelLastName());
            }
            if (reasonDTO != null) {
                encouragementDTO.setEncouragementReason(reasonDTO.getEncouragementReasonTitle());
                encouragementDTO.setEncouragementReasonId(reasonDTO.getEncouragementReasonId());
            }
            if (typeDTO != null) {
                encouragementDTO.setEncouragementType(typeDTO.getEncouragementTypeTitle());
                encouragementDTO.setEncouragementTypeId(typeDTO.getEncouragementTypeId());
            }
            this.mapEntityToDTO(encouragement, encouragementDTO);
            if (withFile) {
                FileAttachmentDTO fileAttachmentDTO = FileAttachmentDTO.builder().encouragementId(encouragement.getEncouragementId()).build();
                List<Attachment> attachmentGetDTOList = attachmentService.getAllEncouragementAttachments(PageRequest.of(0, 9000), fileAttachmentDTO, false);
                encouragementDTO.setAttachmentList(attachmentGetDTOList);
            }
            encouragementList.add(encouragementDTO);
        }
    }

    private List<PunishmentDTO> setPunishment(PunishmentFilterDTOV2 dto, Integer pageSize, Integer pageNumber) {
        try {
            return punishmentFeign.getAllPunishmentsForEncouragement(dto, pageSize, pageNumber);
        } catch (FeignException | ParseException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(rollbackOn = Exception.class)
    public List<Encouragement> addOrUpdateEncouragement(EncouragementCreateRequestSpecial request, PersonnelDTO registrar) throws Exception {
        String currentUserRole = authService.getCurrentUserProfile().getCurrentRole();
        if (request != null && request.getEncouragementRelatedPersonnelOrganizationIds() != null) {
            List<Encouragement> encouragements = new ArrayList<>();
            EncouragementReasonType reasonType = null;
            if (request.getReasonId() != null && request.getType() != null && request.getType().getTypeId() != null)
                reasonType = this.findReasonType(request.getReasonId(), request.getType().getTypeId());
            validate(request.getAmount(), reasonType);
            for (String personnelOrganizationId : request.getEncouragementRelatedPersonnelOrganizationIds()) {
                PersonnelDTO encouragedPerson = (PersonnelDTO) personnelService.findByOrganizationId(personnelOrganizationId, typeOfPersonnelDTOWithManagerSending);
                if (!encouragedPerson.getPersonnelMembershipCode().equals(MembershipType.PERMANENT.getCode()))
                    throw new ValidationException("شخص انتخاب شده، رسمی نمی باشد.");
                /** @CheckBothAreExistsInSameOrganization */
                if ((registrar.getPersonnelUnitCode() == null || encouragedPerson.getPersonnelUnitCode() == null) || Objects.equals(registrar.getPersonnelUnitCode(), encouragedPerson.getPersonnelUnitCode())) {
                    /** @CheckExistsRegistrarInPersonnelManager */
                    if (encouragedPerson.getPersonnelManager() != null && encouragedPerson.getPersonnelManager().getPersonnelManagerOrganizationIdList().stream().anyMatch(managerOrganizationId -> managerOrganizationId.equals(registrar.getPersonnelOrganizationID()))) {
                        Encouragement encouragement = createOrUpdateEncouragement(request, reasonType, registrar.getPersonnelOrganizationID(), personnelOrganizationId);
                        encouragement.setEncouragementTypeCategory(TypeCategoryEnum.NORMAL);
                        if (encouragement.getEncouragementStatus() != null && !encouragement.getEncouragementStatus().equals(ReviewResultEnum.SENT_FOR_REGISTRAR_CORRECTION.getCode())) {
                            if (!DraftEnum.fromCode(request.getEncouragementDraft()).equals(DraftEnum.Sent))
                                encouragement.setEncouragementStatus(EncouragementResultEnum.DRAFT.getCode());
                            else {
                                if (encouragement.getEncouragementStatus() == null || encouragement.getEncouragementStatus().equals(EncouragementResultEnum.DRAFT.getCode()))
                                    encouragement.setEncouragementStatus(EncouragementResultEnum.UNDER_REVIEW.getCode());
                                if (DraftEnum.fromCode(request.getEncouragementDraft()).equals(DraftEnum.Sent))
                                    encouragement.setEncouragementSentDraftDate(LocalDate.now());
                            }
                        }
                        encouragements.add(encouragement);
                    } else
                        throw new GeneralException(String.format("شما در سلسله مراتب پرسنل %s قرار ندارید.", encouragedPerson.getPersonnelOrganizationID()));
                } else
                    throw new GeneralException(String.format("تشویق شونده ی %s در یگان شما نمی باشد.", encouragedPerson.getPersonnelOrganizationID()));
            }
            List<Encouragement> resultList = encouragementRepository.saveAll(encouragements);
            if (!resultList.isEmpty() && request.getEncouragementDraft().equals(DraftEnum.Sent.getCode())) {
                resultList.forEach(encouragement -> {
                    try {
                        if (request.getEncouragementDraft() != null && request.getEncouragementDraft().equals(DraftEnum.Sent.getCode())) {
                            if (!encouragement.getIsEncouragementSeen()) {
                                List<EncouragementReview> list = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithReviewerType(encouragement.getEncouragementRegistrarOrganizationId(), encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                                if (list.isEmpty()) {
                                    this.calculator(encouragement, request.getType().getTypeId(), registrar, Boolean.FALSE, request.getEncouragementDraft(), true, false, true, encouragement.getEncouragementRegistrarOrganizationId(), currentUserRole);
                                } else {
                                    this.calculator(encouragement, request.getType().getTypeId(), registrar, Boolean.TRUE, request.getEncouragementDraft(), true, false, true, encouragement.getEncouragementRegistrarOrganizationId(), currentUserRole);
                                    if (encouragement.getEncouragementStatus().equals(ReviewResultEnum.SENT_FOR_REGISTRAR_CORRECTION.getCode()) || encouragement.getEncouragementStatus().equals(EncouragementResultEnum.NEED_FOR_ACCEPT.getCode())) {
                                        EncouragementReview encouragementReview = list.get(0);
                                        encouragementReview.setEncouragementReviewSentDraftDate(LocalDate.now());
                                        encouragementReviewService.changeEncouragementReviewStatus(encouragementReview, ReviewResultEnum.APPROVED.getCode());
                                    }
                                }
                                if (encouragement.getEncouragementStatus().equals(ReviewResultEnum.SENT_FOR_REGISTRAR_CORRECTION.getCode())) {
                                    encouragement.setEncouragementStatus(ReviewResultEnum.UNDER_REVIEW.getCode());
                                }
                                encouragementReviewService.updateSentForRegistrarEncouragementReviewHasBeenSeen(encouragement);
                            } else
                                throw new GeneralException("این تشویق مشاهده شده و قابل ویرایش نیست");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return resultList;
        } else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }

    public void addOrUpdateEncouragementByEncouragementSpecialist(EncouragementCreateRequestSpecial dto, PersonnelDTO registrar, List<MultipartFile> fileList, List<String> finalDeleteFileIdList) throws ExecutionException, InterruptedException, GeneralException {
        EncouragementReasonType reasonType = null;
        if (dto.getReasonId() != null && dto.getType() != null && dto.getType().getTypeId() != null)
            reasonType = this.findReasonType(dto.getReasonId(), dto.getType().getTypeId());

        final var finalReasonType = reasonType;
        String currentUserRole = authService.getCurrentUserProfile().getCurrentRole();
        dto.getEncouragementRelatedPersonnelOrganizationIds().stream().map(personnelOrganizationId -> {
                    try {
                        return createOrUpdateEncouragement(dto, finalReasonType, registrar.getPersonnelOrganizationID(), personnelOrganizationId);
                    } catch (GeneralException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(encouragement -> {
                    encouragement.setEncouragementTypeCategory(TypeCategoryEnum.SPECIALIST);
                    encouragementRepository.save(encouragement);
                    if (encouragement.getEncouragementId() != null) {
                        FileAttachmentDTO fileAttachmentDTO = FileAttachmentDTO.builder().encouragementId(encouragement.getEncouragementId()).build();
                        try {
                            addFile(fileList, finalDeleteFileIdList, fileAttachmentDTO);
                            if (dto.getEncouragementDraft().equals(DraftEnum.Sent.getCode()))
                                this.calculator(encouragement, finalReasonType.getEncouragementTypeId(), registrar, Boolean.FALSE, dto.getEncouragementDraft(), false, false, true, null, currentUserRole);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
    private Encouragement createOrUpdateEncouragement(EncouragementCreateRequestSpecial request, EncouragementReasonType reasonType, String registrarPersonnelOrganizationID, String encouragedPersonnelOrganizationId) throws GeneralException {
        Encouragement encouragement = null;
        UUID encouragementId = null;
        if (request.getEncouragementId() != null) {
            encouragementId = request.getEncouragementId();
        }
        if (encouragementId != null) {
            encouragement = encouragementRepository.findById(encouragementId).orElse(null);
            if (encouragement != null) {
                if (encouragement.getIsEncouragementSeen() && !encouragement.getEncouragementStatus().equals(EncouragementResultEnum.SENT_FOR_CORRECTION.getCode()))
                    throw new ValidationException("این تشویق مشاهده شده است و قابل ویرایش نمی باشد.");
                Optional<EncouragementReasonType> encouragementReasonType = encouragementReasonTypeRepository.findByEncouragementReasonTypeId(encouragement.getEncouragementReasonTypeId());
                if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.DRAFT.getCode()) && encouragementReasonType.isPresent() && !encouragementReasonType.get().isActive())
                    throw new GeneralException("ارتباط نوع و علت غیر فعال شده است.");
//                                if (!encouragement.getEncouragementStatus().equals(ReviewResultEnum.SENT_FOR_REGISTRAR_CORRECTION.getCode()))
//                                    throw new ValidationException("این تشویق ارسال شده است و قابل ویرایش نمی باشد.");
                encouragement.setEncouragementId(request.getEncouragementId());
            }
        }
        if (encouragement == null) {
            if (reasonType != null && !reasonType.isActive())
                throw new GeneralException("ارتباط نوع و علت غیر فعال است.");
            encouragement = new Encouragement();
            Optional<Long> lastNumber = encouragementRepository.findMaxEncouragementNumber();
            if (lastNumber.isPresent())
                encouragement.setEncouragementNumber(String.valueOf(lastNumber.get() + 1));
            else {
                encouragement.setEncouragementNumber(String.valueOf(100000));
            }
            if (!DraftEnum.fromCode(request.getEncouragementDraft()).equals(DraftEnum.Sent))
                encouragement.setEncouragementStatus(EncouragementResultEnum.DRAFT.getCode());
            else {
                encouragement.setEncouragementStatus(EncouragementResultEnum.UNDER_REVIEW.getCode());
                if (DraftEnum.fromCode(request.getEncouragementDraft()).equals(DraftEnum.Sent))
                    encouragement.setEncouragementSentDraftDate(LocalDate.now());
            }
        }
        encouragement.setEncouragementAmount(request.getAmount());
        encouragement.setEncouragementDescription(request.getDescription());
        encouragement.setEncouragementEffectiveDate(request.getEncouragementEffectiveDate() != null ? request.getEncouragementEffectiveDate() : LocalDate.now());
        if (request.getAmountType() != null)
            encouragement.setEncouragementAmountType(request.getAmountType());
        if (registrarPersonnelOrganizationID != null)
            encouragement.setEncouragementRegistrarOrganizationId(registrarPersonnelOrganizationID);
        if (encouragedPersonnelOrganizationId != null)
            encouragement.setEncouragementPersonnelOrganizationId(encouragedPersonnelOrganizationId);
        if (request.getEncouragementDraft() != null) {
            encouragement.setEncouragementDraft(DraftEnum.fromCode(request.getEncouragementDraft()));
        }
        if (reasonType != null && reasonType.getEncouragementReasonTypeId() != null)
            encouragement.setEncouragementReasonTypeId(reasonType.getEncouragementReasonTypeId());
        return encouragement;
    }

    private void validate(Long amount, EncouragementReasonType reasonType) throws GeneralException {
        /** @CheckMaxAmount */
        if (reasonType != null && reasonType.getMaxAmount() != null && !reasonType.getMaxAmount().equals(BigDecimal.ZERO) && amount != null) {
            if ((reasonType.getMaxAmount().compareTo(BigDecimal.valueOf(amount)) < 0)) {
                throw new GeneralException(" تشویق بالاتر از حداکثر  مجاز تعیین شده می باشد . ");
            }
        } else if (reasonType != null && reasonType.getMaxDuration() != null && !reasonType.getMaxDuration().equals(0) && amount != null) {
            if (amount > reasonType.getMaxDuration())
                throw new GeneralException(" تشویق بالاتر از حداکثر  مجاز تعیین شده می باشد . ");
        }
//        if (reasonType != null) {
//            Integer typeCategory = encouragementReasonTypeService.getEncouragementTypeByReasonTypeId(reasonType.getEncouragementReasonTypeId());
//            if (!typeCategory.equals(TypeCategoryEnum.NORMAL.getCode()))
//                throw new GeneralException("شما مجاز به ثبت این نوع تشویق نمی باشید.");
//        }
    }


    @Transactional
    protected void calculator(Encouragement encouragement, UUID typeId, PersonnelDTO personnelReviewCreator, Boolean isUpdateMode, Integer draft, boolean nextManager, boolean deleteRecentManagerReview, boolean checkingRegistrarPowerLimits, String registrarPowerLimitOrganizationId, String currentUserRole) throws Exception {
        if (encouragement != null) {
            PersonnelDTO encouragedPerson = (PersonnelDTO) personnelService.findByOrganizationId(encouragement.getEncouragementPersonnelOrganizationId(), typeOfPersonnelDTOWithManagerSending);
            if (encouragedPerson != null) {
                if (personnelReviewCreator != null) {
                    if (checkingRegistrarPowerLimits) {
                        if (currentUserRole.equals(RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST.getValue())) {
                            Integer encouragedPersonRank = encouragedPerson.getPersonnelRankTypeCode();
                            boolean isGreaterThanSecondLieutenant = encouragedPersonRank >= RankTypeEnum.SECOND_LIEUTENANT.getRankCode();
                            if (isGreaterThanSecondLieutenant) {
                                sendForVedjaCommission(encouragement, personnelReviewCreator);
                            } else
                                this.changeEncouragementStatus(encouragement, EncouragementResultEnum.APPROVED.getCode(), personnelReviewCreator.getPersonnelOrganizationID(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                        } else {
                            Optional<RegistrarPowerLimits> optional = registrarPowerLimitsService.getThisPersonnelOrganizationIdGroupAndEncouragementTypeId(registrarPowerLimitOrganizationId, typeId);
                            if (optional.isPresent()) {
                                boolean amountOfEncouragementIsWithinThePowerLimits = false;
                                RegistrarPowerLimits powerLimits = optional.get();
                                if ((encouragement.getEncouragementAmount() == null && powerLimits.getMaxAmount() == null)) {
                                    amountOfEncouragementIsWithinThePowerLimits = true;
                                } else {
                                    if (encouragement.getEncouragementAmount() != null && (powerLimits.getMaxAmount().compareTo(BigDecimal.valueOf(encouragement.getEncouragementAmount())) >= 0 ||
                                            powerLimits.getMaxDuration() != null && powerLimits.getMaxDuration().compareTo(Math.toIntExact(encouragement.getEncouragementAmount())) >= 0)) {
                                        amountOfEncouragementIsWithinThePowerLimits = true;
                                    }
                                }
                                if (amountOfEncouragementIsWithinThePowerLimits) {
                                    this.changeEncouragementStatus(encouragement, EncouragementResultEnum.APPROVED.getCode(), personnelReviewCreator.getPersonnelOrganizationID(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                                    afterCheckingRegistrarPowerLimit(encouragement, personnelReviewCreator, isUpdateMode, draft, nextManager, deleteRecentManagerReview, encouragedPerson, currentUserRole);
                                } else {
                                    if (encouragedPerson.getPersonnelManager() != null && draft.equals(DraftEnum.Sent.getCode())) {
                                        this.changeEncouragementStatus(encouragement, EncouragementResultEnum.NEED_FOR_ACCEPT.getCode(), personnelReviewCreator.getPersonnelOrganizationID(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                                        afterCheckingRegistrarPowerLimit(encouragement, personnelReviewCreator, isUpdateMode, draft, nextManager, deleteRecentManagerReview, encouragedPerson, currentUserRole);
//                                        forwardEncouragementForNextStep(encouragement, personnelReviewCreator, encouragedPerson, nextManager, deleteRecentManagerReview, currentUserRole);
                                    } else throw new GeneralException("لیست مدیران شخص وارد شده خالی می باشد .");
                                }
                            } else {
                                afterCheckingRegistrarPowerLimit(encouragement, personnelReviewCreator, isUpdateMode, draft, nextManager, deleteRecentManagerReview, encouragedPerson, currentUserRole);
                            }
                        }
                    } else {
                        afterCheckingRegistrarPowerLimit(encouragement, personnelReviewCreator, isUpdateMode, draft, nextManager, deleteRecentManagerReview, encouragedPerson, currentUserRole);
                    }
                } else throw new GeneralException("شخص تشویق کننده یافت نشد .");
            } else throw new GeneralException("شخص تشویق شونده یافت نشد .");
        } else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }

    private void afterCheckingRegistrarPowerLimit(Encouragement encouragement, PersonnelDTO personnelReviewCreator, Boolean isUpdateMode, Integer draft, boolean nextManager, boolean deleteRecentManagerReview, PersonnelDTO encouragedPerson, String currentUserRole) throws GeneralException {
        if (!isUpdateMode) {
            this.createReviewFromEncouragement(encouragement, personnelReviewCreator.getPersonnelOrganizationID(), ReviewResultEnum.APPROVED.getCode(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode(), DraftEnum.Sent.getCode(), null);
        }
        if (encouragedPerson.getPersonnelManager() != null && draft.equals(DraftEnum.Sent.getCode())) {
//            List<EncouragementReview> list = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithReviewerType(personnelReviewCreator.getPersonnelOrganizationID(), encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
//            if (list.isEmpty())
//                this.createReviewFromEncouragement(encouragement, personnelReviewCreator.getPersonnelOrganizationID(), ReviewResultEnum.APPROVED.getCode(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode(), DraftEnum.Sent.getCode(), null);
            forwardEncouragementForNextStep(encouragement, personnelReviewCreator, encouragedPerson, nextManager, deleteRecentManagerReview, currentUserRole);
        } else throw new GeneralException("لیست مدیران شخص وارد شده خالی می باشد .");
    }

    private void forwardEncouragementForNextStep(Encouragement encouragement, PersonnelDTO personnelReviewCreator, PersonnelDTO encouragedPerson, boolean nextManager, boolean deleteRecentManagerReview, String currentUserRole) throws GeneralException {
        PersonnelManagerDTO personnelManager = encouragedPerson.getPersonnelManager();
        List<String> managerPositionList = personnelManager.getPersonnelManagerPositionList();
        Optional<String> creatorPositionInListOfManagersPosition = managerPositionList.stream().filter(position -> JobPositionEnum.fromCode(personnelReviewCreator.getPersonnelJobPositionCode()).equals(JobPositionEnum.fromCode(Integer.parseInt(position)))).findAny();
        if ((currentUserRole.equals(RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST.getValue()) || creatorPositionInListOfManagersPosition.isEmpty()) && !encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode())){
            commissionInput(encouragement, personnelManager.getPersonnelManagerOrganizationIdList().size() - 1 >= 0 ? personnelManager.getPersonnelManagerOrganizationIdList().get(personnelManager.getPersonnelManagerOrganizationIdList().size() - 1) : null, personnelReviewCreator);
            return;
        }
        String creatorPosition = creatorPositionInListOfManagersPosition.get();
        int creatorIndex = managerPositionList.indexOf(creatorPosition);
        List<EncouragementReview> encouragementReviewList = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithReviewerType(personnelReviewCreator.getPersonnelOrganizationID(), encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
        EncouragementReview thisEncouragementReview= null;
        if (encouragementReviewList != null && encouragementReviewList.size() > 0)
            thisEncouragementReview = encouragementReviewList.get(0);
        if (nextManager) {
            forwardNextManager(encouragement, personnelReviewCreator, personnelManager, managerPositionList, creatorIndex, Optional.ofNullable(thisEncouragementReview), deleteRecentManagerReview, encouragedPerson);
        } else {
            forwardPreviousManager(encouragement,personnelReviewCreator, personnelManager, managerPositionList, creatorIndex, Optional.ofNullable(thisEncouragementReview), deleteRecentManagerReview, encouragedPerson);
        }
    }

    private void commissionInput(Encouragement encouragement, String personnelManager, PersonnelDTO personnelReviewCreator) throws GeneralException {
        Optional<EncouragementReview> commissionEncouragementReview = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());
        if (commissionEncouragementReview.isEmpty())
            createReviewFromEncouragement(encouragement, null, ReviewResultEnum.UNDER_COMMISSION_REVIEW.getCode(), ReviewTypeEnum.ORDINARY_COMMISSION.getCode(), DraftEnum.Nothing.getCode(),
                personnelManager);
        else
            updateExistEncouragement(commissionEncouragementReview.get(), encouragement);
        if (!encouragement.getEncouragementStatus().equals(EncouragementResultEnum.NEED_FOR_ACCEPT.getCode()))
            this.changeEncouragementStatus(encouragement, EncouragementResultEnum.UNDER_COMMISSION_REVIEW.getCode(), personnelReviewCreator.getPersonnelOrganizationID(), ReviewTypeEnum.ORDINARY_COMMISSION.getCode());
    }

    private void forwardPreviousManager(Encouragement encouragement, PersonnelDTO personnelReviewCreator, PersonnelManagerDTO personnelManager, List<String> managerPositionList, int creatorIndex, Optional<EncouragementReview> thisEncouragementReview, boolean deleteRecentManagerReview, PersonnelDTO encouragedPerson) throws GeneralException {
        if (creatorIndex - 1 >= 0) {
            Optional<EncouragementReview> nextEncouragementReview = Optional.empty();
            String nextManagerOrganizationId = null;
            String previousManagerOrganizationId = personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex - 1);
            Optional<EncouragementReview> previousEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(previousManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
            if (creatorIndex + 1 <= personnelManager.getPersonnelManagerOrganizationIdList().size() - 1) {
                nextManagerOrganizationId = personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex + 1);
                nextEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(nextManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                nextEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
            }
            if (thisEncouragementReview.isPresent()) {
                EncouragementReview review = thisEncouragementReview.get();
                if (review.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_RECENT_MANAGER_CORRECTION.getCode())) {
                    if (previousManagerOrganizationId.equals(encouragement.getEncouragementRegistrarOrganizationId()))
                        changeEncouragementStatus(encouragement, EncouragementResultEnum.SENT_FOR_CORRECTION.getCode(),null, null);
                }
            }
            Optional<EncouragementReview> commissionEncouragementReview = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());
            commissionEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
            if (!encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode())) {
                if (previousEncouragementReview.isEmpty() && encouragement.getIsEncouragementSeen()) {
                    this.createReviewFromEncouragement(encouragement, previousManagerOrganizationId, ReviewResultEnum.UNDER_REVIEW.getCode(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode(), DraftEnum.Nothing.getCode(), creatorIndex - 2 >= 0 ?
                            personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex - 2) : null);
                    if (previousManagerOrganizationId.equals(encouragement.getEncouragementRegistrarOrganizationId())) {
                        sentForRegistrar(encouragement, personnelManager, creatorIndex, previousManagerOrganizationId);
                    }
                } else {
                    if (!encouragement.getIsEncouragementSeen()) {
                        sentForRegistrar(encouragement, personnelManager, creatorIndex, previousManagerOrganizationId);
                    } else {
                        if (previousEncouragementReview.isPresent()) {
                            EncouragementReview review = previousEncouragementReview.get();
                            if (deleteRecentManagerReview) {
                                encouragementReviewService.deleteEncouragementReview(review.getEncouragementReviewId());
                                forwardNextManager(encouragement, personnelReviewCreator, personnelManager, managerPositionList, creatorIndex, thisEncouragementReview, deleteRecentManagerReview, encouragedPerson);
                            } else {
                                if (!encouragement.getEncouragementRegistrarOrganizationId().equals(review.getEncouragementReviewRegistrarOrganizationId()))
                                    encouragementReviewService.updateExistEncouragementReview(review, encouragement);
                            }
                        }
                        thisEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));
                    }
                }
            } else {
                List<EncouragementReview> approvedNextEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithReviewerType(nextManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                if (!approvedNextEncouragementReview.isEmpty())
                    encouragementReviewService.deleteEncouragementReview(approvedNextEncouragementReview.get(0).getEncouragementReviewId());

            }
        } else
            throw new GeneralException("مدیر قبلی ایی وجود ندارد.");
    }

    private void deleteRegistrarReviewIfExist(Encouragement encouragement) {
        Optional<EncouragementReview> registrarEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(encouragement.getEncouragementRegistrarOrganizationId(), encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
        registrarEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
    }

    private void sentForRegistrar(Encouragement encouragement, PersonnelManagerDTO personnelManager, int creatorIndex, String previousManagerOrganizationId) throws GeneralException {
        String registrarOrganizationId = encouragement.getEncouragementRegistrarOrganizationId();
        int registrarOrgIdIndex = personnelManager.getPersonnelManagerOrganizationIdList().indexOf(registrarOrganizationId);
        if (!previousManagerOrganizationId.equals(encouragement.getEncouragementRegistrarOrganizationId())) {
            Optional<EncouragementReview> previousEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(previousManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
//        nextEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
            previousEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
        }
        changeEncouragementStatus(encouragement, EncouragementResultEnum.SENT_FOR_CORRECTION.getCode(),null, null);
        Optional<EncouragementReview> registrarEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(registrarOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
        if (registrarEncouragementReview.isEmpty())
            this.createReviewFromEncouragement(encouragement, registrarOrganizationId, ReviewResultEnum.UNDER_REVIEW.getCode(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode(), DraftEnum.Nothing.getCode(),
                    registrarOrgIdIndex-1 >= 0 ? personnelManager.getPersonnelManagerOrganizationIdList().get(registrarOrgIdIndex - 1) : null);
        else {
            registrarEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));
        }
    }

    private void forwardNextManager(Encouragement encouragement, PersonnelDTO personnelReviewCreator, PersonnelManagerDTO personnelManager, List<String> managerPositionList, int creatorIndex, Optional<EncouragementReview> thisEncouragementReview, boolean deleteRecentManagerReview, PersonnelDTO encouragedPerson) throws GeneralException {
        if (deleteRecentManagerReview) {
            deleteRegistrarReviewIfExist(encouragement);
        }
        if (creatorIndex + 1 < managerPositionList.size()) {
            String nextManagerOrganizationId = personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex + 1);
            Optional<EncouragementReview> nextEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(nextManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
            if (creatorIndex - 1 >= 0) {
                String previousManagerOrganizationId = personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex - 1);
                Optional<EncouragementReview> previousEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(previousManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
                previousEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
            }
            if (nextEncouragementReview.isPresent()) {
                if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||
                        encouragement.getEncouragementStatus().equals(EncouragementResultEnum.APPROVED.getCode())) {
                    nextEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
                } else {
                    nextEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));
                    thisEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));
                }
            } else if (!(encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||
                    encouragement.getEncouragementStatus().equals(EncouragementResultEnum.APPROVED.getCode())))
                this.createReviewFromEncouragement(encouragement, nextManagerOrganizationId, ReviewResultEnum.UNDER_REVIEW.getCode(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode(), DraftEnum.Nothing.getCode(), personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex));
        } else {
            if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||
                    encouragement.getEncouragementStatus().equals(EncouragementResultEnum.APPROVED.getCode())) {
                Optional<EncouragementReview> commissionEncouragementReview = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());
                commissionEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));
            }else {
                Optional<EncouragementReview> optional = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());
                if (optional.isEmpty()) {
                    commissionInput(encouragement, personnelManager.getPersonnelManagerOrganizationIdList().size() - 1 >= 0 ? personnelManager.getPersonnelManagerOrganizationIdList().get(personnelManager.getPersonnelManagerOrganizationIdList().size() - 1) : null, personnelReviewCreator);
                } else {
                    EncouragementReview commissionReview = optional.get();
                    encouragementReviewService.updateExistEncouragementReview(commissionReview, encouragement);
                    thisEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));
                }
            }
        }
    }

    @Transactional
    protected void createReviewFromEncouragement(Encouragement encouragement, String registrarOrganizationId, Integer result, Integer type, Integer draft, String previousManagerOrganizationId) throws GeneralException {
        EncouragementReview encouragementReview = new EncouragementReview();
        encouragementReview.setEncouragementReviewEncouragementId(encouragement.getEncouragementId());
        encouragementReview.setEncouragementReviewType(type);
        encouragementReview.setEncouragementReviewEncouragementTypeId(encouragementTypeService.findIdByReasonTypeId(encouragement.getEncouragementReasonTypeId()));
        encouragementReview.setEncouragementReviewAmount(encouragement.getEncouragementAmount());
        encouragementReview.setEncouragementReviewAmountType(encouragement.getEncouragementAmountType());
        encouragementReview.setEncouragementReviewRegistrarOrganizationId(registrarOrganizationId);
        encouragementReview.setEncouragementReviewResult(result);
        if (draft != null) {
            encouragementReview.setEncouragementReviewDraft(DraftEnum.fromCode(draft));
            if (draft.equals(DraftEnum.Sent.getCode()))
                encouragementReview.setEncouragementReviewSentDraftDate(LocalDate.now());
        }
        if (encouragement.getEncouragementRegistrarOrganizationId().equals(registrarOrganizationId))
            encouragementReview.setEncouragementReviewDescription(encouragement.getEncouragementDescription());
        encouragementReview.setEncouragementReviewCreatedAt(LocalDateTime.now());
        encouragementReview.setEncouragementReviewEncouragementCreatedDate(encouragement.getEncouragementCreatedAt() == null ? LocalDateTime.now() : encouragement.getEncouragementCreatedAt());
        if (previousManagerOrganizationId != null)
            encouragementReview.setEncouragementReviewPreviousOrganizationId(previousManagerOrganizationId);
        fillEncouragementFieldsInEncouragementReview(encouragement, encouragementReview);
        encouragementReviewService.add(encouragementReview);
    }

    private void fillEncouragementFieldsInEncouragementReview(Encouragement encouragement, EncouragementReview entity) {
        entity.setEncouragementReviewEncouragementAmount(encouragement.getEncouragementAmount());
        entity.setEncouragementReviewEncouragementAppliedDate(encouragement.getEncouragementAppliedDate());
        entity.setEncouragementReviewEncouragementAmountType(encouragement.getEncouragementAmountType());
        entity.setEncouragementReviewEncouragementDescription(encouragement.getEncouragementDescription());
        entity.setEncouragementReviewEncouragementReasonTypeId(encouragement.getEncouragementReasonTypeId());
        entity.setEncouragementReviewReasonTypeId(encouragement.getEncouragementReasonTypeId());
    }

    private EncouragementReasonType findReasonType(UUID reasonId, UUID typeId) throws GeneralException {
        return encouragementReasonTypeRepository.findByEncouragementReasonIdAndEncouragementTypeId(reasonId, typeId).orElseThrow(() -> new GeneralException("ارتباط نوع و علت یافت نشد . "));
    }

    public void deleteEncouragement(UUID id) {
        encouragementRepository.deleteById(id);
    }

    @Transactional
    public void changeEncouragementStatus(Encouragement encouragement, Integer status, String personnelOrganizationId, Integer approverType) {
        if (encouragement != null) {
            encouragement.setEncouragementStatus(status);
            if (status != null && (status.equals(EncouragementResultEnum.APPROVED.getCode()) || status.equals(EncouragementResultEnum.REJECTED.getCode()) || status.equals(EncouragementResultEnum.CORRECTION_AND_APPROVAL.getCode()))) {
                encouragement.setEncouragementAppliedDate(LocalDate.now());
            }
            if (status != null && (status.equals(EncouragementResultEnum.SENT_FOR_CORRECTION.getCode())))
                encouragement.setIsEncouragementSeen(Boolean.FALSE);
            encouragementRepository.save(encouragement);
        }
    }


    /**
     * @Mappers ------------------------------------------------------------------------------------
     */


    private void mapEntityToDTO(Encouragement encouragement, EncouragementFilterDTOV2 encouragementDTO) {
        String reviewDescription = encouragementReviewService.getLastReviewForEncouragement(encouragement.getEncouragementId());
//        encouragementDTO.setEncouragementReviewDescription(reviewDescription);
        encouragementDTO.setEncouragementReasonTypeId(encouragement.getEncouragementReasonTypeId());
        encouragementDTO.setEncouragementId(encouragement.getEncouragementId());
        encouragementDTO.setEncouragementAmount(encouragement.getEncouragementAmount());
        encouragementDTO.setEncouragementRegistrarOrganizationId(encouragement.getEncouragementRegistrarOrganizationId());
        encouragementDTO.setEncouragementDescription(encouragement.getEncouragementDescription());
        encouragementDTO.setEncouragementNumber(encouragement.getEncouragementNumber());
        encouragementDTO.setEncouragementStatus(encouragement.getEncouragementStatus());
        encouragementDTO.setEncouragementAmountType(encouragement.getEncouragementAmountType());
        encouragementDTO.setCreatedAt(encouragement.getEncouragementCreatedAt());
        encouragementDTO.setEncouragementManagersConcatenatedDescription(encouragement.getEncouragementManagersConcatenatedDescription());
        encouragementDTO.setEncouragementAppliedDate(encouragement.getEncouragementAppliedDate() != null ? encouragement.getEncouragementAppliedDate() : null);
        encouragementDTO.setEncouragementApproverOrganizationId(encouragement.getEncouragementApproverOrganizationId());
        encouragementDTO.setEncouragementApproverType(encouragement.getEncouragementApproverType());
        encouragementDTO.setEncouragementCreatedAt(encouragement.getEncouragementCreatedAt());
        encouragementDTO.setEncouragementSentDraftDate(encouragement.getEncouragementSentDraftDate() != null ? encouragement.getEncouragementSentDraftDate() : null);
        encouragementDTO.setEncouragementEffectiveDate(encouragement.getEncouragementEffectiveDate());
        encouragementDTO.setEncouragementDraft(encouragement.getEncouragementDraft() != null ? encouragement.getEncouragementDraft().getCode() : null);
        encouragementDTO.setIsEncouragementSeen(encouragement.getIsEncouragementSeen());
    }


    @Transactional
    public void updateEncouragementReview(String encouragementReviewUpdateDTO, List<MultipartFile> fileList, List<String> deleteFileIdList) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        EncouragementReviewUpdateDTO dto = objectMapper.readValue(encouragementReviewUpdateDTO, EncouragementReviewUpdateDTO.class);
        PersonnelDTO currentUser = authService.getCurrentUserProfile().getUserInfo();
        EncouragementReview entity = encouragementReviewService.findById(dto.getEncouragementReviewId());
        if (entity == null) throw new GeneralException("بررسی تشویق مربوطه یافت نشد.");
        if ((entity.getEncouragementReviewDraft() != null && entity.getEncouragementReviewDraft().equals(DraftEnum.Sent)) && (entity.getIsEncouragementReviewSeen() != null && entity.getIsEncouragementReviewSeen()))
            throw new GeneralException("این بررسی تشویق ارسال و مشاهده شده و قابل ویرایش نمی باشد.");
        Encouragement encouragement = encouragementRepository.findById(entity.getEncouragementReviewEncouragementId()).orElseThrow(() -> new GeneralException("تشویق مورد نظر یافت نشد. "));
        if (dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode()))
            this.createEncouragementConcatenatedDescription(encouragement, entity.getEncouragementReviewEncouragementAmount(), dto, currentUser.getPersonnelOrganizationID(), entity.getEncouragementReviewType());
        if (entity.getEncouragementReviewId() != null) {
            FileAttachmentDTO fileAttachmentDTO = FileAttachmentDTO.builder().encouragementReviewId(entity.getEncouragementReviewId()).build();
            addFile(fileList, deleteFileIdList, fileAttachmentDTO);
        }
        Optional<EncouragementReview> optional = Optional.empty();
        if (entity.getEncouragementReviewType().equals(ReviewTypeEnum.ORDINARY_COMMISSION.getCode()))
            optional = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());
        else if (entity.getEncouragementReviewType().equals(ReviewTypeEnum.VEDJA_COMMISSION.getCode()))
            optional = encouragementReviewService.getAllVedjaReviewsForThisEncouragement(encouragement.getEncouragementId());
        if (
                ((dto.getEncouragementAmount() != null && !dto.getEncouragementAmount().equals(encouragement.getEncouragementAmount()))
                        || (dto.getEncouragementAmountType() != null && !dto.getEncouragementAmountType().equals(encouragement.getEncouragementAmountType()))
                        || (dto.getEncouragementReasonTypeId() != null && !dto.getEncouragementReasonTypeId().equals(encouragement.getEncouragementReasonTypeId())))
                        && !(dto.getEncouragementReviewResult().equals(ReviewResultEnum.CORRECTION_AND_APPROVAL.getCode()))
        )
            throw new GeneralException(" فقط در وضعیت اصلاح و تایید، می توانید نوع و میزان را تغییر دهید.");
        if (dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
            if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.CORRECTION_AND_APPROVAL.getCode())) {
                if (dto.getEncouragementAmount() != null)
                    encouragement.setEncouragementAmount(dto.getEncouragementAmount());
                if (dto.getEncouragementAmountType() != null) {
                    encouragement.setEncouragementAmountType(dto.getEncouragementAmountType());
                }
                if (dto.getEncouragementReasonTypeId() != null) {
                    EncouragementReasonType reasonType = encouragementReasonTypeService.findById(dto.getEncouragementReasonTypeId());
                    validate(dto.getEncouragementAmount(), reasonType);
                    encouragement.setEncouragementReasonTypeId(dto.getEncouragementReasonTypeId());
                }
                if (dto.getEncouragementReviewAppliedDate() != null)     // تاریخ تصویب وارد شده توسط کاربر همان تاریخ تصویب حقیقی تشویق؟
                    encouragement.setEncouragementAppliedDate(dto.getEncouragementReviewAppliedDate());
//                    this.changeEncouragementStatus(encouragement, dto.getEncouragementReviewResult(), null);
            }
        }
        EncouragementReview encouragementReview = optional.orElse(entity);
        try {
            if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.CORRECTION_AND_APPROVAL.getCode()) && !dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
                if (dto.getEncouragementAmount() != null) {
                    encouragementReview.setEncouragementReviewAmount(dto.getEncouragementAmount());
                }
                if (dto.getEncouragementAmountType() != null) {
                    encouragementReview.setEncouragementReviewAmountType(dto.getEncouragementAmountType());
                }
                if (dto.getEncouragementReasonTypeId() != null) {
                    EncouragementReasonType reasonType = encouragementReasonTypeService.findById(dto.getEncouragementReasonTypeId());
                    validate(dto.getEncouragementAmount(), reasonType);
                    encouragementReview.setEncouragementReviewReasonTypeId(dto.getEncouragementReasonTypeId());
                }
            }
            if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.CORRECTION_AND_APPROVAL.getCode()) && dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
                if (dto.getEncouragementAmount() != null) {
                    encouragementReview.setEncouragementReviewAmount(dto.getEncouragementAmount());
                }
                if (dto.getEncouragementAmountType() != null) {
                    encouragementReview.setEncouragementReviewEncouragementAmountType(encouragement.getEncouragementAmountType());
                }
                if (dto.getEncouragementReasonTypeId() != null) {
                    encouragementReview.setEncouragementReviewReasonTypeId(dto.getEncouragementReasonTypeId());
                }
            }
            if (entity.getEncouragementReviewRegistrarOrganizationId() == null)
                encouragementReview.setEncouragementReviewRegistrarOrganizationId(currentUser.getPersonnelOrganizationID());
            updateEachEncouragementReviewThatNeeded(dto, encouragementReview, encouragement, currentUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addFile(List<MultipartFile> fileList, List<String> deleteFileIdList, FileAttachmentDTO fileAttachmentDTO) throws Exception {
        if (fileAttachmentDTO != null && fileList != null) {
            attachmentService.addAttachmentForEncouragement(fileList, fileAttachmentDTO);
        }
        if (deleteFileIdList != null) {
            deleteFileIdList.forEach(s -> {
                try {
                    attachmentService.deleteAttachmentFile(UUID.fromString(s));
                } catch (GeneralException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void createEncouragementConcatenatedDescription(Encouragement encouragement, Long encouragementReviewEncouragementAmount, EncouragementReviewUpdateDTO dto, String personnelOrganizationID, Integer encouragementReviewType) {
        String result = "";
        if (encouragementReviewType.equals(ReviewTypeEnum.ORDINARY_COMMISSION.getCode()))
            personnelOrganizationID = "کمیسیون";
        else if (encouragementReviewType.equals(ReviewTypeEnum.VEDJA_COMMISSION.getCode()))
            personnelOrganizationID = "ودجا";
        String previous = Optional.ofNullable(encouragement.getEncouragementManagersConcatenatedDescription()).orElse("");
        if (!Objects.equals(dto.getEncouragementAmount(), encouragement.getEncouragementAmount()) && dto.getEncouragementReasonTypeId() != null &&
                dto.getEncouragementReasonTypeId().equals(encouragement.getEncouragementReasonTypeId())) {
            String description = String.format("نظر مدیر %s :", personnelOrganizationID).concat(dto.getEncouragementReviewDescription() != null ? dto.getEncouragementReviewDescription() : "");
            String newLine = String.format("تشویق با میزان: %s ثبت شده بود اما توسط مدیر %s به %s تغییر یافت.", encouragementReviewEncouragementAmount, personnelOrganizationID, dto.getEncouragementAmount()).concat(description).concat("/n");
            result = previous.isEmpty() ? newLine : previous + newLine;
        } else if (dto.getEncouragementReasonTypeId() != null && !dto.getEncouragementReasonTypeId().equals(encouragement.getEncouragementReasonTypeId())) {
            Optional<EncouragementReasonType> reasonType = encouragementReasonTypeRepository.findByEncouragementReasonTypeId(encouragement.getEncouragementReasonTypeId());
//            Optional<EncouragementReasonType> newReasonType = encouragementReasonTypeRepository.findByEncouragementReasonTypeId(dto.getEncouragementReasonTypeId());
            String typeTitle = encouragementReasonTypeRepository.getTypeTitleWithReasonTypeId(encouragement.getEncouragementReasonTypeId());
            String newTypeTitle = encouragementReasonTypeRepository.getTypeTitleWithReasonTypeId(dto.getEncouragementReasonTypeId());
            String description = String.format("نظر مدیر %s :", personnelOrganizationID).concat(dto.getEncouragementReviewDescription() != null ? dto.getEncouragementReviewDescription() : "").concat("/n");
            String newLine = "";
            if (reasonType.isPresent()) {
                EncouragementReasonType encouragementReasonType = reasonType.get();
                if (encouragementReasonType.getMaxAmount() != null)
                    newLine = String.format("تشویق از نوع: %s به مبلغ %s تومان ثبت شده بود اما توسط مدیر %s به %s تغییر یافت.", typeTitle, encouragement.getEncouragementAmount(), personnelOrganizationID, newTypeTitle).concat(description).concat("/n");
                if (encouragementReasonType.getMaxDuration() != null)
                    newLine = String.format("تشویق از نوع: %s به میزان %s روز ثبت شده بود اما توسط مدیر %s به %s تغییر یافت.", typeTitle, encouragement.getEncouragementAmount(), personnelOrganizationID, newTypeTitle).concat(description).concat("/n");
                if (encouragementReasonType.getMaxAmount() == null && encouragementReasonType.getMaxDuration() == null)
                    newLine = String.format("تشویق از نوع: %s ثبت شده بود اما توسط مدیر %s به %s تغییر یافت.", typeTitle, personnelOrganizationID, newTypeTitle).concat(description);
            }
            result = previous.isEmpty() ? newLine : previous + newLine;
        } else if (dto.getEncouragementAmount() == null && dto.getEncouragementReasonTypeId() == null) {
            String newLine = String.format("نظر مدیر %s :", personnelOrganizationID).concat(dto.getEncouragementReviewDescription() != null ? dto.getEncouragementReviewDescription() : "").concat("/n");
            result = previous.isEmpty() ? newLine : previous + newLine;
        } else if (dto.getEncouragementReviewDescription() != null) {
            String newLine = String.format("نظر مدیر %s :", personnelOrganizationID).concat(dto.getEncouragementReviewDescription() != null ? dto.getEncouragementReviewDescription() : "").concat("/n");
            result = previous.isEmpty() ? newLine : previous + newLine;
        }
        encouragement.setEncouragementManagersConcatenatedDescription(result);
    }

    @Transactional
    protected void updateEachEncouragementReviewThatNeeded(EncouragementReviewUpdateDTO dto, EncouragementReview entity, Encouragement encouragement, PersonnelDTO currentUser) throws Exception {
        if (entity.getIsEncouragementReviewSeen())
            throw new GeneralException("این بررسی مشاهده شده است و قابل ویرایش نمی باشد.");
        String currentUserRole = authService.getCurrentUserProfile().getCurrentRole();
        encouragementInitialPreparation(encouragement);
        EncouragementReasonType reasonType = encouragementReasonTypeService.findById(encouragement.getEncouragementReasonTypeId());
        entity.setEncouragementReviewEncouragementAppliedDate(encouragement.getEncouragementAppliedDate());
        if (dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
            entity.setEncouragementReviewSentDraftDate(LocalDate.now());
        }
        updateNonNullFields(dto, entity);
        encouragementReviewService.add(entity);
        if (dto.getEncouragementReviewResult() != null &&
                (dto.getEncouragementReviewResult().equals(ReviewResultEnum.APPROVED.getCode()) || dto.getEncouragementReviewResult().equals(ReviewResultEnum.CORRECTION_AND_APPROVAL.getCode()) ||
                        dto.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_ENCOURAGEMENT_SPECIALIST.getCode()) ||
                dto.getEncouragementReviewResult().equals(ReviewResultEnum.REJECT_TO_COMMISSION.getCode()))) {
            if (dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
                if (reasonType != null && reasonType.getEncouragementTypeId() != null) {
                    if (entity.getEncouragementReviewType().equals(ReviewTypeEnum.ORDINARY_REVIEWER.getCode())) {
                        this.calculator(encouragement, reasonType.getEncouragementTypeId(), currentUser, Boolean.TRUE, dto.getEncouragementReviewDraft(), true, true, true, currentUser.getPersonnelOrganizationID(), currentUserRole);
                    } else {
                        if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_ENCOURAGEMENT_SPECIALIST.getCode())) {
                            sendForVedjaCommission(encouragement, currentUser);
                            encouragementReviewService.updateExistEncouragementReview(entity, encouragement);
                        } else if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.REJECT_TO_COMMISSION.getCode())) {
                            this.calculator(encouragement, reasonType.getEncouragementTypeId(), currentUser, Boolean.TRUE, dto.getEncouragementReviewDraft(), true, true, false, null, currentUserRole);
                        } else {
//                            entity.setIsEncouragementReviewSeen(Boolean.TRUE);
                            if ((dto.getEncouragementReviewResult().equals(ReviewResultEnum.APPROVED.getCode()) || dto.getEncouragementReviewResult().equals(ReviewResultEnum.CORRECTION_AND_APPROVAL.getCode())) &&
                                    entity.getEncouragementReviewType().equals(ReviewTypeEnum.ORDINARY_COMMISSION.getCode()))
                                this.calculator(encouragement, reasonType.getEncouragementTypeId(), currentUser, Boolean.TRUE, dto.getEncouragementReviewDraft(), true, true, true, currentUser.getPersonnelOrganizationID(), currentUserRole);
                            else
                                this.changeEncouragementStatus(encouragement, dto.getEncouragementReviewResult(), currentUser.getPersonnelOrganizationID(), entity.getEncouragementReviewType());
                        }
                    }
                }
            }
        } else if (dto.getEncouragementReviewResult() != null && dto.getEncouragementReviewResult().equals(ReviewResultEnum.REJECTED.getCode())) {
            if (dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
                if (reasonType != null && reasonType.getEncouragementTypeId() != null) {
                    this.changeEncouragementStatus(encouragement, EncouragementResultEnum.REJECTED.getCode(), currentUser.getPersonnelOrganizationID(), entity.getEncouragementReviewType());
                    this.calculator(encouragement, reasonType.getEncouragementTypeId(), currentUser, Boolean.TRUE, dto.getEncouragementReviewDraft(), true, true, false, entity.getEncouragementReviewRegistrarOrganizationId(), currentUserRole);
                }
            }
        } else if (dto.getEncouragementReviewResult() != null) {
            if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_REGISTRAR_CORRECTION.getCode()) && dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
                encouragement.setIsEncouragementSeen(Boolean.FALSE);
                this.calculator(encouragement, reasonType.getEncouragementTypeId(), currentUser, Boolean.TRUE, dto.getEncouragementReviewDraft(), false, false, true, encouragement.getEncouragementRegistrarOrganizationId(), currentUserRole);
            }
            if (dto.getEncouragementReviewResult().equals(ReviewResultEnum.SENT_FOR_RECENT_MANAGER_CORRECTION.getCode()) && dto.getEncouragementReviewDraft().equals(DraftEnum.Sent.getCode())) {
                deleteRegistrarReviewIfExist(encouragement);
                this.calculator(encouragement, reasonType.getEncouragementTypeId(), currentUser, Boolean.TRUE, dto.getEncouragementReviewDraft(), false, false, true, entity.getEncouragementReviewPreviousOrganizationId(), currentUserRole);
            }
        }
    }

    private void encouragementInitialPreparation(Encouragement encouragement) {
        if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||
                encouragement.getEncouragementStatus().equals(EncouragementResultEnum.SENT_FOR_CORRECTION.getCode()))
            encouragement.setEncouragementStatus(EncouragementResultEnum.UNDER_REVIEW.getCode());
        if (!encouragement.getIsEncouragementSeen())
            encouragement.setIsEncouragementSeen(Boolean.TRUE);
    }

    private void sendForVedjaCommission(Encouragement encouragement, PersonnelDTO currentUser) throws GeneralException {
        Optional<EncouragementReview> optional = encouragementReviewService.getAllVedjaReviewsForThisEncouragement(encouragement.getEncouragementId());
        if (optional.isEmpty()) {
            createReviewFromEncouragement(encouragement, null,ReviewResultEnum.UNDER_VEDJA_REVIEW.getCode() , ReviewTypeEnum.VEDJA_COMMISSION.getCode(),  DraftEnum.Nothing.getCode(), null);
            this.changeEncouragementStatus(encouragement, EncouragementResultEnum.UNDER_VEDJA_REVIEW.getCode(), currentUser.getPersonnelOrganizationID(), ReviewTypeEnum.VEDJA_COMMISSION.getCode());
        } else {
            EncouragementReview nextEncouragementReview = optional.get();
            encouragementReviewService.updateExistEncouragementReview(nextEncouragementReview, encouragement);
        }
    }

    private void updateNonNullFields(EncouragementReviewUpdateDTO dto, EncouragementReview entity) {
        if (dto.getEncouragementReviewDescription() != null) {
            entity.setEncouragementReviewDescription(dto.getEncouragementReviewDescription());
        }
        if (dto.getEncouragementReviewDraft() != null)
            entity.setEncouragementReviewDraft(DraftEnum.fromCode(dto.getEncouragementReviewDraft()));
        if (dto.getEncouragementReviewResult() != null)
            entity.setEncouragementReviewResult(dto.getEncouragementReviewResult());
        entity.setEncouragementReviewAppliedDate(dto.getEncouragementReviewAppliedDate() != null ? dto.getEncouragementReviewAppliedDate() : LocalDate.now());
    }


    public PageImpl<EncouragementReviewDTO> getAllEncouragementReviews(EncouragementReviewSearchDTO dto, PageRequest pageRequest) throws Exception {
        List<EncouragementReviewDTO> dtoList = new ArrayList<>();
        PersonnelDTO currentUser = authService.getCurrentUserProfile().getUserInfo();
        String currentUserRole = authService.getCurrentUserProfile().getCurrentRole();
        if (Objects.equals(RoleConstant.ROLE.fromValue(currentUserRole), RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST)) {
            dto.setEncouragementReviewTypeList(List.of(ReviewTypeEnum.ORDINARY_COMMISSION.getCode(), ReviewTypeEnum.VEDJA_COMMISSION.getCode()));
        } else {
            dto.setEncouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId(currentUser.getPersonnelOrganizationID());
            dto.setEncouragementReviewType(ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
        }
        if (dto.getEncouragementReviewEncouragedLastName() != null) {
            List<? extends BasePersonnelDTO> list = personnelService.getFilteredPersonnel(PersonnelFilterDTOV2.builder().personnelLastName(dto.getEncouragementReviewEncouragedLastName()).type("basePersonnel").build());
            if (list != null && list.size() > 0) {
                List<String> orgIdList = list.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                dto.setEncouragementReviewEncouragedOrganizationIdList(orgIdList);
            } else return new PageImpl<>(dtoList, pageRequest, 0);
        }
        if (dto.getEncouragementReviewEncouragedRegistrarLastName() != null) {
            List<? extends BasePersonnelDTO> list = personnelService.getFilteredPersonnel(PersonnelFilterDTOV2.builder().personnelLastName(dto.getEncouragementReviewEncouragedRegistrarLastName()).type("basePersonnel").build());
            if (list != null && list.size() > 0) {
                List<String> orgIdList = list.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                dto.setEncouragementReviewEncouragedRegistrarOrganizationIdList(orgIdList);
            } else return new PageImpl<>(dtoList, pageRequest, 0);
        }
        Page<EncouragementReview> page = encouragementReviewService.getEncouragementReviewsWithSpecification(dto, pageRequest);
        // تبدیل نتایج به DTO ها
        dtoList = page.stream().map(encouragementReview -> {
            UUID encouragementId = encouragementReview.getEncouragementReviewEncouragementId();
            Optional<Encouragement> optional = encouragementRepository.findByEncouragementId(encouragementId);
            EncouragementReviewDTO encouragementReviewDTO = encouragementReviewService.convertToDTO(encouragementReview);
            encouragementReviewDTO.setEncouragementReviewEncouragementId(encouragementId);
            if (Objects.equals(RoleConstant.ROLE.fromValue(currentUserRole), RoleConstant.ROLE.HUMAN_RESOURCE_COMMISSION_ADMIN) || Objects.equals(RoleConstant.ROLE.fromValue(currentUserRole), RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST)) {
                List<Attachment> attachmentGetDTOList = attachmentService.getAllEncouragementAttachments(PageRequest.of(0, 9000), FileAttachmentDTO.builder().encouragementReviewId(encouragementReview.getEncouragementReviewId()).build(), false);
                encouragementReviewDTO.setAttachmentList(attachmentGetDTOList);
            }
            try {
                if (optional.isPresent()) {
                    Encouragement encouragement = optional.get();
                    PersonnelDTO relatedEncouragementPersonnelDTO = (PersonnelDTO) personnelService.findByOrganizationId(encouragement.getEncouragementPersonnelOrganizationId(), typeOfPersonnelDTOSending);
                    encouragementReviewDTO.setEncouragementReviewEncouragementPersonnelDTO(relatedEncouragementPersonnelDTO);
                    PersonnelDTO registrarEncouragementPersonnelDTO = (PersonnelDTO) personnelService.findByOrganizationId(encouragement.getEncouragementRegistrarOrganizationId(), typeOfPersonnelDTOSending);
                    encouragementReviewDTO.setEncouragementReviewRegistrarPersonnelDTO(registrarEncouragementPersonnelDTO);
                    encouragementReviewDTO.setEncouragementNumber(encouragement.getEncouragementNumber());
                    this.updateEncouragementAndReviewHasBeenSeen(encouragement, encouragementReview, pageRequest, currentUser.getPersonnelOrganizationID(), currentUserRole);
                }
                encouragementReviewDTO.setEncouragementReviewCreatedAt(encouragementReview.getEncouragementReviewCreatedAt());
                encouragementReviewDTO.setEncouragementReviewUpdatedAt(encouragementReview.getEncouragementReviewUpdatedAt());
                encouragementReviewDTO.setEncouragementReviewSentDraftDate(encouragementReview.getEncouragementReviewSentDraftDate());
                encouragementReviewDTO.setEncouragementReviewAppliedDate(encouragementReview.getEncouragementReviewAppliedDate());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return encouragementReviewDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageRequest, dtoList.size());
    }

    private void updateEncouragementAndReviewHasBeenSeen(Encouragement encouragement, EncouragementReview encouragementReview, PageRequest pageRequest, String currentUserOrganizationID, String currentUserRole) throws ExecutionException, InterruptedException {
        if ((encouragement.getIsEncouragementSeen() == null || !encouragement.getIsEncouragementSeen()) && !encouragement.getEncouragementStatus().equals(EncouragementResultEnum.SENT_FOR_CORRECTION.getCode())) {
            encouragement.setIsEncouragementSeen(Boolean.TRUE);
//            if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.DRAFT.getCode()))
//                updateExistEncouragement(encouragementReview, encouragement);
            encouragementRepository.save(encouragement);
        }
        encouragementReviewService.updateEncouragementReviewHasBeenSeen(encouragement, encouragementReview, currentUserOrganizationID, pageRequest);
    }

    public List<EncouragementFilterDTOV2> getAllEncouragementForPunishments(EncouragementFilterDTOV2 filterDTO, PageRequest pageRequest) throws ExecutionException, InterruptedException {
        Page<Encouragement> encouragementPage = encouragementRepository.findAll(EncouragementSpecifications.filterBySpecification(filterDTO), pageRequest);
        List<EncouragementFilterDTOV2> encouragementList = new ArrayList<>();
        setEncouragementDtoList(encouragementPage.getContent(), encouragementList, filterDTO.getWithFile());
        return encouragementList;
    }

    public List<EncouragementFlowDetailDTO> getEncouragementFlow(UUID id, PageRequest pageRequest) {
        Page<EncouragementReview> reviewPage = encouragementReviewService.getEncouragementReviewsWithSpecification(EncouragementReviewSearchDTO.builder().encouragementReviewEncouragementId(id).build(), pageRequest);
        List<EncouragementFlowDetailDTO> managerFlow = new ArrayList<>();
        reviewPage.forEach(encouragementReview -> {
            BasePersonnelDTO eachManager = null;
            if (encouragementReview.getEncouragementReviewRegistrarOrganizationId() != null) {
                try {
                    eachManager = personnelService.findByOrganizationId(encouragementReview.getEncouragementReviewRegistrarOrganizationId(), typeOfBasePersonnelDTOSending);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            UUID reasonTypeId = encouragementReview.getEncouragementReviewReasonTypeId();
            Optional<EncouragementReasonType> optional = encouragementReasonTypeRepository.findEncouragementReasonTypeByEncouragementReasonTypeId(reasonTypeId);
            String reasonTitle = null;
            if (optional.isPresent()) {
                EncouragementReasonType encouragementReasonType = optional.get();
                Optional<EncouragementReason> optional1 = encouragementReviewService.getEncouragementReason(encouragementReasonType.getEncouragementReasonId());
                if (optional1.isPresent()) {
                    EncouragementReason encouragementReason = optional1.get();
                    reasonTitle = encouragementReason.getEncouragementReasonTitle();
                }
            }
            String typeTitle = encouragementReasonTypeRepository.getTypeTitleWithReasonTypeId(reasonTypeId);
            managerFlow.add(EncouragementFlowDetailDTO.builder()
                    .managerFirstName(eachManager != null ? eachManager.getPersonnelFirstName() : null)
                    .managerLastName(eachManager != null ? eachManager.getPersonnelLastName() : null)
                    .managerStatus(encouragementReview.getEncouragementReviewResult())
                    .managerReviewType(encouragementReview.getEncouragementReviewType())
                    .managerDescription(encouragementReview.getEncouragementReviewDescription())
                    .managerSentDraftDate(encouragementReview.getEncouragementReviewSentDraftDate())
                    .managerDraft(encouragementReview.getEncouragementReviewDraft().getCode())
                    .encouragementReviewId(encouragementReview.getEncouragementReviewId())
                    .encouragementReviewRegistrarOrganizationId(encouragementReview.getEncouragementReviewRegistrarOrganizationId())
                    .encouragementReviewReasonTitle(reasonTitle)
                    .encouragementReviewTypeTitle(typeTitle)
                    .encouragementReviewEncouragementAmount(encouragementReview.getEncouragementReviewAmount())
                    .build());
        });
        return managerFlow;
    }

    public UnSeenCountDTO getCountOfEncouragementReviewIncoming(PageRequest pageRequest) throws ExecutionException, InterruptedException {
        TblUser profile = authService.getCurrentUserProfile();
        PersonnelDTO currentUser = profile.getUserInfo();
        List<Integer> encouragementReviewResultList = new ArrayList<>();
        List<Integer> encouragementReviewTypeList = new ArrayList<>();
        Page<EncouragementReview> page;
        String personnelOrganizationID = null;
        Integer reviewType = null;
        if (profile.getCurrentRole().equals(RoleConstant.ROLE.PERSONNEL.getValue())) {
            encouragementReviewResultList.add(ReviewResultEnum.UNDER_REVIEW.getCode());
            personnelOrganizationID = currentUser.getPersonnelOrganizationID();
            encouragementReviewTypeList.add(ReviewTypeEnum.ORDINARY_REVIEWER.getCode());
        }
        if (profile.getCurrentRole().equals(RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST.getValue())) {
            encouragementReviewResultList.add(ReviewResultEnum.UNDER_VEDJA_REVIEW.getCode());
            encouragementReviewTypeList.add(ReviewTypeEnum.VEDJA_COMMISSION.getCode());
            encouragementReviewResultList.add(ReviewResultEnum.UNDER_COMMISSION_REVIEW.getCode());
            encouragementReviewTypeList.add(ReviewTypeEnum.ORDINARY_COMMISSION.getCode());
        }
        EncouragementReviewSearchDTO searchDTO = EncouragementReviewSearchDTO.builder().encouragementReviewRegistrarOrgIdNotEncouragementRegistrarOrgId(personnelOrganizationID)
                .encouragementReviewResultList(encouragementReviewResultList).encouragementReviewDraftNotSent(DraftEnum.Sent.getCode()).encouragementReviewTypeList(encouragementReviewTypeList).encouragementReviewType(reviewType).build();
        page = encouragementReviewService.getEncouragementReviewsWithSpecification(searchDTO, pageRequest);
        PageRequest pageRequest2 = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), Sort.by(Encouragement.Fields.encouragementAppliedDate).descending());
        long unSeenPersonnelEncouragement = encouragementRepository.returnThisPersonnelUnSeenEncouragement(currentUser.getPersonnelOrganizationID(), pageRequest2).getTotalElements();
        UnSeenCountDTO unSeenCountDTO = UnSeenCountDTO.builder().unSeenPersonnelEncouragement(unSeenPersonnelEncouragement).build();
        if (page != null)
            unSeenCountDTO.setUnSeenEncouragementReview(page.toList().size());
        return unSeenCountDTO;
    }

    public void updateExistEncouragement(EncouragementReview encouragementReview, Encouragement encouragement) {
        encouragement.setEncouragementReasonTypeId(encouragementReview.getEncouragementReviewEncouragementReasonTypeId());
        encouragement.setEncouragementAmount(encouragementReview.getEncouragementReviewAmount());
        encouragement.setEncouragementStatus(EncouragementResultEnum.UNDER_REVIEW.getCode());
    }
}
