package com.paya.EncouragementService.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReasonDTO {
    private UUID reasonId;
    private String reasonTitle;
}
