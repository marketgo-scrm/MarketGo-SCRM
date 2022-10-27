package com.easy.marketgo.api.model.request.contactway;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
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
public class WeComContactWayClientRequest extends BaseRpcRequest {
    private String configId;
    private Integer type;
    private Integer scene;
    private Integer style;
    private String remark;
    private Boolean skipVerify;
    private String state;
    private List<String> user;
    private List<Integer> party;
    private Boolean isTemp;
    private Long expiresIn;
    private Long chatExpiresIn;
    private String unionId;
    private ConclusionsMessage conclusions;

    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConclusionsMessage implements Serializable{
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
        private String mediaId;
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

}
