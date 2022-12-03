package com.easy.marketgo.common.enums.cdp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/30/22 3:52 PM
 * Describe:
 */
public enum CdpSyncCrowdUsersStatusEnum {

    UNSTART("UNSTART"),

    SYNCING("SYNCING"),

    SYNC_COMPLETE("SYNC_COMPLETE"),

    SYNC_FAILED("SYNC_FAILED"),

    FINISHED("FINISHED");

    private String value;

    CdpSyncCrowdUsersStatusEnum(String value) {
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
    public static CdpSyncCrowdUsersStatusEnum fromValue(String text) {
        for (CdpSyncCrowdUsersStatusEnum b : CdpSyncCrowdUsersStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
