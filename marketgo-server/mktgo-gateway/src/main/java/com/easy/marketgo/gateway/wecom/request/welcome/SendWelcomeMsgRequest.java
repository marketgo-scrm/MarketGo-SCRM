package com.easy.marketgo.gateway.wecom.request.welcome;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:50
 * Describe:
 */
@Data
public class SendWelcomeMsgRequest {
    @JsonProperty("welcome_code")
    private String welcomeCode;
    private TextMessage text;
    private List<AttachmentsMessage> attachments;

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TextMessage {
        private String content;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttachmentsMessage {
        @JsonProperty("msgtype")
        private String msgType;
        private ImageAttachmentsMessage image;
        private LinkAttachmentsMessage link;
        private VideoAttachmentsMessage video;
        private FileAttachmentsMessage file;
        private MiniProgramAttachmentsMessage miniprogram;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageAttachmentsMessage {
        @JsonProperty("media_id")
        private String mediaId;
        @JsonProperty("pic_url")
        private String picUrl;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MiniProgramAttachmentsMessage {
        private String title;
        @JsonProperty("pic_media_id")
        private String picMediaId;
        private String appid;
        private String page;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkAttachmentsMessage {
        private String title;
        @JsonProperty("pic_url")
        private String picUrl;
        private String desc;
        private String url;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoAttachmentsMessage {
        @JsonProperty("media_id")
        private String mediaId;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileAttachmentsMessage {
        @JsonProperty("media_id")
        private String mediaId;
    }
}
