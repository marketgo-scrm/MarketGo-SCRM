package com.easy.marketgo.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/24/22 8:46 PM
 * Describe:
 */
@Getter
public enum WeComAttachmentTypeEnum {
    MOMENT(1),
    GOODS_PICTURE(2);

    @JsonValue
    Integer value;

    WeComAttachmentTypeEnum(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static WeComAttachmentTypeEnum fromValue(Integer type) {
        for (WeComAttachmentTypeEnum value : values()) {
            if (Objects.equals(type, value.getValue())) {
                return value;
            }
        }
        return null;
    }
}
