package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankTypeEnum {
    SOLDIER(0, "سرباز"),
    THIRD_SERGEANT(1, "گروهبان سوم"),
    SECOND_SERGEANT(2, "گروهبان دوم"),
    FIRST_SERGEANT(3, "گروهبان يکم"),
    SECOND_MASTER_SERGEANT(4, "استوار دوم"),
    FIRST_MASTER_SERGEANT(5, "استوار يکم"),
    THIRD_LIEUTENANT(6, "ستوان سوم"),
    SECOND_LIEUTENANT(7, "ستوان دوم"),
    FIRST_LIEUTENANT(8, "ستوان يکم"),
    CAPTAIN(9, "سروان"),
    MAJOR(10, "سرگرد"),
    LIEUTENANT_COLONEL(11, "سرهنگ دوم"),
    COLONEL(12, "سرهنگ تمام"),
    BRIGADIER_GENERAL(13, "سرتیپ دوم"),
    MAJOR_GENERAL(14, "سرتیپ يکم"),
    LIEUTENANT_GENERAL(15, "سرلشکر"),
    GENERAL1(16, "سپهبد"),
    GENERAL2(17, "ارتشبد");

    private final Integer rankCode;
    private final String persianName;


    public static RankTypeEnum fromRankCode(int code) {
        for (RankTypeEnum rank : RankTypeEnum.values()) {
            if (rank.getRankCode() == code) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }


}
