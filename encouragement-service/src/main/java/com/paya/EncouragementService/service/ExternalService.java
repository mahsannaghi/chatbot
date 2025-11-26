package com.paya.EncouragementService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class ExternalService {

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String getGradeTitle(UUID gradeId) {
        String url = "http://api.example.com/grades/" + gradeId;
        return restTemplate.getForObject(url, String.class);
    }


    public String getPositionTitle(UUID positionId) {
        String url = "http://api.example.com/positions/" + positionId;
        return restTemplate.getForObject(url, String.class);
    }
}
