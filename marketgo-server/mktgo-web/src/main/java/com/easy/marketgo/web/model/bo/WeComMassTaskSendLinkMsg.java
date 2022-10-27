package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 4:09 PM
 * Describe:
 */
@ApiModel(description = "图文消息")
public class WeComMassTaskSendLinkMsg {
    private String mediaUuid;

    private String title;

    private String desc;

    private String url;

    private String imageContent;

    public WeComMassTaskSendLinkMsg uuid(String uuid) {
        this.mediaUuid = uuid;
        return this;
    }

    /**
     * 图文消息封面素材UUID
     *
     * @return mediaUuid
     **/
    @ApiModelProperty(value = "图文消息封面素材UUID")
    public String getMediaUuid() {
        return mediaUuid;
    }

    public void setMediaUuid(String mediaUuid) {
        this.mediaUuid = mediaUuid;
    }

    public WeComMassTaskSendLinkMsg title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 图文消息标题，群发最长128个字节，朋友圈最长64个字节
     *
     * @return title
     **/
    @ApiModelProperty(value = "图文消息标题，群发最长128个字节，朋友圈最长64个字节")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WeComMassTaskSendLinkMsg desc(String desc) {
        this.desc = desc;
        return this;
    }

    /**
     * 图文消息的描述，最多512个字节
     *
     * @return desc
     **/
    @ApiModelProperty(value = "图文消息的描述，最多512个字节")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public WeComMassTaskSendLinkMsg url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 图文消息的链接，最长2048个字节
     *
     * @return url
     **/
    @ApiModelProperty(value = "图文消息的链接，最长2048个字节")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WeComMassTaskSendLinkMsg thumbnailContent(String thumbnailContent) {
        this.imageContent = thumbnailContent;
        return this;
    }

    /**
     * h5链接封面缩略图base64
     *
     * @return imageContent
     **/
    @ApiModelProperty(value = "h5链接封面缩略图base64")
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
        WeComMassTaskSendLinkMsg scRMLinkMsg = (WeComMassTaskSendLinkMsg) o;
        return Objects.equals(this.mediaUuid, scRMLinkMsg.mediaUuid) &&
                Objects.equals(this.title, scRMLinkMsg.title) &&
                Objects.equals(this.desc, scRMLinkMsg.desc) &&
                Objects.equals(this.url, scRMLinkMsg.url) &&
                Objects.equals(this.imageContent, scRMLinkMsg.imageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaUuid, title, desc, url, imageContent);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TLinkMsg {\n");

        sb.append("    mediaUuid: ").append(toIndentedString(mediaUuid)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    desc: ").append(toIndentedString(desc)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
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
