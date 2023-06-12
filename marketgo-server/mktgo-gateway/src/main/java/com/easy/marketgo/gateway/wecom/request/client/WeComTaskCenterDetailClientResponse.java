package com.easy.marketgo.gateway.wecom.request.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 12:03 PM
 * Describe:
 */
@Data
@ApiModel(description = "任务中心的详情response")
public class WeComTaskCenterDetailClientResponse {

    @ApiModelProperty(value = "企业ID")
    private String corpId;
    @ApiModelProperty(value = "应用ID")
    private String agentId;
    @ApiModelProperty(value = "任务类型：SIGNLE 客户， GROUP 客户群， MOMENT 朋友圈")
    private String chatType;
    @ApiModelProperty(value = "发送的内容类型。【SEND_MESSAGE】发送内容 【ASSIGN_TASK】指派任务 ")
    private String MessageType;
    @ApiModelProperty(value = "任务的uuid")
    private String taskUuid;
    @ApiModelProperty(value = "任务的名称")
    private String taskName;
    @ApiModelProperty(value = "发送的内容list")
    private List<AttachmentsMessage> attachments;
    @ApiModelProperty(value = "任务组list")
    private List<WeComTaskCenterMessage> taskList;

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WeComTaskCenterMessage {
        @ApiModelProperty(value = "任务组uuid")
        private String uuid;
        @ApiModelProperty(value = "客户列表")
        private List<ExternalUserMessage> externalUserId;
        @ApiModelProperty(value = "目标类型")
        private String targetType;
        @ApiModelProperty(value = "目标时间")
        private Integer targetTime;
        @ApiModelProperty(value = "计划执行时间")
        private String planTime;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExternalUserMessage {
        @ApiModelProperty(value = "客户头像")
        private String avatar;
        @ApiModelProperty(value = "客户名称")
        private String name;
        @ApiModelProperty(value = "客户ID")
        private String externalUserId;
        @ApiModelProperty(value = "客户发送状态， UNDELIVERED 未送达客户; DELIVERED 已送达客户")
        private String status;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttachmentsMessage {
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
    public static class TextMessage {
        private String content;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageAttachmentsMessage {
        private String mediaId;
        private String imageContent;
        private String picUrl;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LinkAttachmentsMessage {
        private String title;
        private String picUrl;
        private String url;
        private String desc;
        private String mediaId;
        private String imageContent;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MiniProgramAttachmentsMessage {
        private String title;
        private String picMediaId;
        private String imageContent;
        private String appId;
        private String page;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoAttachmentsMessage {
        private String mediaId;
        private String imageContent;
    }

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileAttachmentsMessage {
        private String mediaId;
        private String imageContent;
    }
}