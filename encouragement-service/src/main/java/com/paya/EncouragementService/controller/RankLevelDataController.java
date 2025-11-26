package com.paya.EncouragementService.controller;

import com.paya.EncouragementService.dto.JobLevelWithJobGroupDataDto;
import com.paya.EncouragementService.dto.RankLevelDataDTO;
import com.paya.EncouragementService.repository.RankLevelDataFeignClient;
import com.paya.EncouragementService.service.RankLevelDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rankLevelData")
public class RankLevelDataController {


    @Autowired
    private RankLevelDataFeignClient rankLevelDataFeignClient;


    @Autowired
    private RankLevelDataService rankLevelDataService;


    @GetMapping("/api/v1/rankLevelData")
    public ResponseEntity<List<RankLevelDataDTO>> getRankLevelData(
            @RequestParam(value = "gradeId", required = false) String gradeId,
            @RequestParam(value = "positionId", required = false) String positionId) {


        ResponseEntity<List<RankLevelDataDTO>> response = rankLevelDataFeignClient.getAllRankWithCivilian();
        List<RankLevelDataDTO> rankLevelDataList = response.getBody();


        if (rankLevelDataList != null && gradeId != null) {
            UUID gradeIdUUID = UUID.fromString(gradeId);
            rankLevelDataList = rankLevelDataList.stream()
//                    .filter(data -> data.getRankLevelDataId().equals(gradeIdUUID))  // فیلتر بر اساس UUID
                    .collect(Collectors.toList());
        }


        if (rankLevelDataList != null && positionId != null) {
            UUID positionIdUUID = UUID.fromString(positionId);
            rankLevelDataList = rankLevelDataList.stream()
//                    .filter(data -> data.getRankLevelDataId().equals(positionIdUUID))  // فیلتر بر اساس UUID
                    .collect(Collectors.toList());
        }


        return ResponseEntity.ok(rankLevelDataList);
    }


    @GetMapping("/getAllRankWithCivilian")
    public ResponseEntity<List<RankLevelDataDTO>> getAllRankWithCivilian(
            @RequestParam(value = "rankLevelDataId", required = false) UUID rankLevelDataId,
            @RequestParam(value = "rankLevelDataDegree", required = false) String rankLevelDataDegree,
            @RequestParam(value = "rankLevelDataJobLevel", required = false) String rankLevelDataJobLevel,
            @RequestParam(value = "rankLevelDataRank", required = false) String rankLevelDataRank) {

        List<RankLevelDataDTO> rankLevels = rankLevelDataService.getAllRankWithCivilian(
                rankLevelDataId,
                rankLevelDataDegree,
                rankLevelDataJobLevel,
                rankLevelDataRank
        );

        return ResponseEntity.ok(rankLevels);
    }

    @GetMapping("/getAllJobLevelWithJobGroup")
    public ResponseEntity<List<JobLevelWithJobGroupDataDto>> getAllJobLevelWithJobGroup() {

        List<JobLevelWithJobGroupDataDto> jobLevelWithJobGroup = rankLevelDataService.getAllJobLevelWithJobGroup();

        return ResponseEntity.ok(jobLevelWithJobGroup);
    }

    @GetMapping("/getRankWithGradeIdAndPositionId")
    public ResponseEntity<List<RankLevelDataDTO>> getRankWithGradeIdAndPositionId() {
        return ResponseEntity.ok().body(rankLevelDataService.getRankLevelData());
    }
}
