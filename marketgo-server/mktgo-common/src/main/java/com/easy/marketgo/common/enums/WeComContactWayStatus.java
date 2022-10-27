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
 * @date :  2022-07-30 16:08:13
 * @description : WeChatWorkContactWayStatus.java
 */
public enum WeComContactWayStatus {

    ALL(-1, "全部"),
    DRAFT(1, "草稿"),
    PUSH(2, "发布");

    private static final Object _LOCK = new Object();
    private static Logger logger = LoggerFactory.getLogger(WeComContactWayStatus.class);
    private static Map<Integer, WeComContactWayStatus> _MAP;
    private static List<WeComContactWayStatus> _LIST;
    private static List<WeComContactWayStatus> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, WeComContactWayStatus> map = new HashMap<>();
            List<WeComContactWayStatus> list = new ArrayList<>();
            List<WeComContactWayStatus> listAll = new ArrayList<>();
            for (WeComContactWayStatus status : WeComContactWayStatus.values()) {
                map.put(status.value, status);
                listAll.add(status);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private int value;
    private String name;

    WeComContactWayStatus(int value, String name) {

        this.value = value;
        this.name = name;
    }

    public static WeComContactWayStatus get(int value) {

        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<WeComContactWayStatus> list() {

        return _LIST;
    }

    public static List<WeComContactWayStatus> listAll() {

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
