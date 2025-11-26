package com.paya.EncouragementService.dto.v2;


import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class QuranicEncouragementFilterDTOV2 {
    private String fromDate;
    private String toDate;
    private String quranicSeniorityType;
    private Integer quranicEncouragementAmount;
    private String quranicSeniorityAmount;
    private PersonnelFilterDTOV2 personnel;
    private List<String> rankTypeCodeList;
}
