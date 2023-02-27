package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/25/22 11:23 AM
 * Describe:
 */
public enum WeComSidebarTypeEnum {
    TASK_CENTER_SEND_CONTENT("TASK_CENTER_SEND_CONTENT"),

    CDP_USER_GROUP("CDP"),

    OFFLINE_USER_GROUP("OFFLINE");

    private String value;

    WeComSidebarTypeEnum(String value) {
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
    public static WeComSidebarTypeEnum fromValue(String text) {
        for (WeComSidebarTypeEnum b : WeComSidebarTypeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
