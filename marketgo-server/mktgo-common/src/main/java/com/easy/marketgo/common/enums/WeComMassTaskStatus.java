package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 6:02 PM
 * Describe:
 */
public enum WeComMassTaskStatus {
    UNSTART("UNSTART"),

    COMPUTING("COMPUTING"),

    COMPUTE_FAILED("COMPUTE_FAILED"),

    COMPUTED("COMPUTED"),

    SENDING("SENDING"),

    SENT("SENT"),

    SEND_FAILED("SEND_FAILED"),

    FINISHED("FINISHED");

    private String value;

    WeComMassTaskStatus(String value) {
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
    public static WeComMassTaskStatus fromValue(String text) {
        for (WeComMassTaskStatus b : WeComMassTaskStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
