package com.paya.EncouragementService.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoleEnum {
    Manager(0),
    Commission(1),
    Vedja(2);
    private final Integer value;
}
