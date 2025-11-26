package com.paya.EncouragementService.dto;

import com.paya.EncouragementService.dto.v2.EncouragementFilterDTOV2;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.PageImpl;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class EncouragementAndPunishmentDTO {
    private PageImpl<EncouragementFilterDTOV2> encouragementList;
    private PageImpl<PunishmentDTO> punishmentList;

}
