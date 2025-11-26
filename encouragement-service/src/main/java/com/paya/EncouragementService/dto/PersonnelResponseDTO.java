package com.paya.EncouragementService.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PersonnelResponseDTO {

    private String correlationId;
    private List<? extends BasePersonnelDTO> personnelDTOList;
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Integer totalElements;
    private String requestQueueName;
    private String responseQueueName;
}
