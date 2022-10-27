package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 21:55:13
 * @description : PermissionsEnum.java
 */
@Getter
@AllArgsConstructor
public enum PermissionsEnum {
    disable("true"),
    enable("false"),;

    private String desc;

    @JsonCreator
    public static PermissionsEnum fromValue(String text) {

        for (PermissionsEnum b : PermissionsEnum.values()) {
            if (String.valueOf(b.desc).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
