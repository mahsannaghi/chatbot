package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewTypeEnum {
    VEDJA_COMMISSION(0 , "VEDJA_COMMISSION" , "کمیسون ودجا"),
    ORDINARY_COMMISSION(1 , "ORDINARY_COMMISSION" , "کمیسیون عادی"),
    DRAFT(2 , "DRAFT" , "پیشنویس"),
    ORDINARY_REVIEWER(3 , "ORDINARY_REVIEWER" , "بررسی کننده عادی ");


    private final int code;
    private final String title;
    private final String description;
}
