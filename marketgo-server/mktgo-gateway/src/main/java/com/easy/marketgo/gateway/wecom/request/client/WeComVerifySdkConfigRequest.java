package com.easy.marketgo.gateway.wecom.request.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/3/23 11:47 PM
 * Describe:
 */
@Data
@ApiModel(description = "获取企微 SDK 配置")
public class WeComVerifySdkConfigRequest {
    @ApiModelProperty(value = "当前页面url")
    private String url = null;

    /**
     * 校验类型
     */
    public enum TypeEnum {
        ALL("all"),

        CORP("corp"),

        AGENT("agent");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @ApiModelProperty(value = "校验类型")
    private TypeEnum type = null;
}
