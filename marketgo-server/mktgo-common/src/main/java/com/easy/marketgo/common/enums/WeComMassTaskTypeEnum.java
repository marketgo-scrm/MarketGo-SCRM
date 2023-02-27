package com.easy.marketgo.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 9:05 PM
 * Describe:
 */
public enum WeComMassTaskTypeEnum {
    /**
     * 群发好友
     */
    SINGLE("SINGLE", "客户"),
    /**
     * 群发客户群
     */
    GROUP("GROUP", "客户群"),
    /**
     * 群发朋友圈
     */
    MOMENT("MOMENT", "朋友圈"),
    /**
     * 活码
     */
    LIVE_CODE("LIVE_CODE", "活码");

    private String name;
    private String cname;

    WeComMassTaskTypeEnum(String name, String cname) {
        this.name = name;
        this.cname = cname;
    }

    public static boolean isSupported(String type) {
        return Arrays.stream(values()).anyMatch(WeComMassTaskTypeEnum -> WeComMassTaskTypeEnum.name().equals(type));
    }

    public static WeComMassTaskTypeEnum fromValue(String type) {
        for (WeComMassTaskTypeEnum value : WeComMassTaskTypeEnum.values()) {
            String valueType = value.name();
            if (Objects.equals(valueType, type)) {
                return value;
            }
        }
        return null;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
