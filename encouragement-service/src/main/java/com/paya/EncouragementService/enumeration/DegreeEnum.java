package com.paya.EncouragementService.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DegreeEnum {
    DIPLOMA(0, "دیپلم"),
    ASSOCIATE(1, " فوق دیپلم"),
    BACHELOR(2, "لیسانس"),
    MASTER(3, "فوق لیسانس"),
    DOCTORATE(4, "دکتری"),
    POSTDOCTORAL(5, "فوق دکتری");

    private final Integer DegreeCode;
    private final String DegreeTitle;


}
