package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 4:00 PM
 * Describe:
 */
public class WeComMassTaskSendImageMsg {
    private String mediaUuid = null;

    private String imageContent = null;

    public WeComMassTaskSendImageMsg uuid(String uuid) {
        this.mediaUuid = uuid;
        return this;
    }

    /**
     * 图片素材UUID
     * @return mediaUuid
     **/
    @ApiModelProperty(value = "图片素材UUID")
    public String getMediaUuid() {
        return mediaUuid;
    }

    public void setUuid(String uuid) {
        this.mediaUuid = uuid;
    }

    public WeComMassTaskSendImageMsg imageContent(String thumbnailContent) {
        this.imageContent = thumbnailContent;
        return this;
    }

    /**
     * 图片缩略图base64
     * @return imageContent
     **/
    @ApiModelProperty(value = "图片缩略图base64")
    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String thumbnailContent) {
        this.imageContent = thumbnailContent;
    }

}
