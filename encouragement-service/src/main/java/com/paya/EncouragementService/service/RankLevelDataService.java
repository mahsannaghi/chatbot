package com.paya.EncouragementService.service;

import com.paya.EncouragementService.dto.JobLevelWithJobGroupDataDto;
import com.paya.EncouragementService.dto.RankLevelDataDTO;
import com.paya.EncouragementService.repository.RankLevelDataFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RankLevelDataService {

    @Autowired
    private RankLevelDataFeignClient rankLevelDataFeignClient;


    public List<RankLevelDataDTO> getRankLevelData() {
        ResponseEntity<List<RankLevelDataDTO>> response = rankLevelDataFeignClient.getAllRankWithCivilian();
        List<RankLevelDataDTO> allRankLevelData = response.getBody();
        List<RankLevelDataDTO> filteredRankLevelData = allRankLevelData.stream()
//                .filter(data -> data.getRankTypeCivilian().equals(gradeId.toString()))
                .collect(Collectors.toList());
        return filteredRankLevelData;
    }


    public List<RankLevelDataDTO> getAllRankWithCivilian(UUID rankLevelDataId,
                                                         String rankLevelDataDegree,
                                                         String rankLevelDataJobLevel,
                                                         String rankLevelDataRank) {
        ResponseEntity<List<RankLevelDataDTO>> response = rankLevelDataFeignClient.getAllRankWithCivilian();

        return response.getBody();
    }

    public List<JobLevelWithJobGroupDataDto> getAllJobLevelWithJobGroup() {
        ResponseEntity<List<JobLevelWithJobGroupDataDto>> response = rankLevelDataFeignClient.getAllJobLevelWithJobGroup();

        return response.getBody();
    }


}