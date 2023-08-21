package com.easy.marketgo.web.model.response.welcomemsg;

import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/23/23 10:54 PM
 * Describe:
 */
@Data
@ApiModel(description = "客户群欢迎语列表response")
public class WelcomeMsgGroupChatListResponse {
    @ApiModelProperty(value = "任务的数量")
    private Integer totalCount;
    @ApiModelProperty(value = "任务的列表详情")
    private List<WelcomeMsgDetail> list;

    @Data
    public static class WelcomeMsgDetail {
        @ApiModelProperty(value = "欢迎语的id")
        private Integer id;
        @ApiModelProperty(value = "欢迎语的uuid")
        private String uuid;
        @ApiModelProperty(value = "欢迎语的名称")
        private String name;

        @ApiModelProperty(value = "创建人名称")
        private String creatorName;
        @ApiModelProperty(value = "创建人id")
        private String creatorId;
        @ApiModelProperty(name = "创建时间", notes = "创建时间")
        private String createTime;

        @ApiModelProperty(name = "欢迎语列表明细 ")
        private List<WeComMassTaskSendMsg> welcomeContent;
    }
}
