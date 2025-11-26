package com.paya.EncouragementService.controller;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.EncouragementTypeDTO;
import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.service.EncouragementTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/encouragement-types")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")

public class EncouragementTypeController {

    private final EncouragementTypeService service;

    @GetMapping
    public List<EncouragementTypeDTO> getAllEncouragementTypes() {
        return service.getAllEncouragementTypes();
    }

    @PostMapping
    public ResponseEntity<EncouragementType> create(@RequestBody EncouragementTypeDTO dto) throws GeneralException {
        return ResponseEntity.ok(service.create(dto));
    }



    @PatchMapping("/{id}")
    public ResponseEntity<EncouragementType> update(@PathVariable UUID id, @RequestBody EncouragementTypeDTO dto) throws GeneralException {
        return ResponseEntity.ok(service.update(id, dto));
    }

   /* @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }*/


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) throws Exception {
            service.delete(id);
            return ResponseEntity.noContent().build();

    }

    @GetMapping("/filter")
    public ResponseEntity<List<EncouragementTypeDTO>> filterEncouragementTypes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer natureType) {
        if (title != null)
            title = title.replace("ی", "ي").trim();
        List<EncouragementTypeDTO> filteredList = service.filter(title, isActive, natureType);
        return ResponseEntity.ok(filteredList);
    }


}
