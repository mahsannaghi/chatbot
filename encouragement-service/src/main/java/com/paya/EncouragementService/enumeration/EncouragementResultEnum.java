package com.paya.EncouragementService.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EncouragementResultEnum {
    UNDER_REVIEW(0, "UNDER_REVIEW", "ارجاع جهت بررسی"),
    APPROVED(1, "APPROVED", "تایید شده"),
    REJECTED(2, "REJECTED", "رد شده"),
    SENT_FOR_CORRECTION(3, "SENT_FOR_CORRECTION", "ارسال برای اصلاح"),
    DRAFT(4, "DRAFT", "پیش نویس"),
    UNDER_COMMISSION_REVIEW(5, "UNDER_COMMISSION_REVIEW", "ارجاع جهت بررسی کمیسیون"),
    UNDER_VEDJA_REVIEW(6, "UNDER_VEDJA_REVIEW", "ارجاع جهت بررسی ودجا"),
    CORRECTION_AND_APPROVAL(7, "CORRECTION_AND_APPROVAL", "اصلاح و تایید"),
    NEED_FOR_ACCEPT(8, "NEED_FOR_ACCEPT", "نیازمند تایید"),
    SENT_FOR_ENCOURAGEMENT_SPECIALIST(9, "SEND_FOR_ENCOURAGEMENT_SPECIALIST", "ارسال به ودجا");

    private final int code;
    private final String title;
    private final String Description;

    public static EncouragementResultEnum fromCode(int code) {
        for (EncouragementResultEnum draft : EncouragementResultEnum.values()) {
            if (draft.getCode() == code) {
                return draft;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
