package com.paya.EncouragementService.entity;

import com.paya.EncouragementService.enumeration.TypeCategoryEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "tbl_encouragement_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncouragementType extends TblBase{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID encouragementTypeId;

    @Column(name = "encouragement_type_title", length = 50, nullable = false, unique = true)
    private String encouragementTypeTitle;

    @Column(name = "encouragement_type_is_active", nullable = false)
    private boolean encouragementTypeIsActive = true;

    @Column(name = "encouragement_type_nature_type", nullable = false)
    private int encouragementTypeNatureType;

    @Column(name = "encouragement_type_category", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TypeCategoryEnum encouragementTypeCategory = TypeCategoryEnum.NORMAL;
}
