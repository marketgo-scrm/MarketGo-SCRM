package com.easy.marketgo.api.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 10:48 PM
 * Describe:
 */
@Data
public class WeComUploadMediaClientResponse implements Serializable {
    private String type;
    /**
     * 临时素材表示mediaId，永久素材表示url
     */
    private String mediaId;
    /**
     * 临时素材上传时间
     */
    private Long createdAt;

    private String url;
}
