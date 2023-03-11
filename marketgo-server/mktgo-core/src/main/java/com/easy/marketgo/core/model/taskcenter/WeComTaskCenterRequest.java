package com.easy.marketgo.core.model.taskcenter;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:50
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeComTaskCenterRequest implements Serializable {

    private String corpId;
    private String agentId;
    private String projectUuid;
    private String chatType;
    private String MessageType;
    private String uuid;
    private String taskUuid;
    private String taskName;
    private List<ExternalUserMessage> externalUserId;
    private String sender;
    private List<AttachmentsMessage> attachments;
    private String targetType;
    private Integer targetTime;
    private String planTime;

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExternalUserMessage implements Serializable {
        private String name;
        private String externalUserId;
        private String status;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttachmentsMessage implements Serializable {
        private String type;
        private ImageAttachmentsMessage image;
        private LinkAttachmentsMessage link;
        private MiniProgramAttachmentsMessage miniProgram;
        private VideoAttachmentsMessage video;
        private FileAttachmentsMessage file;
        private TextMessage text;
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
        private String mediaId;
        private String mediaUuid;
        private String imageContent;
        private String picUrl;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkAttachmentsMessage implements Serializable {
        private String title;
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
        private String picMediaId;
        private String mediaUuid;
        private String imageContent;
        private String appId;
        private String page;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoAttachmentsMessage implements Serializable {
        private String mediaId;
        private String mediaUuid;
        private String imageContent;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileAttachmentsMessage implements Serializable {
        private String mediaId;
        private String mediaUuid;
        private String imageContent;
    }
}
