package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 4:55 PM
 * Describe:
 */
public enum SdkConfigSignatureTyeEnum {
    ALL("all"),

    CORP("corp"),

    AGENT("agent");

    private String value;

    SdkConfigSignatureTyeEnum(String value) {
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
    public static SdkConfigSignatureTyeEnum fromValue(String text) {
        for (SdkConfigSignatureTyeEnum b : SdkConfigSignatureTyeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
