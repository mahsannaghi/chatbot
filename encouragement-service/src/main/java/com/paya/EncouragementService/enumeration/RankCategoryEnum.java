package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankCategoryEnum {

    MILITARY(0, "نظامي"),
    CIVILIAN(1, "غير نظامي");
    private final int code;
    private final String persianName;

    public int getCode() {
        return code;
    }

    public String getPersianName() {
        return persianName;
    }

    public static RankCategoryEnum fromCode(int code) {
        for (RankCategoryEnum draft : RankCategoryEnum.values()) {
            if (draft.getCode() == code) {
                return draft;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}