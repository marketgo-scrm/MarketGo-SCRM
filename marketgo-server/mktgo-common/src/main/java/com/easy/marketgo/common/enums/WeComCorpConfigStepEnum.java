package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 7:09 PM
 * Describe:
 */
public enum WeComCorpConfigStepEnum {
    CORP_MSG("CORP"),

    AGENT_MSG("AGENT"),

    CONTACTS_MSG("CONTACTS"),

    EXTERNAL_USER_MSG("EXTERNAL_USER");

    private String value;

    WeComCorpConfigStepEnum(String value) {
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
    public static WeComCorpConfigStepEnum fromValue(String text) {
        for (WeComCorpConfigStepEnum b : WeComCorpConfigStepEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
