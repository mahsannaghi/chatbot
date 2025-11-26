package com.paya.EncouragementService.dto;

// package com.paya.EncouragementService.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EncouragementRequestDTO {
    private UUID reasonId;
    private List<EncouragementReasonTypeDTO> typesToAddOrUpdate;
    private List<UUID> deleteTheseTypes;




}
