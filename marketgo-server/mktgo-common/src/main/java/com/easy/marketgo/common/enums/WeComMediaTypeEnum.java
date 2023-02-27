package com.easy.marketgo.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 20:57
 * Describe:
 */
public enum WeComMediaTypeEnum {
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    FILE("FILE"),
    MINIPROGRAM("MINIPROGRAM"),
    LINK("LINK"),
    VOICE("VOICE"),
    LOGO("LOGO"),
    QRCODE("QRCODE"),
    TEXT("TEXT"),
    ;

    @Getter
    String type;

    WeComMediaTypeEnum(String type) {
        this.type = type;
    }

    public static WeComMediaTypeEnum fromValue(String type) {
        for (WeComMediaTypeEnum value : WeComMediaTypeEnum.values()) {
            String valueType = value.getType();
            if (Objects.equals(valueType, type)) {
                return value;
            }
        }
        return null;
    }

    public static boolean supportedMomentMediaType(String mediaType) {
        return Objects.equals(mediaType, WeComMediaTypeEnum.IMAGE.getType())
                || Objects.equals(mediaType, WeComMediaTypeEnum.VIDEO.getType())
                || Objects.equals(mediaType, WeComMediaTypeEnum.LINK.getType());
    }

    public static List<String> getSupportedMediaTypeList() {
        return Arrays.stream(WeComMediaTypeEnum.values()).map(WeComMediaTypeEnum::getType).collect(Collectors.toList());
    }
}
