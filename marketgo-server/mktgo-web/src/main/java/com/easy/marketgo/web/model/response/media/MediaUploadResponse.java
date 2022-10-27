package com.easy.marketgo.web.model.response.media;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/19/22 10:37 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微上传素材response")
public class MediaUploadResponse {

    /**
     * 素材表ID
     */
    @ApiModelProperty(value = "素材的uuid")
    private String mediaUuid;

    /**
     * 缩略图：base64
     */
    @ApiModelProperty(value = "素材缩略图base64数据")
    private String imageContent;
}
