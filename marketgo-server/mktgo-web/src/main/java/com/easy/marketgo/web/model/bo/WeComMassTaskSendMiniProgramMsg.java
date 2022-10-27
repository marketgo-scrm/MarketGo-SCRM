package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 4:06 PM
 * Describe:
 */
@ApiModel(description = "小程序消息")
public class WeComMassTaskSendMiniProgramMsg {
    private String mediaUuid = null;

    private String appId = null;

    private String page = null;

    private String title = null;

    private String imageContent = null;

    public WeComMassTaskSendMiniProgramMsg uuid(String uuid) {
        this.mediaUuid = uuid;
        return this;
    }

    /**
     * 小程序封面素材UUID
     *
     * @return mediaUuid
     **/
    @ApiModelProperty(value = "小程序封面素材UUID")
    public String getMediaUuid() {
        return mediaUuid;
    }

    public void setMediaUuid(String mediaUuid) {
        this.mediaUuid = mediaUuid;
    }

    public WeComMassTaskSendMiniProgramMsg appId(String appId) {
        this.appId = appId;
        return this;
    }

    /**
     * 小程序appid（可以在微信公众平台上查询），必须是关联到企业的小程序应用
     *
     * @return appId
     **/
    @ApiModelProperty(value = "小程序appid（可以在微信公众平台上查询），必须是关联到企业的小程序应用")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public WeComMassTaskSendMiniProgramMsg page(String page) {
        this.page = page;
        return this;
    }

    /**
     * 小程序page路径
     *
     * @return queryListByPage
     **/
    @ApiModelProperty(value = "小程序page路径")
    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public WeComMassTaskSendMiniProgramMsg title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 小程序消息标题，最多64个字节
     *
     * @return title
     **/
    @ApiModelProperty(value = "小程序消息标题，最多64个字节")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WeComMassTaskSendMiniProgramMsg thumbnailContent(String thumbnailContent) {
        this.imageContent = thumbnailContent;
        return this;
    }

    /**
     * 小程序封面缩略图base64
     *
     * @return imageContent
     **/
    @ApiModelProperty(value = "小程序封面缩略图base64")
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
        WeComMassTaskSendMiniProgramMsg scRMMiniProgramMsg = (WeComMassTaskSendMiniProgramMsg) o;
        return Objects.equals(this.mediaUuid, scRMMiniProgramMsg.mediaUuid) &&
                Objects.equals(this.appId, scRMMiniProgramMsg.appId) &&
                Objects.equals(this.page, scRMMiniProgramMsg.page) &&
                Objects.equals(this.title, scRMMiniProgramMsg.title) &&
                Objects.equals(this.imageContent, scRMMiniProgramMsg.imageContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaUuid, appId, page, title, imageContent);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TMiniProgramMsg {\n");

        sb.append("    mediaUuid: ").append(toIndentedString(mediaUuid)).append("\n");
        sb.append("    appId: ").append(toIndentedString(appId)).append("\n");
        sb.append("    queryListByPage: ").append(toIndentedString(page)).append("\n");
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
