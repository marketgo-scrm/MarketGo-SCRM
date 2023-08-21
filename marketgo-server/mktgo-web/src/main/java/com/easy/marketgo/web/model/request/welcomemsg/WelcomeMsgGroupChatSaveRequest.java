package com.easy.marketgo.web.model.request.welcomemsg;

import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */

@Data
@ApiModel(description = "客户群欢迎语创建请求")
public class WelcomeMsgGroupChatSaveRequest {

    @ApiModelProperty(value = "客户欢迎语id")
    private Integer id;
    @ApiModelProperty(value = "客户欢迎语uuid")
    private String uuid;
    @ApiModelProperty(value = "标题")
    private String name;
    @ApiModelProperty(value = "创建人名称")
    private String creatorName;
    @ApiModelProperty(value = "创建人id")
    private String creatorId;

    @ApiModelProperty(value = "通知类型  1 全部；  2 部分  0 不通知")
    private Integer notifyType;

    @ApiModelProperty(value = "员工的人群条件详情")
    private CustomerWelcomeMsgSaveRequest.MembersMessage members;


    @ApiModelProperty(name = "欢迎语列表明细 ")
    private List<WeComMassTaskSendMsg> welcomeContent;
}
