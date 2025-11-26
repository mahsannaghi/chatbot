package com.paya.EncouragementService.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RankLevelDataDTO {
    private String rankType;
    private Integer rankTypeCode;
    private Integer rankTypeCivilian;

}

