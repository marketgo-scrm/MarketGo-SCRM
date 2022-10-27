package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 16:42:11
 * @description : BaseRoleEnum.java
 */
public enum BaseRoleEnum {
    super_administrator("超级管理员"),
    administrator("管理员"),
    department_administrator("部门管理员"),
    ordinary_employees("普通员工"),
    ;

    private String desc;

    BaseRoleEnum(String desc) {
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return String.valueOf(desc);
    }

    @JsonCreator
    public static BaseRoleEnum fromValue(String text) {
        for (BaseRoleEnum b : BaseRoleEnum.values()) {
            if (String.valueOf(b.desc).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
