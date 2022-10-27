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
public class WeComMomentMassTaskClientRequest extends BaseRpcRequest {
    private String projectUuid;
    private String taskUuid;
    private VisibleRangeMessage visibleRange;
    private TextMessage text;
    private List<AttachmentsMessage> attachments;


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VisibleRangeMessage implements Serializable {
        private SenderListMessage senderList;
        private ExternalContactListMessage externalContactList;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SenderListMessage implements Serializable {
        private List<String> userList;
        private List<Integer> departmentList;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExternalContactListMessage implements Serializable {
        private List<String> tagList;
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
    public static class AttachmentsMessage implements Serializable {
        private String msgType;
        private ImageAttachmentsMessage image;
        private LinkAttachmentsMessage link;
        private VideoAttachmentsMessage video;
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
        private String mediaId;
        private String url;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoAttachmentsMessage implements Serializable {
        private String mediaId;
    }
}
