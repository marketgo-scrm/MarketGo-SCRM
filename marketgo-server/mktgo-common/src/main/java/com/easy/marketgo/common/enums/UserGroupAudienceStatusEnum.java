package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/29/22 7:47 PM
 * Describe:
 */
public enum UserGroupAudienceStatusEnum {
    COMPUTING("COMPUTING"),

    SUCCEED("SUCCEED"),

    FAILED("FAILED");

    private String value;

    UserGroupAudienceStatusEnum(String value) {
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
    public static UserGroupAudienceStatusEnum fromValue(String text) {
        for (UserGroupAudienceStatusEnum b : UserGroupAudienceStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
