package com.easy.marketgo.api.model.request.masstask;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
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
public class WeComMassTaskClientRequest extends BaseRpcRequest {
    private String projectUuid;
    private String chatType;
    private String taskUuid;
    private List<String> externalUserId;
    private String sender;
    private TextMessage text;
    private List<AttachmentsMessage> attachments;



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
        private MiniProgramAttachmentsMessage miniProgram;
        private VideoAttachmentsMessage video;
        private FileAttachmentsMessage file;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageAttachmentsMessage implements Serializable {
        private String mediaId;
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
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileAttachmentsMessage implements Serializable {
        private String mediaId;
    }
}
