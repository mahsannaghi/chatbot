package com.paya.EncouragementService.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewResultEnum {
    UNDER_REVIEW(0, "UNDER_REVIEW", "ارجاع جهت بررسی"),
    APPROVED(1, "APPROVED", "تایید شده"),
    REJECTED(2, "REJECTED", "رد شده"),
    SENT_FOR_REGISTRAR_CORRECTION(3, "SENT_FOR_REGISTRAR_CORRECTION", "ارسال برای اصلاح ثبت کننده"),
    DRAFT(4, "DRAFT", "پیش نویس"),
    UNDER_COMMISSION_REVIEW(5, "UNDER_COMMISSION_REVIEW", "ارجاع جهت بررسی کمیسیون"),
    UNDER_VEDJA_REVIEW(6, "UNDER_VEDJA_REVIEW", "ارجاع جهت بررسی کمیسیون عالی ودجا"),
    CORRECTION_AND_APPROVAL(7, "CORRECTION_AND_APPROVAL", "اصلاح و تایید"),
    SENT_FOR_RECENT_MANAGER_CORRECTION(8, "SENT_FOR_RECENT_MANAGER_CORRECTION", "ارسال برای اصلاح مدیر قبلی"),
    SENT_FOR_ENCOURAGEMENT_SPECIALIST(9, "SEND_FOR_ENCOURAGEMENT_SPECIALIST", "ارسال به ودجا"),
    REJECT_TO_COMMISSION(10, "REJECT_TO_COMMISSION", "بازگشت به کمیسیون");

    private final int code;
    private final String title;
    private final String Description;
}
