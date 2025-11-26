package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MembershipType {

    PERMANENT(0, "رسمي"),
    CONTRACTUAL(1, "پيماني"),
    TEMPORARY(2, "قراردادي"),
    OUTSOURCED(3, "خريد خدمت");

    private final int code;

    public String getPersianName() {
        return persianName;
    }

    private final String persianName;

    public int getCode() {
        return code;
    }

    public static MembershipType fromCode(int code) {
        for (MembershipType draft : MembershipType.values()) {
            if (draft.getCode() == code) {
                return draft;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
