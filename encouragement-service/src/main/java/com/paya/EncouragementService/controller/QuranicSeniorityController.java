package com.paya.EncouragementService.controller;

import com.paya.EncouragementService.dto.QuranicSeniorityDTO;
import com.paya.EncouragementService.entity.QuranicSeniority;
import com.paya.EncouragementService.service.QuranicSeniorityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quranic-seniority")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
public class QuranicSeniorityController {

    @Autowired
    private QuranicSeniorityService service;


    @GetMapping("/max-seniority")
    public ResponseEntity<Integer> getMaxSeniority() {
        int maxSeniority = service.getMaxSeniority();
        return ResponseEntity.ok(maxSeniority);
    }


    @PatchMapping("/max-seniority")
    public ResponseEntity<Void> updateMaxSeniority(@RequestParam int newMaxSeniority) {
        service.updateMaxSeniority(newMaxSeniority);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/create")
    public ResponseEntity<QuranicSeniority> createQuranicSeniority(@RequestBody QuranicSeniorityDTO dto) throws Exception {
        return ResponseEntity.ok(service.createQuranicSeniority(dto));
    }


    @PatchMapping("/update")
    public ResponseEntity<QuranicSeniority> updateQuranicSeniority(@RequestBody @Valid QuranicSeniorityDTO dto) throws Exception {
        QuranicSeniority entity = service.updateQuranicSeniority(dto);
        return ResponseEntity.ok(entity);
    }


    @GetMapping("/{id}")
    public ResponseEntity<QuranicSeniority> getQuranicSeniorityById(@PathVariable String id) {
        try {
            UUID quranicSeniorityId = UUID.fromString(id);
            return service.getQuranicSeniorityById(quranicSeniorityId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuranicSeniority(@PathVariable String id) throws Exception {
        UUID quranicSeniorityId = UUID.fromString(id);
        service.deleteQuranicSeniority(quranicSeniorityId);
        return ResponseEntity.ok("حذف با موفقیت انجام شد.");
    }


    @GetMapping
    public ResponseEntity<Page<QuranicSeniority>> getQuranicSeniorities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String amount,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer fromAmount,
            @RequestParam(required = false) Integer toAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (type != null)
            type = type.replace("ی", "ي").trim();
        Page<QuranicSeniority> result = service.getQuranicSeniorities(type, amount, isActive, fromAmount, toAmount, page, size);
        return ResponseEntity.ok(result);
    }


}
