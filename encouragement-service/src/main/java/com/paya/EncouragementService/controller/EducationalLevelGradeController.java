package com.paya.EncouragementService.controller;

import com.paya.EncouragementService.dto.EducationalLevelGradeDTO;
import com.paya.EncouragementService.dto.v2.EducationalLevelGradeDTOV2;
import com.paya.EncouragementService.service.EducationalLevelGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/educationalLevelGrades")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
public class EducationalLevelGradeController {

    private final EducationalLevelGradeService educationalLevelGradeService;

    @Autowired
    public EducationalLevelGradeController(EducationalLevelGradeService educationalLevelGradeService) {
        this.educationalLevelGradeService = educationalLevelGradeService;
    }


    @GetMapping
    public ResponseEntity<List<EducationalLevelGradeDTO>> getAllEducationalLevelGrades(
            @RequestParam(required = false) Integer degree,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String rankDegree,
            @RequestParam(required = false) String rankRank) {
        List<EducationalLevelGradeDTO> result = educationalLevelGradeService.getAllEducationalLevelGrades(degree, isActive, rankDegree, rankRank);
        return ResponseEntity.ok(result);
    }

    @GetMapping("getRank")
    public ResponseEntity<EducationalLevelGradeDTO> getRankLevelWithDegree(@RequestParam Integer degree) throws Exception {
        return ResponseEntity.ok(educationalLevelGradeService.getRankLevelWithDegree(degree));
    }

    @GetMapping("getRankAndMaxAmount")
    public ResponseEntity<EducationalLevelGradeDTOV2> getRankLevelWithDegree(@RequestParam Integer lastDegree, @RequestParam Integer newDegree) throws Exception {
        return ResponseEntity.ok(educationalLevelGradeService.getRankAndMaxAmount(lastDegree, newDegree));
    }

    @PostMapping
    public ResponseEntity<EducationalLevelGradeDTO> createEducationalLevelGrade(@RequestBody EducationalLevelGradeDTO educationalLevelGradeDTO) throws Exception {
        EducationalLevelGradeDTO createdEducationalLevelGrade = educationalLevelGradeService.createEducationalLevelGrade(educationalLevelGradeDTO);
        return ResponseEntity.status(201).body(createdEducationalLevelGrade);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<EducationalLevelGradeDTO> updateEducationalLevelGrade(
            @PathVariable UUID id,
            @RequestBody EducationalLevelGradeDTO educationalLevelGradeDTO) throws Exception {
        EducationalLevelGradeDTO updatedEducationalLevelGrade = educationalLevelGradeService.updateEducationalLevelGrade(id, educationalLevelGradeDTO);
        return ResponseEntity.ok(updatedEducationalLevelGrade);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducationalLevelGrade(@PathVariable UUID id) {
        educationalLevelGradeService.deleteEducationalLevelGrade(id);
        return ResponseEntity.noContent().build();
    }
}
