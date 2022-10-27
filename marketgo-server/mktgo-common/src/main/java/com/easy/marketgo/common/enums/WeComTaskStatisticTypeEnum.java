package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/5/22 9:29 PM
 * Describe:
 */
public enum WeComTaskStatisticTypeEnum {
    MASS_TASK_MEMBER_TOTAL_COUNT("MASS_TASK_MEMBER_TOTAL_COUNT"),

    MASS_TASK_EXTERNAL_USER_TOTAL_COUNT("MASS_TASK_EXTERNAL_USER_TOTAL_COUNT");

    private String value;

    WeComTaskStatisticTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static WeComTaskStatisticTypeEnum fromValue(String text) {
        for (WeComTaskStatisticTypeEnum b : WeComTaskStatisticTypeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
