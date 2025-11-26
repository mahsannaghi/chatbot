package com.paya.EncouragementService.controller;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.UpgradeDegreeSeniorityDTO;
import com.paya.EncouragementService.entity.UpgradeDegreeSeniority;
import com.paya.EncouragementService.service.UpgradeDegreeSeniorityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/upgrade-degree-seniority")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
public class UpgradeDegreeSeniorityController {

    private final UpgradeDegreeSeniorityService service;

    public UpgradeDegreeSeniorityController(UpgradeDegreeSeniorityService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<UpgradeDegreeSeniority> createUpgradeDegreeSeniority(
            @RequestBody @Valid UpgradeDegreeSeniorityDTO dto) throws GeneralException {
        UpgradeDegreeSeniority entity = service.createUpgradeDegreeSeniority(dto);
        return ResponseEntity.status(201).body(entity);
    }

    @PatchMapping("/update")
    public ResponseEntity<UpgradeDegreeSeniority> updateUpgradeDegreeSeniority(
            @RequestBody @Valid UpgradeDegreeSeniorityDTO dto) throws GeneralException {
        UpgradeDegreeSeniority entity = service.updateUpgradeDegreeSeniority(dto);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UpgradeDegreeSeniority> getUpgradeDegreeSeniorityById(@PathVariable UUID id) {
        return service.getUpgradeDegreeSeniorityById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUpgradeDegreeSeniority(@PathVariable UUID id) {
        service.deleteUpgradeDegreeSeniority(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UpgradeDegreeSeniority>> getUpgradeDegreeSeniorities(
            @RequestParam(required = false) Integer fromDegree,
            @RequestParam(required = false) Integer toDegree,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer maxAmount,
            @RequestParam(required = false) Integer fromAmount,
            @RequestParam(required = false) Integer toAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UpgradeDegreeSeniority> result = service.getUpgradeDegreeSeniorities(fromDegree, toDegree, isActive, fromAmount, toAmount, maxAmount, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("getMaxAmount")
    public ResponseEntity<Integer> getMaxAmount(@RequestParam Integer lastDegree, @RequestParam Integer newDegree) throws Exception {
        return ResponseEntity.ok().body(service.getMaxAmountWithLastDegreeAndNewDegree(lastDegree, newDegree));
    }


}
