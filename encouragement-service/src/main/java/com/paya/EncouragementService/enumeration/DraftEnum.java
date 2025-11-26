package com.paya.EncouragementService.enumeration;

public enum DraftEnum {

    Nothing(0),
    RegisterAndClose(1),
    Sent(2);
    private final int code;

    public int getCode() {
        return code;
    }

    DraftEnum(int code) {
        this.code = code;
    }

    public static DraftEnum fromCode(int code) {
        for (DraftEnum draft : DraftEnum.values()) {
            if (draft.getCode() == code) {
                return draft;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
