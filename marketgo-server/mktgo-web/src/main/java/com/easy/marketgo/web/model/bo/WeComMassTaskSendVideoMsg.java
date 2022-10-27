package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 4:03 PM
 * Describe:
 */
public class WeComMassTaskSendVideoMsg {
    private String mediaUuid = null;

    private String title = null;

    private String imageContent = null;

    public WeComMassTaskSendVideoMsg uuid(String uuid) {
        this.mediaUuid = uuid;
        return this;
    }

    /**
     * 视频素材UUID
     *
     * @return mediaUuid
     **/
    @ApiModelProperty(value = "视频素材UUID")
    public String getMediaUuid() {
        return mediaUuid;
    }

    public void setMediaUuid(String mediaUuid) {
        this.mediaUuid = mediaUuid;
    }

    public WeComMassTaskSendVideoMsg title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 视频素材标题
     *
     * @return title
     **/
    @ApiModelProperty(value = "视频素材标题")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WeComMassTaskSendVideoMsg thumbnailContent(String thumbnailContent) {
        this.imageContent = thumbnailContent;
        return this;
    }

    /**
     * 视频封面缩略图base64
     *
     * @return imageContent
     **/
    @ApiModelProperty(value = "视频封面缩略图base64")
    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeComMassTaskSendVideoMsg scRMVideoMsg = (WeComMassTaskSendVideoMsg) o;
        return Objects.equals(this.mediaUuid, scRMVideoMsg.mediaUuid) &&
                Objects.equals(this.title, scRMVideoMsg.title) &&
                Objects.equals(this.imageContent, scRMVideoMsg.imageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaUuid, title, imageContent);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TVideoMsg {\n");

        sb.append("    mediaUuid: ").append(toIndentedString(mediaUuid)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    imageContent: ").append(toIndentedString(imageContent)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
