package com.easy.marketgo.web.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:59 PM
 * Describe:
 */
@ApiModel(description = "文本消息")
public class WeComMassTaskSendTextMsg {
    private String content = null;

    public WeComMassTaskSendTextMsg content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 文本消息内容，最多4000个字节
     *
     * @return content
     **/
    @ApiModelProperty(value = "文本消息内容，最多4000个字节")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WeComMassTaskSendTextMsg scRMTextMsg = (WeComMassTaskSendTextMsg) o;
        return Objects.equals(this.content, scRMTextMsg.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TTextMsg {\n");

        sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
