package com.paya.EncouragementService.feign.auth;

import com.paya.EncouragementService.dto.PunishmentDTO;
import com.paya.EncouragementService.dto.PunishmentFilterDTOV2;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.List;

@FeignClient(name = "PUNISHMENT", url = "http://10.16.113.23:8083" )
public interface PunishmentFeign {

    @PostMapping("/punishments/punishmentListInEncouragement")
    List<PunishmentDTO> getAllPunishmentsForEncouragement(@RequestBody(required = false) PunishmentFilterDTOV2 dto, @RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "5") Integer pageSize) throws ParseException;
}
