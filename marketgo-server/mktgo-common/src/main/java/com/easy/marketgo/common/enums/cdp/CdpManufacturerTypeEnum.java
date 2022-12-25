package com.easy.marketgo.common.enums.cdp;

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
    SENSORS("SENSORS", "神策", "sensorsCdpCrowdService", "支持神策CDP接入和使用"),

    ANALYSYS("ANALYSYS", "易观", "analysysCdpCrowdService", "支持易观CDP接入和使用"),

    LINKFLOW("LINKFLOW", "LinkFlow", "AnalysysCdpCrowdService", "支持LinkFlow CDP接入和使用"),

    CONVERTLAB("CONVERTLAB", "ConvertLab", "AnalysysCdpCrowdService", "支持ConvertLab CDP接入和使用"),

    HYPERS("HYPERS", "hypers", "AnalysysCdpCrowdService", "支持hypers CDP接入和使用"),

    GROWINGIO("GROWINGIO", "GrowingIO", "AnalysysCdpCrowdService", "支持GrowingIO CDP接入和使用"),

    ALIYUN("ALICLOUD", "阿里云", "AnalysysCdpCrowdService", "支持阿里云CDP接入和使用"),

    TENCENTCLOUD("TENCENTCLOUD", "腾讯云", "AnalysysCdpCrowdService", "支持腾讯云CDP接入和使用"),
    ;

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

    private String service;

    private String desc;

    CdpManufacturerTypeEnum(String value, String name, String service, String desc) {

        this.value = value;
        this.name = name;
        this.service = service;
        this.desc = desc;
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

    public String getService() {

        return this.service;
    }

    public void setService(String service) {

        this.service = service;
    }

    public String getDesc() {

        return this.desc;
    }

    public void setDesc(String desc) {

        this.desc = desc;
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
