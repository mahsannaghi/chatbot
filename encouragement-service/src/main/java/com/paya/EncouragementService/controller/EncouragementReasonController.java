package com.paya.EncouragementService.controller;


import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.CustomMessage;
import com.paya.EncouragementService.dto.EncouragementReasonDTO;
import com.paya.EncouragementService.entity.EncouragementReason;
import com.paya.EncouragementService.service.EncouragementReasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/encouragement-reasons")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EXECUTIVE_MANAGER', 'ENCOURAGEMENT_SPECIALIST')")
public class EncouragementReasonController {

    private final EncouragementReasonService reasonService;

    @GetMapping("/filter")
    public Page<EncouragementReason> filter(
            @RequestParam(required = false) String encouragementReasonTitle,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        if (encouragementReasonTitle != null)
            encouragementReasonTitle = encouragementReasonTitle.replace("ی", "ي").trim();
        return reasonService.filter(encouragementReasonTitle, active, pageable);
    }


    @PostMapping
    public ResponseEntity<EncouragementReason> create(@RequestBody EncouragementReasonDTO dto) {
        return ResponseEntity.ok(reasonService.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EncouragementReason> update(@PathVariable UUID id, @RequestBody EncouragementReasonDTO dto) throws GeneralException {
        return ResponseEntity.ok(reasonService.update(id, dto));
    }

    /*@DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reasonService.delete(id);
        return ResponseEntity.noContent().build();
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomMessage> delete(@PathVariable UUID id) throws GeneralException {
            return ResponseEntity.ok().body(reasonService.delete(id));
    }
}