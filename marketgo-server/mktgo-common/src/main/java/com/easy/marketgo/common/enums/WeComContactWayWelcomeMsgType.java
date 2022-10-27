package com.easy.marketgo.common.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-24 10:02:41
 * @description : WeChatWorkWelcomeMsgType.java
 */
@AllArgsConstructor
@Getter
@Slf4j
public enum WeComContactWayWelcomeMsgType {
    /**
     *
     */
    TEXT(0, "text"),
    IMAGE(1, "image"),
    LINK(2, "link"),
    MINI_PROGRAM(3, "miniprogram");

    private static final Object _LOCK = new Object();


    private static Map<Integer, WeComContactWayWelcomeMsgType> _MAP;

    private static List<WeComContactWayWelcomeMsgType> _LIST;

    private static List<WeComContactWayWelcomeMsgType> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, WeComContactWayWelcomeMsgType> map = new HashMap<>();
            List<WeComContactWayWelcomeMsgType> list = new ArrayList<>();
            List<WeComContactWayWelcomeMsgType> listAll = new ArrayList<>();
            for (WeComContactWayWelcomeMsgType msgType : WeComContactWayWelcomeMsgType.values()) {
                map.put(msgType.value, msgType);
                listAll.add(msgType);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private int value;

    private String name;

    public static WeComContactWayWelcomeMsgType get(int value) {

        try {
            return _MAP.get(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<WeComContactWayWelcomeMsgType> list() {

        return _LIST;
    }

    public static List<WeComContactWayWelcomeMsgType> listAll() {

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
