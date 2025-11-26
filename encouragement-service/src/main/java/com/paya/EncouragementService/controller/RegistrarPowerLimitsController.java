package com.paya.EncouragementService.controller;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.RegistrarPowerLimitsDTO;
import com.paya.EncouragementService.dto.RegistrarPowerLimitsRequestDTO;
import com.paya.EncouragementService.service.RegistrarPowerLimitsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registrar-power-limits")
@PreAuthorize("hasAnyRole('ENCOURAGEMENT_SPECIALIST')")
public class RegistrarPowerLimitsController {

    private final RegistrarPowerLimitsService registrarPowerLimitsService;

    public RegistrarPowerLimitsController(RegistrarPowerLimitsService registrarPowerLimitsService) {
        this.registrarPowerLimitsService = registrarPowerLimitsService;
    }



    @PostMapping("search")
    public List<RegistrarPowerLimitsDTO> getRegistrarPowerLimitsWithExternalData(@RequestBody RegistrarPowerLimitsRequestDTO requestDTO) {
        if (requestDTO.getTypeTitleList() != null)
            requestDTO.setTypeTitleList(requestDTO.getTypeTitleList().stream().map(s -> s.replace("ی", "ي").trim()).toList());
        return registrarPowerLimitsService.getRegistrarPowerLimitsWithExternalData(requestDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<String> saveRegistrarPowerLimits(@RequestBody RegistrarPowerLimitsRequestDTO requestDTO) throws GeneralException {
        registrarPowerLimitsService.saveRegistrarPowerLimits(requestDTO);
        return ResponseEntity.ok("Request processed successfully");
    }
}
