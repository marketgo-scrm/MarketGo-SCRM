package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 6:25 PM
 * Describe:
 */
public enum WeComMassTaskExternalUserStatusEnum {
    /**
     * 未识别的用户
     */
    UNRECOGNIZED("UNRECOGNIZED"),
    /**
     * 未接收
     */
    UNDELIVERED("UNDELIVERED"),
    /**
     * 已接收
     */
    DELIVERED("DELIVERED"),
    /**
     * 非好友
     */
    UNFRIEND("UNFRIEND"),
    /**
     * 接收达上限
     */
    EXCEED_LIMIT("EXCEED_LIMIT"),
    /**
     * 评论
     */
    COMMENT("COMMENT"),
    /**
     * 点赞
     */
    LIKE("LIKE"),
    ;

    private String value;

    WeComMassTaskExternalUserStatusEnum(String value) {
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
    public static WeComMassTaskExternalUserStatusEnum fromValue(String text) {
        for (WeComMassTaskExternalUserStatusEnum b : WeComMassTaskExternalUserStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
