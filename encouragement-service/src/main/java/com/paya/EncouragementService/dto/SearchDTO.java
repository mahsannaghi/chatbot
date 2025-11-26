package com.paya.EncouragementService.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SearchDTO {
    private UUID reasonId;
    private String reasonTitle;
    private List<EncouragementReasonTypeDTO> types;
}