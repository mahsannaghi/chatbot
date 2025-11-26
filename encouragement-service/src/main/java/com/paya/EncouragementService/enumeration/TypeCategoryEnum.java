package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeCategoryEnum {
    NORMAL(0, "تشویق عادی"),
    SPECIALIST(1, "تشویق مخصوص کارشناس");

    private final Integer code;
    private final String title;
}
