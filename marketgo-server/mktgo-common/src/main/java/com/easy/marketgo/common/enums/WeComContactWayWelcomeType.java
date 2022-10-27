package com.easy.marketgo.common.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:08:08
 * @description : WeComContactWayWelcomeType.java
 */
public enum WeComContactWayWelcomeType {

    ALL(-1, "全部"),
    CLOSED(0, "不开启欢迎语"),
    CHANNEL(1, "渠道欢迎语"),
    USER(2, "员工欢迎语");

    private static final Object _LOCK = new Object();
    private static Logger logger = LoggerFactory.getLogger(WeComContactWayWelcomeType.class);
    private static Map<Integer, WeComContactWayWelcomeType> _MAP;
    private static List<WeComContactWayWelcomeType> _LIST;
    private static List<WeComContactWayWelcomeType> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, WeComContactWayWelcomeType> map = new HashMap<>();
            List<WeComContactWayWelcomeType> list = new ArrayList<>();
            List<WeComContactWayWelcomeType> listAll = new ArrayList<>();
            for (WeComContactWayWelcomeType o : WeComContactWayWelcomeType.values()) {
                map.put(o.value, o);
                listAll.add(o);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private int value;
    private String name;

    WeComContactWayWelcomeType(int value, String name) {

        this.value = value;
        this.name = name;
    }

    public static WeComContactWayWelcomeType get(int value) {

        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<WeComContactWayWelcomeType> list() {

        return _LIST;
    }

    public static List<WeComContactWayWelcomeType> listAll() {

        return _ALL_LIST;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getValue() {

        return this.value;
    }

    public void setValue(int value) {

        this.value = value;
    }

}
