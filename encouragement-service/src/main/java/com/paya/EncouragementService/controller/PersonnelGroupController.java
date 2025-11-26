package com.paya.EncouragementService.controller;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.*;
import com.paya.EncouragementService.service.PersonnelGroupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/personnel-group")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
@Slf4j
public class PersonnelGroupController {

    private final PersonnelGroupService personnelGroupService;

    public PersonnelGroupController(PersonnelGroupService personnelGroupService) {
        this.personnelGroupService = personnelGroupService;
    }

    @GetMapping()
    public ResponseEntity<List<PersonnelGroupDTO>> getResults(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String orgId
    ) throws ExecutionException, InterruptedException {
        if (groupName != null)
            groupName = groupName.replace("ی", "ي").trim();
        List<PersonnelGroupDTO> results = personnelGroupService.getResults(groupName, active, lastName, orgId);
        return ResponseEntity.ok(results);
    }


    @PostMapping("/process")
    public ResponseEntity<PersonnelGroupDTO> processPersonnelGroupRequest(@Valid @RequestBody PersonnelGroupDTO requestDTO) throws GeneralException {
        PersonnelGroupDTO personnelGroupDTO = personnelGroupService.processPersonnelGroupRequest(requestDTO);
        return ResponseEntity.ok(personnelGroupDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonnelGroup(@PathVariable UUID id) throws GeneralException {
        personnelGroupService.deletePersonnelGroup(id);
        return ResponseEntity.ok("حذف با موفقیت انجام شد.");
    }
//
//    public ResponseEntity<List<EncouragementReasonTypeDTO>> searchByReasonAndTypeTitles(
//            @RequestParam(required = false) String reasonTitle,
//            @RequestParam(required = false) String typeTitle) {
//
//        // If both reasonTitle and typeTitle are empty, return empty list
//        if ((reasonTitle == null || reasonTitle.isEmpty()) && (typeTitle == null || typeTitle.isEmpty())) {
//            return ResponseEntity.ok(Collections.emptyList());
//        }
//
//        List<EncouragementReasonTypeDTO> results = encouragementReasonTypeService.searchByReasonAndTypeTitles(reasonTitle, typeTitle);
//        return ResponseEntity.ok(results);
//    }

//    @GetMapping("/getGroupedReasonTypes")
//    public ResponseEntity<List<ReasonTypeDTOV2>> getGroupedReasonTypes(@RequestParam(required = false) String reasonTitle,
//                                                                       @RequestParam(required = false) String typeTitle) throws Exception {
//        try {
//            return ResponseEntity.ok().body(encouragementReasonTypeService.getGroupedReasonTypes(reasonTitle, typeTitle));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }


}
