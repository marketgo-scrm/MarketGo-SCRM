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
public class WeComMomentTaskCenterRequest implements Serializable {
    private String corpId;
    private String agentId;
    private String projectUuid;
    private String uuid;
    private String taskUuid;
    private String taskName;
    private List<String> tagList;
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
    public static class TextMessage implements Serializable {
        private String content;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttachmentsMessage implements Serializable {
        private String msgType;
        private ImageAttachmentsMessage image;
        private LinkAttachmentsMessage link;
        private VideoAttachmentsMessage video;
        private TextMessage text;
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
        private String mediaId;
        private String mediaUuid;
        private String imageContent;
        private String url;
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
}
