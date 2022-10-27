package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/8/22 6:17 PM
 * Describe:
 */
public enum WeComMassTaskMemberStatusEnum {
    /**
     * 未发送
     */
    UNSENT("UNSENT", 0),
    /**
     * 已发送
     */
    SENT("SENT", 2),
    /**
     * 发送失败
     */
    SENT_FAIL("SENT_FAIL", 1),
    /**
     * 员工离职
     */
    DIMISSION("DIMISSION", 3);
    private String value;
    private int status;

    WeComMassTaskMemberStatusEnum(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @JsonValue
    public String getValue() {
        return value;
    }


    public static  int getMemberStatus(WeComMassTaskMemberStatusEnum status) {
        return status.status;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static WeComMassTaskMemberStatusEnum fromValue(String text) {
        for (WeComMassTaskMemberStatusEnum b : WeComMassTaskMemberStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
