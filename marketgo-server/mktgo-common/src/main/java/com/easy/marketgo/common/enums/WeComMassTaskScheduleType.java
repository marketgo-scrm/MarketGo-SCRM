package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:49 PM
 * Describe:
 */
public enum WeComMassTaskScheduleType {
    IMMEDIATE("IMMEDIATE"),

    FIXED_TIME("FIXED_TIME"),

    REPEAT_TIME("REPEAT_TIME");

    private String value;

    WeComMassTaskScheduleType(String value) {
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
    public static WeComMassTaskScheduleType fromValue(String text) {
        for (WeComMassTaskScheduleType b : WeComMassTaskScheduleType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
