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
 * @date :  2022-07-30 16:40:40
 * @description : WeComContactWayMsgUserType.java
 */
public enum WeComContactWayMsgUserType {
    USER(1, "员工"),
    DEPARTMENT(2, "部门");

    private static final Object _LOCK = new Object();

    private static Logger logger = LoggerFactory.getLogger(WeComContactWayMsgUserType.class);

    private static Map<Integer, WeComContactWayMsgUserType> _MAP;

    private static List<WeComContactWayMsgUserType> _LIST;

    private static List<WeComContactWayMsgUserType> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, WeComContactWayMsgUserType> map = new HashMap<>();
            List<WeComContactWayMsgUserType> list = new ArrayList<>();
            List<WeComContactWayMsgUserType> listAll = new ArrayList<>();
            for (WeComContactWayMsgUserType msgType : WeComContactWayMsgUserType.values()) {
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

    WeComContactWayMsgUserType(int value, String name) {

        this.value = value;
        this.name = name;
    }

    public static WeComContactWayMsgUserType get(int value) {

        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<WeComContactWayMsgUserType> list() {

        return _LIST;
    }

    public static List<WeComContactWayMsgUserType> listAll() {

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
