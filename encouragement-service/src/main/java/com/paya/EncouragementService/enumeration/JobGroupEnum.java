package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobGroupEnum {
    JOB_GROUP_0(0, "گروه شغلی0"),
    JOB_GROUP_1(1, "گروه شغلی1"),
    JOB_GROUP_2(2, "گروه شغلی2"),
    JOB_GROUP_3(18, "گروه شغلی18"),
    JOB_GROUP_4(3, "گروه شغلی3"),
    JOB_GROUP_5(4, "گروه شغلی4"),
    JOB_GROUP_6(5, "گروه شغلی5"),
    JOB_GROUP_7(6, "گروه شغلی6"),
    JOB_GROUP_8(7, "گروه شغلی7"),
    JOB_GROUP_9(8, "گروه شغلی8"),
    JOB_GROUP_10(9, "گروه شغلی9"),
    JOB_GROUP_11(10, "گروه شغلی10"),
    JOB_GROUP_12(11, "گروه شغلی11"),
    JOB_GROUP_13(12, "گروه شغلی12"),
    JOB_GROUP_14(13, "گروه شغلی13"),
    JOB_GROUP_15(14, "گروه شغلی14"),
    JOB_GROUP_16(15, "گروه شغلی15"),
    JOB_GROUP_17(16, "گروه شغلی16"),
    JOB_GROUP_18(17, "گروه شغلی17"),
    JOB_GROUP_19(19, "گروه شغلی19");
    private final int code;
    private final String persianName;

    public int getCode() {
        return code;
    }

    public static JobGroupEnum fromCode(int code) {
        for (JobGroupEnum draft : JobGroupEnum.values()) {
            if (draft.getCode() == code) {
                return draft;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
