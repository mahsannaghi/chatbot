package com.paya.EncouragementService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paya.EncouragementService.config.RabbitMQConfig;
import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.PersonnelResponseDTO;
import com.paya.EncouragementService.entity.TblQueue;
import com.paya.EncouragementService.repository.QueueRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitMQListenerAndProducer {
    private final AmqpTemplate amqpTemplate;

    private final ObjectMapper objectMapper;

    private final RabbitAdmin rabbitAdmin;

    private final QueueRepository queueRepository;
    private final Map<String, CompletableFuture<PersonnelResponseDTO>> futureMap = new ConcurrentHashMap<>();
    private final RabbitMQConfig rabbitMQConfig;


    @Value("${encouragement.consumerName}")
    private String consumerName;
    @Value("${encouragement.requestQueueSuffix}")
    private String requestQueueSuffix;
    @Value("${encouragement.responseQueueSuffix}")
    private String responseQueueSuffix;

    public RabbitMQListenerAndProducer(AmqpTemplate amqpTemplate, ObjectMapper objectMapper, RabbitAdmin rabbitAdmin, RabbitMQConfig rabbitMQConfig, QueueRepository queueRepository) {
        this.amqpTemplate = amqpTemplate;
        this.objectMapper = objectMapper;
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitMQConfig = rabbitMQConfig;
        this.queueRepository = queueRepository;
//        rabbitMQConfig.ensureAllQueuesExist(rabbitAdmin);
    }

    public CompletableFuture<PersonnelResponseDTO> send(PersonnelDTO personnelDto, String initRequestKey) {
        String jsonBody;
        Message request = null;
        String requestQueueName;
        try {
            CompletableFuture<PersonnelResponseDTO> futureResponse = new CompletableFuture<>();
            String correlationId = generateCorrelationId();
            futureMap.put(correlationId, futureResponse);
            personnelDto.setCorrelationId(correlationId);
            if (initRequestKey == null)
                requestQueueName = getActiveRequestQueueName(personnelDto);
            else
                requestQueueName = initRequestKey;
            jsonBody = objectMapper.writeValueAsString(personnelDto);
            System.out.println("sending request: " + jsonBody);
            request = MessageBuilder.withBody(jsonBody.getBytes(StandardCharsets.UTF_8)).setContentType("application/json").build();
            amqpTemplate.convertAndSend(requestQueueName, request.getBody());
            System.out.println("Sent message: " + request);
            return futureResponse;
        } catch (JsonProcessingException e) {
            System.out.println("Sent message: " + request);
            return null;
        }
    }

    public String getActiveRequestQueueName(PersonnelDTO personnelDto) {
        if (queueRepository.findAll().size() != 0) {
            TblQueue queue = queueRepository.findAll().get(0);
            String requestQueueName = queue.getRequestQueueName();
            Properties queueProperties = rabbitAdmin.getQueueProperties(requestQueueName);
            String compareConsumerName = consumerName.concat(requestQueueSuffix).toUpperCase();
            if (!(queueProperties != null && queueProperties.get("QUEUE_CONSUMER_COUNT") != null) || !requestQueueName.equals(compareConsumerName)) {
                personnelDto.setConsumerName(consumerName);
                queueRepository.deleteAll();
                return RabbitMQConfig.INIT_REQUEST_KEY;
            } else {
                return requestQueueName;
            }
        } else {
            personnelDto.setConsumerName(consumerName);
            return RabbitMQConfig.INIT_REQUEST_KEY;
        }
    }

    public void receiveMessage(byte[] request, String queueName) {
        PersonnelResponseDTO responseDTO = null;
        String responseBody;
        try {
            responseBody = new String(request, StandardCharsets.UTF_8);
            System.out.println("Received response: " + responseBody);
            responseDTO = objectMapper.readValue(responseBody, PersonnelResponseDTO.class);
            String correlationId = responseDTO.getCorrelationId();
            CompletableFuture<PersonnelResponseDTO> future = futureMap.remove(correlationId);
            if (future != null) {
                future.complete(responseDTO);
            } else {
//                rabbitMQConfig.removeQueue(queueName, rabbitAdmin);
                rabbitMQConfig.addQueue(consumerName.concat(requestQueueSuffix), consumerName.concat(responseQueueSuffix), rabbitAdmin);
                System.out.printf("No future found for correlationId: %s, queueName: %s%n", correlationId, queueName);
            }
            if (responseDTO.getResponseQueueName() != null) {
                rabbitMQConfig.addQueue(responseDTO.getRequestQueueName(), responseDTO.getResponseQueueName(), rabbitAdmin);
            }
        } catch (Exception e) {
            System.out.println("Received message: " + responseDTO);
        }
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}

