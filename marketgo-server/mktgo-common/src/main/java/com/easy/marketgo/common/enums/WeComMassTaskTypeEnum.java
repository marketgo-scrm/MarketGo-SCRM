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
    SINGLE,
    /**
     * 群发客户群
     */
    GROUP,
    /**
     * 群发朋友圈
     */
    MOMENT,
    /**
     * 活码
     */
    LIVE_CODE;

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
}
