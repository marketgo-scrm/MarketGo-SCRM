package com.easy.marketgo.gateway.wecom.request.masstask;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:19
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMassTaskRequest {
    @JsonProperty("chat_type")
    private String chatType;
    @JsonProperty("external_userid")
    private List<String> externalUserid;
    private String sender;
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
        private MiniprogramAttachmentsMessage miniprogram;
        private VideoAttachmentsMessage video;
        private FileAttachmentsMessage file;
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
    public static class LinkAttachmentsMessage {
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
    public static class MiniprogramAttachmentsMessage {
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
