package com.easy.marketgo.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 9:05 PM
 * Describe:
 */
public enum WeComTaskCenterTargetTypeEnum {
    /**
     * 分钟
     */
    MINUTE("MINUTE", "分钟"),
    /**
     * 小时
     */
    HOUR("HOUR", "小时"),
    /**
     * 天
     */
    DAY("DAY", "天"),
   ;

    private String name;
    private String cname;

    WeComTaskCenterTargetTypeEnum(String name, String cname) {
        this.name = name;
        this.cname = cname;
    }

    public static boolean isSupported(String type) {
        return Arrays.stream(values()).anyMatch(WeComMassTaskTypeEnum -> WeComMassTaskTypeEnum.name().equals(type));
    }

    public static WeComTaskCenterTargetTypeEnum fromValue(String type) {
        for (WeComTaskCenterTargetTypeEnum value : WeComTaskCenterTargetTypeEnum.values()) {
            String valueType = value.name();
            if (Objects.equals(valueType, type)) {
                return value;
            }
        }
        return null;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
