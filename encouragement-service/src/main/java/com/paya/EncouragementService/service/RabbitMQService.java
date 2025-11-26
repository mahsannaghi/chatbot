package com.paya.EncouragementService.service;

import com.paya.EncouragementService.config.RabbitMQConfig;
import com.paya.EncouragementService.dto.BasePersonnelDTO;
import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.PersonnelResponseDTO;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService {
    private final RabbitMQListenerAndProducer rabbitMQListenerAndProducer;

    @Value("${encouragement.typeOfBasePersonnelDTOSending")
    private String typeOfBasePersonnelDTOSending;

    @Value("${encouragement.typeOfPersonnelDTOSending}")
    private String typeOfPersonnelDTOSending;
    @Value("${encouragement.typeOfManagerDTOSending}")
    private String typeOfManagerDTOSending;


    @Value("${encouragement.timeout}")
    private int timeout;

    @Autowired
    public RabbitMQService(RabbitMQListenerAndProducer rabbitMQListenerAndProducer) {
        this.rabbitMQListenerAndProducer = rabbitMQListenerAndProducer;
    }

    public PersonnelResponseDTO listOfItems(PersonnelDTO requestPersonnelDTO) {
        try {
            requestPersonnelDTO.setType(typeOfPersonnelDTOSending);
//            CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDto, "ENCOURAGEMENT_REQUEST_KEY");
            CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
            return waitAndGetResponse(future);
        } catch (TimeoutException e) {
            CompletableFuture<PersonnelResponseDTO> futureAfterTimeOut = rabbitMQListenerAndProducer.send(requestPersonnelDTO, RabbitMQConfig.INIT_REQUEST_KEY);
            try {
                return waitAndGetResponse(futureAfterTimeOut);
            } catch (TimeoutException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public List<? extends BasePersonnelDTO> listOfItems(PersonnelFilterDTOV2 filterDTO) throws ExecutionException, InterruptedException {
        PersonnelDTO requestPersonnelDTO = new PersonnelDTO();
        requestPersonnelDTO.setPersonnelFirstName(filterDTO.getPersonnelFirstName());
        requestPersonnelDTO.setPersonnelLastName(filterDTO.getPersonnelLastName());
        requestPersonnelDTO.setPersonnelUnitCode(filterDTO.getPersonnelUnitCode());
        requestPersonnelDTO.setPersonnelRankType(filterDTO.getPersonnelRankType());
        requestPersonnelDTO.setPersonnelRankCodeList(filterDTO.getPersonnelRankCodeList());
        requestPersonnelDTO.setPersonnelOrganizationID(filterDTO.getPersonnelOrganizationId());
        if (filterDTO.getType() == null)
            requestPersonnelDTO.setType(typeOfPersonnelDTOSending);
        else
            requestPersonnelDTO.setType(filterDTO.getType());
        CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
        long startTime = System.currentTimeMillis();
        PersonnelResponseDTO responseDTO = future.get();
        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                if (future.isDone()) {
                    responseDTO = future.get();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Error occurred during execution: " + e.getMessage());
        }
        return responseDTO.getPersonnelDTOList();
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
            throw new TimeoutException("RabbitMQService-listOfItems :: timing out for getting personnel");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public BasePersonnelDTO findById(String personId, String organizationId, String type) throws ExecutionException, InterruptedException {
        PersonnelDTO requestPersonnelDTO = new PersonnelDTO();
        if (type == null)
            requestPersonnelDTO.setType(typeOfManagerDTOSending);
        else
            requestPersonnelDTO.setType(type);
        if (personId != null) {
            requestPersonnelDTO.setPersonnelId(personId);
        }
        if (organizationId != null) {
            requestPersonnelDTO.setPersonnelOrganizationID(organizationId);
        }

        // Send request via RabbitMQ
        CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
        long startTime = System.currentTimeMillis();
        PersonnelResponseDTO responseDTO = future.get();

        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                if (future.isDone()) {
                    responseDTO = future.get();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Error occurred during execution: " + e.getMessage());
        }
        return responseDTO.getPersonnelDTOList().get(0);
    }

    public PersonnelResponseDTO findPageByOrganizationIdList(List<String> organizationIdList) throws ExecutionException, InterruptedException {
        PersonnelDTO requestPersonnelDTO = new PersonnelDTO();
        requestPersonnelDTO.setType(typeOfBasePersonnelDTOSending);
        if (organizationIdList != null) {
            requestPersonnelDTO.setPersonnelOrganizationIdList(organizationIdList);
        }
        CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
        long startTime = System.currentTimeMillis();
        PersonnelResponseDTO responseDTO = future.get();

        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                if (future.isDone()) {
                    responseDTO = future.get();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Error occurred during execution: " + e.getMessage());
        }
        return responseDTO;
    }



  public PersonnelDTO findById(String personId) throws Exception {
        PersonnelDTO requestPersonnelDTO = new PersonnelDTO();
        requestPersonnelDTO.setType(typeOfPersonnelDTOSending);
            requestPersonnelDTO.setPersonnelId(personId);
        // Send request via RabbitMQ
        CompletableFuture<PersonnelResponseDTO> future = rabbitMQListenerAndProducer.send(requestPersonnelDTO, null);
        long startTime = System.currentTimeMillis();
        PersonnelResponseDTO responseDTO = future.get();

        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                if (future.isDone()) {
                    responseDTO = future.get();
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (ExecutionException e) {
            System.err.println("Error occurred during execution: " + e.getMessage());
        }
        return (PersonnelDTO) responseDTO.getPersonnelDTOList().get(0);
    }


}
