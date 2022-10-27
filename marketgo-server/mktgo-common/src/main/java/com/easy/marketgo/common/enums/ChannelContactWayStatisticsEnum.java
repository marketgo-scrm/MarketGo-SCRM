package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 07:50:49
 * @description : ChannelContactWayStatisticsEnum.java
 */
@Getter
@AllArgsConstructor
public enum ChannelContactWayStatisticsEnum {
    MEMBER("MEMBER"),
    DATE("DATE"),;

    private String desc;

    @JsonCreator
    public static ChannelContactWayStatisticsEnum fromValue(String text) {

        for (ChannelContactWayStatisticsEnum b : ChannelContactWayStatisticsEnum.values()) {
            if (String.valueOf(b.desc).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
