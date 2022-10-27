package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-24 09:45:10
 * @description : ExtUserCeilingStatus.java
 */
@AllArgsConstructor
@Getter
public enum ChannelContactWayExtUserCeilingStatus {
    /**
     *
     */
    enable("enable"),
    disable("disable"),

    ;

    private String desc;


    @JsonCreator
    public static ChannelContactWayExtUserCeilingStatus fromValue(String text) {

        for (ChannelContactWayExtUserCeilingStatus b : ChannelContactWayExtUserCeilingStatus.values()) {
            if (String.valueOf(b.desc).equals(text)) {
                return b;
            }
        }
        return null;
    }
}