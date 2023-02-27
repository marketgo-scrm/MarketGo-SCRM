package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/5/22 9:33 PM
 * Describe:
 */
public enum WeComMassTaskMetricsType {
    MASS_TASK_MEMBER("MEMBER"),

    MASS_TASK_EXTERNAL_USER("EXTERNAL_USER"),
    MASS_TASK_COMMENTS("COMMENTS"),
    MASS_TASK_RATE("RATE"),
    ;

    private String value;

    WeComMassTaskMetricsType(String value) {
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
    public static WeComMassTaskMetricsType fromValue(String text) {
        for (WeComMassTaskMetricsType b : WeComMassTaskMetricsType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
