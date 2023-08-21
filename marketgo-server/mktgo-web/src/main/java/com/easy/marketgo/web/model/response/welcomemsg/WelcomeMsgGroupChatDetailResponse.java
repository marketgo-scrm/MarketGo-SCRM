package com.easy.marketgo.web.model.response.welcomemsg;

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
@ApiModel(description = "客户群欢迎语详情response")
public class WelcomeMsgGroupChatDetailResponse {

    @ApiModelProperty(value = "客户群欢迎语id")
    private Integer id;
    @ApiModelProperty(value = "客户群欢迎语uuid")
    private String uuid;
    @ApiModelProperty(value = "标题")
    private String name;
    @ApiModelProperty(value = "创建人名称")
    private String creatorName;
    @ApiModelProperty(value = "创建人id")
    private String creatorId;
    @ApiModelProperty(name = "创建时间", notes = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "群主的人群条件详情")
    private MembersMessage members;

    @ApiModelProperty(value = "通知类型  1 全部；  2 部分  0 不通知")
    private Integer notifyType;

    @ApiModelProperty(name = "欢迎语列表明细 ")
    private List<WeComMassTaskSendMsg> welcomeContent;

    @Data
    public static class MembersMessage {
        @ApiModelProperty(value = "部门的列表")
        private List<DepartmentMessage> departments;
        @ApiModelProperty(value = "员工列表")
        private List<UserMessage> users;
    }

    @Data
    public static class UserMessage {
        @ApiModelProperty(value = "员工id")
        private String memberId;
        @ApiModelProperty(name = "thumbAvatar", notes = "头像url")
        private String thumbAvatar;
        @ApiModelProperty(value = "员工名称")
        private String memberName;
    }

    @Data
    public static class DepartmentMessage {
        @ApiModelProperty(value = "部门id")
        private Long id;
        @ApiModelProperty(value = "部门名称")
        private String name;
    }
}
