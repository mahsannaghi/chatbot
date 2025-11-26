package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobLevelWithJobGroupDataDto {
    private String jobGroup;
    private Integer jobGroupCode;
    private String jobGroupLevel;
    private Integer jobGroupLevelCode;
    private String jobPosition;
    private Integer jobPositionCode;
}
