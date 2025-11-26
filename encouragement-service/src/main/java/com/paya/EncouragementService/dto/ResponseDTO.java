package com.paya.EncouragementService.dto;

import com.paya.EncouragementService.controller.ErrorResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResponseDTO {
    private UUID reasonId;
    private List<EncouragementReasonTypeDTO> typesToAddOrUpdate;
    private List<UUID> deleteTheseTypes;
    private UUID reasonTypeId;
    private ErrorResponse errorResponse;



}