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
 * @author wangKevin
 * @date
 */
public enum CdpManufacturerTypeEnum {
    /**
     *
     */
    SENSORS("SENSORS", "神策"),

    ANALYSYS("ANALYSYS", "易观"),

    LINKFLOW("LINKFLOW", "LinkFlow"),

    CONVERTLAB("CONVERTLAB", "ConvertLab"),

    HYPERS("HYPERS", "hypers"),

    GROWINGIO("GROWINGIO", "GrowingIO"),

    ALIYUN("ALICLOUD", "阿里云"),

    TENCENTCLOUD("TENCENTCLOUD", "腾讯云"),;

    private static final Object _LOCK = new Object();

    private static Logger logger = LoggerFactory.getLogger(CdpManufacturerTypeEnum.class);

    private static Map<String, CdpManufacturerTypeEnum> _MAP;

    private static List<CdpManufacturerTypeEnum> _LIST;

    private static List<CdpManufacturerTypeEnum> _ALL_LIST;

    static {
        synchronized (_LOCK) {
            Map<String, CdpManufacturerTypeEnum> map = new HashMap<>();
            List<CdpManufacturerTypeEnum> list = new ArrayList<>();
            List<CdpManufacturerTypeEnum> listAll = new ArrayList<>();
            for (CdpManufacturerTypeEnum item : CdpManufacturerTypeEnum.values()) {
                map.put(item.value, item);
                listAll.add(item);
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);
        }
    }

    private String value;

    private String name;

    CdpManufacturerTypeEnum(String value, String name) {

        this.value = value;
        this.name = name;
    }

    public static CdpManufacturerTypeEnum get(int value) {

        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<CdpManufacturerTypeEnum> list() {

        return _LIST;
    }

    public static List<CdpManufacturerTypeEnum> listAll() {

        return _ALL_LIST;
    }

    public String getName() {

        return this.name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getValue() {

        return this.value;
    }

    public void setValue(String value) {

        this.value = value;
    }
    @JsonCreator
    public static CdpManufacturerTypeEnum fromValue(String text) {

        for (CdpManufacturerTypeEnum b : CdpManufacturerTypeEnum.values()) {
            if (b.toString().equals(text)) {
                return b;
            }
        }
        return null;
    }


}
