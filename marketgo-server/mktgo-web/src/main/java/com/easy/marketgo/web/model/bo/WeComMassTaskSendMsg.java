package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:55 PM
 * Describe:
 */
@ApiModel(description = "群发内容的详情")
public class WeComMassTaskSendMsg {
    /**
     * 推送内容类型:TEXT/IMAGE/VOICE/VIDEO/FILE/MINIPROGRAM/LINK
     */
    public enum TypeEnum {
        TEXT("TEXT"),

        IMAGE("IMAGE"),

        VOICE("VOICE"),

        VIDEO("VIDEO"),

        FILE("FILE"),

        MINIPROGRAM("MINIPROGRAM"),

        LINK("LINK");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private TypeEnum type;

    private WeComMassTaskSendTextMsg text;

    private WeComMassTaskSendImageMsg image;

    private WeComMassTaskSendVideoMsg video;

    private WeComMassTaskSendFileMsg file;

    private WeComMassTaskSendMiniProgramMsg miniProgram;

    private WeComMassTaskSendLinkMsg link;

    public WeComMassTaskSendMsg type(TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * 推送内容类型:TEXT/IMAGE/VOICE/VIDEO/FILE/MINIPROGRAM/LINK
     *
     * @return type
     **/
    @ApiModelProperty(required = true, value = "推送内容类型:TEXT/IMAGE/VOICE/VIDEO/FILE/MINIPROGRAM/LINK")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public WeComMassTaskSendMsg text(WeComMassTaskSendTextMsg text) {
        this.text = text;
        return this;
    }

    /**
     * 文本消息
     *
     * @return text
     **/
    @ApiModelProperty(value = "文本消息", required = true)
    public WeComMassTaskSendTextMsg getText() {
        return text;
    }

    public void setText(WeComMassTaskSendTextMsg text) {
        this.text = text;
    }

    public WeComMassTaskSendMsg image(WeComMassTaskSendImageMsg image) {
        this.image = image;
        return this;
    }

    /**
     * 图片消息
     *
     * @return image
     **/
    @ApiModelProperty(value = "图片消息")
    public WeComMassTaskSendImageMsg getImage() {
        return image;
    }

    public void setImage(WeComMassTaskSendImageMsg image) {
        this.image = image;
    }

    public WeComMassTaskSendMsg video(WeComMassTaskSendVideoMsg video) {
        this.video = video;
        return this;
    }

    /**
     * 视频消息
     *
     * @return video
     **/
    @ApiModelProperty(value = "视频消息")
    public WeComMassTaskSendVideoMsg getVideo() {
        return video;
    }

    public void setVideo(WeComMassTaskSendVideoMsg video) {
        this.video = video;
    }

    public WeComMassTaskSendMsg file(WeComMassTaskSendFileMsg file) {
        this.file = file;
        return this;
    }

    /**
     * 文件消息
     *
     * @return file
     **/
    @ApiModelProperty(value = "文件消息")
    public WeComMassTaskSendFileMsg getFile() {
        return file;
    }

    public void setFile(WeComMassTaskSendFileMsg file) {
        this.file = file;
    }

    public WeComMassTaskSendMsg miniProgram(WeComMassTaskSendMiniProgramMsg miniProgram) {
        this.miniProgram = miniProgram;
        return this;
    }

    /**
     * 小程序消息
     *
     * @return miniprogram
     **/
    @ApiModelProperty(value = "小程序消息")
    public WeComMassTaskSendMiniProgramMsg getMiniProgram() {
        return miniProgram;
    }

    public void setMiniprogram(WeComMassTaskSendMiniProgramMsg miniProgram) {
        this.miniProgram = miniProgram;
    }

    public WeComMassTaskSendMsg link(WeComMassTaskSendLinkMsg link) {
        this.link = link;
        return this;
    }

    /**
     * 图文消息
     *
     * @return link
     **/
    @ApiModelProperty(value = "图文消息")
    public WeComMassTaskSendLinkMsg getLink() {
        return link;
    }

    public void setLink(WeComMassTaskSendLinkMsg link) {
        this.link = link;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeComMassTaskSendMsg scRMMsgContent = (WeComMassTaskSendMsg) o;
        return Objects.equals(this.type, scRMMsgContent.type) &&
                Objects.equals(this.text, scRMMsgContent.text) &&
                Objects.equals(this.image, scRMMsgContent.image) &&
                Objects.equals(this.video, scRMMsgContent.video) &&
                Objects.equals(this.file, scRMMsgContent.file) &&
                Objects.equals(this.miniProgram, scRMMsgContent.miniProgram) &&
                Objects.equals(this.link, scRMMsgContent.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, text, image, video, file, miniProgram, link);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TMsgContent {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    text: ").append(toIndentedString(text)).append("\n");
        sb.append("    image: ").append(toIndentedString(image)).append("\n");
        sb.append("    video: ").append(toIndentedString(video)).append("\n");
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
        sb.append("    miniprogram: ").append(toIndentedString(miniProgram)).append("\n");
        sb.append("    link: ").append(toIndentedString(link)).append("\n");
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
