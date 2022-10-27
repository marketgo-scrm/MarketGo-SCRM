package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ssk
 * @date
 */
public enum YesNoStatus {
    /**
     *
     */
    ALL(-1, "全部"),
    //    DEFAULT(0, "默认"),
    NO(0, "否"),

    YES(1, "是");

    private static final Object _LOCK = new Object();

    private static Logger logger = LoggerFactory.getLogger(YesNoStatus.class);

    private static Map<Integer, YesNoStatus> _MAP;

    private static List<YesNoStatus> _LIST;

    private static List<YesNoStatus> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<Integer, YesNoStatus> map = new HashMap<>();
            List<YesNoStatus> list = new ArrayList<>();
            List<YesNoStatus> listAll = new ArrayList<>();
            for (YesNoStatus yesNoStatus : YesNoStatus.values()) {
                map.put(yesNoStatus.value, yesNoStatus);
                listAll.add(yesNoStatus);
                if (!yesNoStatus.equals(ALL)) {
                    list.add(yesNoStatus);
                }
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private int value;

    private String name;

    YesNoStatus(int value, String name) {

        this.value = value;
        this.name = name;
    }

    public static YesNoStatus get(int value) {

        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<YesNoStatus> list() {

        return _LIST;
    }

    public static List<YesNoStatus> listAll() {

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
    @JsonCreator
    public static YesNoStatus fromValue(String text) {

        for (YesNoStatus b : YesNoStatus.values()) {
            if (b.toString().equals(text)) {
                return b;
            }
        }
        return null;
    }


}
