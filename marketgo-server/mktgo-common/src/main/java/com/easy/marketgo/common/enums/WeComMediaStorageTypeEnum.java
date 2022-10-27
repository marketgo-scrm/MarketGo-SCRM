package com.easy.marketgo.common.enums;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/24/22 12:37 PM
 * Describe:
 */
public enum WeComMediaStorageTypeEnum {
    /**
     * mysql数据库
     */
    MYSQL,
    /**
     * CDN
     */
    CDN,
    /**
     * hdfs
     */
    HDSF;


    public static WeComMediaStorageTypeEnum fromValue(String type) {
        for (WeComMediaStorageTypeEnum value : WeComMediaStorageTypeEnum.values()) {
            String valueType = value.name();
            if (Objects.equals(valueType, type)) {
                return value;
            }
        }
        return null;
    }
}
