package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-24 09:45:46
 * @description : OnlineStatus.java
 */
@AllArgsConstructor
@Getter
public enum ChannelContactWayOnlineType {
    /**
     *
     */
    onLine_all_day("onLine_all_day"),

    ;

    private String desc;


    @JsonCreator
    public static ChannelContactWayOnlineType fromValue(String text) {

        for (ChannelContactWayOnlineType b : ChannelContactWayOnlineType.values()) {
            if (String.valueOf(b.desc).equals(text)) {
                return b;
            }
        }
        return null;
    }
}