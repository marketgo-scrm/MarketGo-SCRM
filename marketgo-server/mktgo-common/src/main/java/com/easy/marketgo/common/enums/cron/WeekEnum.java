package com.easy.marketgo.common.enums.cron;

import com.google.common.base.Strings;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/25/22 11:50 PM
 * Describe:
 */

@Getter
public enum WeekEnum {

    /**
     * 周一
     */
    Monday("1", "Mon"),
    /**
     * 周二
     */
    Tuesday("2", "Tue"),
    /**
     * 周三
     */
    Wednesday("3", "Wed"),
    /**
     * 周四
     */
    Thursday("4", "Thu"),
    /**
     * 周五
     */
    Friday("5", "Fri"),
    /**
     * 周六
     */
    Saturday("6", "Sat"),
    /**
     * 周日
     */
    Sunday("7", "Sun"),
    ;

    private static final Map<String, WeekEnum> CODE_ROLE_MAP = new HashMap<>();

    private static final Map<String, WeekEnum> NAME_ROLE_MAP = new HashMap<>();

    static {
        for (WeekEnum type : WeekEnum.values()) {
            NAME_ROLE_MAP.put(type.name, type);
            CODE_ROLE_MAP.put(type.code, type);
        }
    }

    private final String name;

    private final String code;

    WeekEnum(final String name, final String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * to WeekEnum by code.
     *
     * @param code code
     * @return WeekEnum
     */
    public static WeekEnum codeOf(final String code) {
        if (StringUtils.isBlank(code)) {
            return Sunday;
        }
        WeekEnum matchType = CODE_ROLE_MAP.get(code);
        return Objects.isNull(matchType) ? Sunday : matchType;
    }

    /**
     * to WeekEnum by name.
     *
     * @param name name
     * @return WeekEnum
     */
    public static WeekEnum nameOf(final String name) {
        if (Strings.isNullOrEmpty(name)) {
            return Sunday;
        }
        WeekEnum matchType = NAME_ROLE_MAP.get(name);
        return Objects.isNull(matchType) ? Sunday : matchType;
    }
}