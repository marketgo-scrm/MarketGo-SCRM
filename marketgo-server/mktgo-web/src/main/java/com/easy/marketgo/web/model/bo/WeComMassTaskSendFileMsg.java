package com.easy.marketgo.web.model.bo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 4:05 PM
 * Describe:
 */
@ApiModel(description = "文件消息")
public class WeComMassTaskSendFileMsg {
    private String mediaUuid = null;

    private String title = null;

    private String fileName = null;

    /**
     * 文件类型
     */
    public enum TypeEnum {
        WORD("WORD"),

        EXCEL("EXCEL"),

        PPT("PPT"),

        PDF("PDF");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("type")
    private TypeEnum type = null;

    @JsonProperty("size")
    private Long size = null;

    public WeComMassTaskSendFileMsg uuid(String uuid) {
        this.mediaUuid = uuid;
        return this;
    }

    /**
     * 文件素材UUID
     * @return mediaUuid
     **/
    @ApiModelProperty(value = "文件素材UUID")
    public String getMediaUuid() {
        return mediaUuid;
    }

    public void setMediaUuid(String mediaUuid) {
        this.mediaUuid = mediaUuid;
    }

    public WeComMassTaskSendFileMsg title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 文件素材标题
     * @return title
     **/
    @ApiModelProperty(value = "文件素材标题")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WeComMassTaskSendFileMsg fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 文件名称
     * @return filename
     **/
    @ApiModelProperty(value = "文件名称")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public WeComMassTaskSendFileMsg type(TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * 文件类型
     * @return type
     **/
    @ApiModelProperty(value = "文件类型")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public WeComMassTaskSendFileMsg size(Long size) {
        this.size = size;
        return this;
    }

    /**
     * 文件大小
     * @return size
     **/
    @ApiModelProperty(value = "文件大小")
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeComMassTaskSendFileMsg scRMFileMsg = (WeComMassTaskSendFileMsg) o;
        return Objects.equals(this.mediaUuid, scRMFileMsg.mediaUuid) &&
                Objects.equals(this.title, scRMFileMsg.title) &&
                Objects.equals(this.fileName, scRMFileMsg.fileName) &&
                Objects.equals(this.type, scRMFileMsg.type) &&
                Objects.equals(this.size, scRMFileMsg.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaUuid, title, fileName, type, size);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TFileMsg {\n");

        sb.append("    mediaUuid: ").append(toIndentedString(mediaUuid)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    filename: ").append(toIndentedString(fileName)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    size: ").append(toIndentedString(size)).append("\n");
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
