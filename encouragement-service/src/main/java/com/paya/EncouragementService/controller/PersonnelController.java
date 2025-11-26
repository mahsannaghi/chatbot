package com.paya.EncouragementService.controller;

import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.PersonnelResponseDTO;
import com.paya.EncouragementService.service.RabbitMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import paya.net.exceptionhandler.Exception.GeneralException;


@RestController
@RequestMapping("/api/personnel")
@Slf4j
public class PersonnelController {


    private final RabbitMQService rabbitMQService;
    @Value("${encouragement.typeOfPersonnelDTOSending}")
    private String typeOfPersonnelDTOSending;

    @Autowired
    public PersonnelController(RabbitMQService rabbitMQService) {

        this.rabbitMQService = rabbitMQService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getPersonnelList(@RequestParam String organizationId) throws Exception {
        try {

            PersonnelDTO personnelDto = new PersonnelDTO();
            personnelDto.setPersonnelOrganizationID(organizationId);
            personnelDto.setType(typeOfPersonnelDTOSending);
            PersonnelResponseDTO personnelResponseDTO = rabbitMQService.listOfItems(personnelDto);
            if (personnelResponseDTO == null ||
                    personnelResponseDTO.getPersonnelDTOList() == null ||
                    personnelResponseDTO.getPersonnelDTOList().isEmpty()) {
                throw new Exception("شخص مورد نظر یافت نشد . ");
            }


            return ResponseEntity.ok(personnelResponseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


}
