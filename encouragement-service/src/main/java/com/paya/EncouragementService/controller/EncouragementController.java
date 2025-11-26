package com.paya.EncouragementService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paya.EncouragementService.dto.*;
import com.paya.EncouragementService.dto.v2.EncouragementFilterDTOV2;
import com.paya.EncouragementService.dto.v2.PersonnelListEncouragementFilterDTO;
import com.paya.EncouragementService.entity.Encouragement;
import com.paya.EncouragementService.entity.EncouragementReview;
import com.paya.EncouragementService.enumeration.EncouragementResultEnum;
import com.paya.EncouragementService.enumeration.RoleConstant;
import com.paya.EncouragementService.enumeration.TypeCategoryEnum;
import com.paya.EncouragementService.service.AuthService;
import com.paya.EncouragementService.service.EncouragementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/encouragements")
@AllArgsConstructor
public class EncouragementController {
    private final EncouragementService encouragementService;
    private final AuthService authService;

    @PostMapping("getCartablEncouragementList")
    public ResponseEntity<EncouragementAndPunishmentDTO> getCartablEncouragementList(@RequestBody(required = false) EncouragementFilterDTOV2 filterDTO, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws Exception {
        try {
            String currentUserRole = authService.getCurrentUserProfile().getCurrentRole();
            if (currentUserRole.equals(RoleConstant.ROLE.PERSONNEL.getValue())) {
                filterDTO.setEncouragementTypeCategory(TypeCategoryEnum.NORMAL.getCode());
                if (filterDTO.getEncouragementRegistrarOrganizationId() == null) {
                    PersonnelDTO personnelDTO = authService.getCurrentUserProfile().getUserInfo();
                    filterDTO.setEncouragementRegistrarOrganizationId(personnelDTO.getPersonnelOrganizationID());
                }
//                filterDTO.setPunishmentRegistrarRoleName(JAVAXCryptoUtils.encrypt(RoleServiceConstant.Personnel.getKeyName()));
            }
            if (currentUserRole.equals(RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST.getValue())) {
                filterDTO.setEncouragementTypeCategory(TypeCategoryEnum.SPECIALIST.getCode());
//                filterDTO.setPunishmentRegistrarRoleName(JAVAXCryptoUtils.encrypt(RoleServiceConstant.PunishmentSpecialist.getKeyName()));
                filterDTO.setWithFile(Boolean.TRUE);
            }
            return ResponseEntity.ok().body(encouragementService.getEncouragementList(filterDTO, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "encouragementNumber"))));
        }catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }

    @PostMapping("getUserPanelEncouragementList")
    public ResponseEntity<EncouragementAndPunishmentDTO> getUserPanelEncouragementList(@RequestBody(required = false) EncouragementFilterDTOV2 filterDTO, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws Exception {
        try {
            if (filterDTO.getEncouragementPersonnelOrganizationId() == null) {
                PersonnelDTO personnelDTO = authService.getCurrentUserProfile().getUserInfo();
                filterDTO.setEncouragementPersonnelOrganizationId(personnelDTO.getPersonnelOrganizationID());
                filterDTO.setEncouragementStatusList(Arrays.asList(EncouragementResultEnum.APPROVED.getCode(), EncouragementResultEnum.CORRECTION_AND_APPROVAL.getCode()));
            }
            return ResponseEntity.ok(encouragementService.getEncouragementList(filterDTO, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "encouragementNumber"))));
        }catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }
    @PostMapping("getPersonnelListEncouragementBothRegistrarOrEncouraged")
    public ResponseEntity<EncouragementAndPunishmentDTO> getPersonnelListEncouragementBothRegistrarOrEncouraged(@RequestBody(required = false) EncouragementFilterDTOV2 filterDTO, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws Exception {
        try {
            filterDTO.setWithPunishment(Boolean.TRUE);
            filterDTO.setEncouragementStatusNot(EncouragementResultEnum.DRAFT.getCode());
            return ResponseEntity.ok().body(encouragementService.getEncouragementList(filterDTO, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "encouragementNumber"))));
        }catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }


    @PostMapping("getPersonnelListEncouragement")
    public ResponseEntity<PaginationResponseDTO<? extends BasePersonnelDTO>> getPersonnelListEncouragement(@RequestBody(required = false) PersonnelListEncouragementFilterDTO dto, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws Exception {
        try {
            return ResponseEntity.ok().body(encouragementService.getPersonnelListEncouragement(dto, PageRequest.of(pageNumber, pageSize)));
        } catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }


    @PostMapping("getEncouragementListOfThisServiceUnit")
    public ResponseEntity<EncouragementAndPunishmentDTO> getEncouragementListOfThisServiceUnit(@RequestBody(required = false) EncouragementFilterDTOV2 filterDTO, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws Exception {
        try {
            filterDTO.setAllServiceUnitPersonnelEncouragement(Boolean.TRUE);
            filterDTO.setEncouragementStatusNot(EncouragementResultEnum.DRAFT.getCode());
            if (filterDTO.getEncouragementReason() != null)
                filterDTO.setEncouragementReason(filterDTO.getEncouragementReason().replace("ی", "ي").trim());
            if (filterDTO.getEncouragementType() != null)
                filterDTO.setEncouragementType(filterDTO.getEncouragementType().replace("ی", "ي").trim());
            return ResponseEntity.ok().body(encouragementService.getEncouragementList(filterDTO, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "encouragementNumber"))));
        }catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("getPunishmentListOfThisServiceUnit")
    public ResponseEntity<EncouragementAndPunishmentDTO> getPunishmentListOfThisServiceUnit(@RequestBody(required = false) PunishmentFilterDTOV2 filterDTO, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws Exception {
        try {
            filterDTO.setPunishmentStatusNot(EncouragementResultEnum.DRAFT.getCode());
            return ResponseEntity.ok().body(encouragementService.getPunishmentListOfThisServiceUnit(filterDTO, PageRequest.of(pageNumber, pageSize)));
        }catch (Exception e) {
            throw e;
        }
    }


    @PostMapping("/EncouragementListInPunishment")
    public List<EncouragementFilterDTOV2> getAllEncouragementForPunishment(@RequestBody(required = false) EncouragementFilterDTOV2 filterDTO, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "50") Integer pageSize) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "encouragementNumber"));
            return encouragementService.getAllEncouragementForPunishments(filterDTO, pageRequest);
        }catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("getEncouragementFlow/{encouragementId}")
    public ResponseEntity<List<EncouragementFlowDetailDTO>> getEncouragementFlow(@PathVariable UUID encouragementId, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "50") Integer pageSize) throws Exception {
        try {
            return ResponseEntity.ok().body(encouragementService.getEncouragementFlow(encouragementId, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, EncouragementReview.Fields.encouragementReviewCreatedAt))));
        }catch (Exception e) {
            throw e;
        }
    }
//    @PostMapping
//    public ResponseEntity<Map<String, Object>> createEncouragement(@RequestBody EncouragementCreateRequestSpecial request) throws Exception {
//        List<Encouragement> createdEncouragements = encouragementService.createEncouragement(request);
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", "Created successfully");
//        List<UUID> punishmentIds = createdEncouragements.stream()
//                .map(Encouragement::getEncouragementId)
//                .collect(Collectors.toList());
//        response.put("encouragementIds", punishmentIds);
//        if (createdEncouragements.isEmpty()) {
//            response.put("status", "No punishments created");
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PERSONNEL')")
    public ResponseEntity<List<Encouragement>> addEncouragement(@RequestBody EncouragementCreateRequestSpecial encouragementCreateDTO) throws Exception {
        try {
            PersonnelDTO currentUser = authService.getCurrentUserProfile().getUserInfo();
            List<Encouragement> encouragementList = encouragementService.addOrUpdateEncouragement(encouragementCreateDTO, currentUser);
            return ResponseEntity.ok().body(encouragementList);
        }catch (Exception e) {
            throw new Exception("خطایی رخ داده است.");
        }
    }

    @PostMapping("addOrUpdateEncouragementByEncouragementSpecialist")
    @PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
    public ResponseEntity<String> addOrUpdateEncouragementByEncouragementSpecialist(@RequestPart("encouragement") String request,
                                                                                           @RequestPart(required = false, name = "files") List<MultipartFile> fileList,
                                                                                           @RequestPart(required = false, name = "deleteFileIdList") String deleteFileIdList) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            List<String> finalDeleteFileIdList = null;
            if (deleteFileIdList != null)
                finalDeleteFileIdList = new ObjectMapper().readValue(deleteFileIdList, new TypeReference<>() {
                });
            EncouragementCreateRequestSpecial dto = objectMapper.readValue(request, EncouragementCreateRequestSpecial.class);
            PersonnelDTO currentUser = authService.getCurrentUserProfile().getUserInfo();
            encouragementService.addOrUpdateEncouragementByEncouragementSpecialist(dto, currentUser, fileList, finalDeleteFileIdList);
            return ResponseEntity.ok("تشویق با موفقیت ثبت شد.");
        } catch (JsonProcessingException e) {
            throw new Exception("داده ی ورودی صحیح نمی باشد.");
        }
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<Encouragement> updateEncouragement(
//            @PathVariable UUID id,
//            @RequestBody EncouragementCreateRequestSpecial request) {
//        Encouragement updatedEncouragement = encouragementService.updateEncouragement(id, request);
//        return ResponseEntity.ok(updatedEncouragement);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePunishment(@PathVariable UUID id) {
        encouragementService.deleteEncouragement(id);

        Map<String, String> response = new HashMap<>();
        response.put("status", "Deleted successfully");

        return ResponseEntity.ok(response);
    }


}
