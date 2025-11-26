package com.paya.EncouragementService.service;

import com.paya.EncouragementService.dto.BasePersonnelDTO;
import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.PersonnelResponseDTO;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class PersonnelService {
    @Value("${encouragement.typeOfPersonnelDTOSending}")
    private String typeOfPersonnelDTOSending;

    @Value("${encouragement.timeout}")
    private int timeout;
    private final RabbitMQService rabbitMQService;

    private final RabbitMQListenerAndProducer rabbitMQListenerAndProducer;

    public PersonnelService(RabbitMQService rabbitMQService, RabbitMQListenerAndProducer rabbitMQListenerAndProducer) {
        this.rabbitMQService = rabbitMQService;
        this.rabbitMQListenerAndProducer = rabbitMQListenerAndProducer;
    }


    public List<PersonnelDTO> findByPersonnelIdList(List<UUID> personnelIds) {
        List<String> personnelIdList = personnelIds.stream().map(p -> p.toString()).collect(Collectors.toList());
        System.out.println(personnelIdList);
        PersonnelDTO personnelDto = new PersonnelDTO();
        personnelDto.setPersonnelIdList(personnelIdList);
        personnelDto.setType(typeOfPersonnelDTOSending);
        System.out.println("Data" + personnelDto.getPersonnelIdList());
        PersonnelResponseDTO personnelResponseDTO = rabbitMQService.listOfItems(personnelDto);
        return (List<PersonnelDTO>) personnelResponseDTO.getPersonnelDTOList();
    }

    public PersonnelDTO findById(String personId) throws Exception {
        return rabbitMQService.findById(personId);
    }
    public PersonnelDTO findById(UUID personId) throws Exception {
        return rabbitMQService.findById(String.valueOf(personId));
    }
    public BasePersonnelDTO findByOrganizationId(String organizationId, String type) throws ExecutionException, InterruptedException {
        return rabbitMQService.findById(null, organizationId, type);
    }

    public PersonnelResponseDTO findPageByOrgId(List<String> organizationIdList) throws ExecutionException, InterruptedException {
        return rabbitMQService.findPageByOrganizationIdList(organizationIdList);
    }
    public List<? extends BasePersonnelDTO> getFilteredPersonnel(PersonnelFilterDTOV2 personnel) throws ExecutionException, InterruptedException {
        return rabbitMQService.listOfItems(personnel);
    }

    public List<? extends BasePersonnelDTO> searchWithPersonnelDTO(PersonnelFilterDTOV2 personnel) throws ExecutionException, InterruptedException {
        return rabbitMQService.listOfItems(personnel);
    }

    public PersonnelResponseDTO getPersonnel(PersonnelDTO requestPersonnelDTO) {
        try {
            CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
            return waitAndGetResponse(future);
        } catch (TimeoutException e) {
            CompletableFuture<PersonnelResponseDTO> futureAfterTimeOut = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
            try {
                return waitAndGetResponse(futureAfterTimeOut);
            } catch (TimeoutException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private PersonnelResponseDTO waitAndGetResponse(CompletableFuture<PersonnelResponseDTO> future) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        PersonnelResponseDTO responseDTO;
        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                if (future.isDone()) {
                    responseDTO = future.get();
                    System.out.println("break");
                    return responseDTO;
                }
                Thread.sleep(100);
            }
            throw new TimeoutException("ConversionQueueService-waitAndGetResponse :: timing out for getting personnel");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
