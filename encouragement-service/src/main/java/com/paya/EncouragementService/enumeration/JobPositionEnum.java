package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobPositionEnum {
    JOB_POSITION_81(0, "جایگاه شغلی 81"),
    JOB_POSITION_91(1, "جایگاه شغلی 91"),
    JOB_POSITION_101(2, "جایگاه شغلی 101"),
    JOB_POSITION_111(3, "جایگاه شغلی 111"),
    JOB_POSITION_121(4, "جایگاه شغلی 121"),
    JOB_POSITION_131(5, "جایگاه شغلی 131"),
    JOB_POSITION_141(6, "جایگاه شغلی 141"),
    JOB_POSITION_151(7, "جایگاه شغلی 151"),
    JOB_POSITION_161(8, "جایگاه شغلی 161"),
    JOB_POSITION_171(9, "جایگاه شغلی 171"),
    JOB_POSITION_181(10, "جایگاه شغلی 181"),
    JOB_POSITION_191(11, "جایگاه شغلی 191"),
    JOB_POSITION_201(12, "جایگاه شغلی 201"),
    JOB_POSITION_251(13, "جایگاه شغلی 251"),
    JOB_POSITION_301(14, "جایگاه شغلی 301"),

    JOB_POSITION_82(15, "جایگاه شغلی 82"),
    JOB_POSITION_92(16, "جایگاه شغلی 92"),
    JOB_POSITION_102(17, "جایگاه شغلی 102"),
    JOB_POSITION_112(18, "جایگاه شغلی 112"),
    JOB_POSITION_122(19, "جایگاه شغلی 122"),
    JOB_POSITION_132(20, "جایگاه شغلی 132"),
    JOB_POSITION_142(21, "جایگاه شغلی 142"),
    JOB_POSITION_152(22, "جایگاه شغلی 152"),
    JOB_POSITION_162(23, "جایگاه شغلی 162"),
    JOB_POSITION_172(24, "جایگاه شغلی 172"),
    JOB_POSITION_182(25, "جایگاه شغلی 182"),
    JOB_POSITION_192(26, "جایگاه شغلی 192"),
    JOB_POSITION_202(27, "جایگاه شغلی 202"),
    JOB_POSITION_252(28, "جایگاه شغلی 252"),
    JOB_POSITION_302(29, "جایگاه شغلی 302"),

    JOB_POSITION_83(30, "جایگاه شغلی 83"),
    JOB_POSITION_93(31, "جایگاه شغلی 93"),
    JOB_POSITION_103(32, "جایگاه شغلی 103"),
    JOB_POSITION_113(33, "جایگاه شغلی 113"),
    JOB_POSITION_123(34, "جایگاه شغلی 123"),
    JOB_POSITION_133(35, "جایگاه شغلی 133"),
    JOB_POSITION_143(36, "جایگاه شغلی 143"),
    JOB_POSITION_153(37, "جایگاه شغلی 153"),
    JOB_POSITION_163(38, "جایگاه شغلی 163"),
    JOB_POSITION_173(39, "جایگاه شغلی 173"),
    JOB_POSITION_183(40, "جایگاه شغلی 183"),
    JOB_POSITION_193(41, "جایگاه شغلی 193"),
    JOB_POSITION_203(42, "جایگاه شغلی 203"),
    JOB_POSITION_253(43, "جایگاه شغلی 253"),
    JOB_POSITION_303(44, "جایگاه شغلی 303"),

    JOB_POSITION_84(45, "جایگاه شغلی 84"),
    JOB_POSITION_94(46, "جایگاه شغلی 94"),
    JOB_POSITION_104(47, "جایگاه شغلی 104"),
    JOB_POSITION_114(46, "جایگاه شغلی 114"),
    JOB_POSITION_124(49, "جایگاه شغلی 124"),
    JOB_POSITION_134(50, "جایگاه شغلی 134"),
    JOB_POSITION_144(51, "جایگاه شغلی 144"),
    JOB_POSITION_154(52, "جایگاه شغلی 154"),
    JOB_POSITION_164(53, "جایگاه شغلی 164"),
    JOB_POSITION_174(54, "جایگاه شغلی 174"),
    JOB_POSITION_184(55, "جایگاه شغلی 184"),
    JOB_POSITION_194(56, "جایگاه شغلی 194"),
    JOB_POSITION_204(57, "جایگاه شغلی 204"),
    JOB_POSITION_254(58, "جایگاه شغلی 254"),
    JOB_POSITION_304(59, "جایگاه شغلی 304"),

    JOB_POSITION_85(60, "جایگاه شغلی 85"),
    JOB_POSITION_95(61, "جایگاه شغلی 95"),
    JOB_POSITION_105(62, "جایگاه شغلی 105"),
    JOB_POSITION_115(63, "جایگاه شغلی 115"),
    JOB_POSITION_125(64, "جایگاه شغلی 125"),
    JOB_POSITION_135(65, "جایگاه شغلی 135"),
    JOB_POSITION_145(66, "جایگاه شغلی 145"),
    JOB_POSITION_155(67, "جایگاه شغلی 155"),
    JOB_POSITION_165(68, "جایگاه شغلی 165"),
    JOB_POSITION_175(69, "جایگاه شغلی 175"),
    JOB_POSITION_185(70, "جایگاه شغلی 185"),
    JOB_POSITION_195(71, "جایگاه شغلی 195"),
    JOB_POSITION_205(72, "جایگاه شغلی 205"),
    JOB_POSITION_255(73, "جایگاه شغلی 255"),
    JOB_POSITION_305(74, "جایگاه شغلی 305");
    private final int code;
    private final String persianName;

    public int getCode() {
        return code;
    }

    public static JobPositionEnum fromCode(int code) {
        for (JobPositionEnum draft : JobPositionEnum.values()) {
            if (draft.getCode() == code) {
                return draft;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
