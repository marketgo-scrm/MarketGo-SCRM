package com.easy.marketgo.common.enums.taskcenter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:49 PM
 * Describe:
 */
public enum WeComTaskCenterRepeatType {
    REPEAT_TYPE_DAY("DAILY"),

    REPEAT_TYPE_WEEK("WEEKLY"),

    REPEAT_TYPE_MONTH("MONTHLY");

    private String value;

    WeComTaskCenterRepeatType(String value) {
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
    public static WeComTaskCenterRepeatType fromValue(String text) {
        for (WeComTaskCenterRepeatType b : WeComTaskCenterRepeatType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
