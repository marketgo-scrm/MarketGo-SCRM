package com.easy.marketgo.gateway.wecom.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:04 PM
 * Describe:
 */
@Data
public class AddOrUpdateContactWayRequest {
    @JsonProperty("config_id")
    private String configId;
    private Integer type;
    private Integer scene;
    private Integer style;
    private String remark;
    @JsonProperty("skip_verify")
    private Boolean skipVerify;
    private String state;
    private List<String> user;
    private List<Integer> party;
    @JsonProperty("is_temp")
    private Boolean isTemp;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("chat_expires_in")
    private Long chatExpiresIn;
    @JsonProperty("unionid")
    private String unionId;
    private ConclusionsMessage conclusions;

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConclusionsMessage {
        private TextMessage text;
        private ImageAttachmentsMessage image;
        private LinkAttachmentsMessage link;
        private MiniProgramAttachmentsMessage miniprogram;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TextMessage implements Serializable {
        private String content;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageAttachmentsMessage implements Serializable {
        @JsonProperty("media_id")
        private String mediaId;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkAttachmentsMessage implements Serializable {
        private String title;
        @JsonProperty("picurl")
        private String picUrl;
        private String url;
        private String desc;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MiniProgramAttachmentsMessage implements Serializable {
        private String title;
        @JsonProperty("pic_media_id")
        private String picMediaId;
        @JsonProperty("appid")
        private String appId;
        private String page;
    }

}
