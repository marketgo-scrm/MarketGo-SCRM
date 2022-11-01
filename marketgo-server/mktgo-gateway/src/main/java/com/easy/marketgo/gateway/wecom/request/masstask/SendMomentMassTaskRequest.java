package com.easy.marketgo.gateway.wecom.request.masstask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 16:20
 * Describe:
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendMomentMassTaskRequest {
    @JsonProperty("visible_range")
    private VisibleRangeMessage visibleRange;
    private TextMessage text;
    private List<AttachmentsMessage> attachments;


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VisibleRangeMessage {
        @JsonProperty("sender_list")
        private SenderListMessage senderList;
        @JsonProperty("external_contact_list")
        private ExternalContactListMessage externalContactList;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SenderListMessage {
        @JsonProperty("user_list")
        private List<String> userList;
        @JsonProperty("department_list")
        private List<Integer> departmentList;
    }


    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExternalContactListMessage {
        @JsonProperty("tag_list")
        private List<String> tagList;
    }


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
        @JsonProperty("media_id")
        private String mediaId;
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
}
