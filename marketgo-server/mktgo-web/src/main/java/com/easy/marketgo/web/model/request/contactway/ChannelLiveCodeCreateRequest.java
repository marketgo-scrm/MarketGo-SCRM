package com.easy.marketgo.web.model.request.contactway;

import com.easy.marketgo.common.enums.WeComContactWayWelcomeType;
import com.easy.marketgo.web.model.bo.WeComCorpTag;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-23 13:46:30
 * @description : ChannelContactWayRequest.java
 */
@Data
@ApiModel(description = "渠道活码创建请求")
public class ChannelLiveCodeCreateRequest {

    @ApiModelProperty(value = "活码id")
    private Integer id;
    @ApiModelProperty(value = "活码uuid")
    private String uuid;
    @ApiModelProperty(name = "name", notes = "二维码名称", example = "测试活码")
    private String name;

    @ApiModelProperty(name = "onlineStatus", notes = "员工在线状态：1 全天在线", example = "")
    private Integer onlineType;

    @ApiModelProperty(value = "员工的人群条件详情")
    private MembersMessage members;

    @ApiModelProperty(name = "addExtUserLimitStatus", notes = "添加员工上限的状态开关" +
            " /**\n" +
            "    enable(\"1\"),\n" +
            "    disable(\"0\"),", example = "")
    private Boolean addExtUserLimitStatus;

    @ApiModelProperty(name = "addExtUserLimit", notes = "员工添加外部联系人的上限明细", example = "")
    private List<MemberInfo> addExtUserLimit;



    @ApiModelProperty(name = "userMembers", notes = "备用员工", example = "")
    private MembersMessage backupMembers;

    @ApiModelProperty(name = "tags", notes = "客户标签")
    private List<WeComCorpTag> tags;
    @ApiModelProperty(name = "skipVerify", notes = "是否开启验证")
    private Boolean skipVerify;
    @ApiModelProperty(name = "welcomeType", notes = "开启的欢迎语类型：    ALL(-1,\"全部\"),\n" +
            "    CLOSED(0,\"不开启欢迎语\"),\n" +
            "    CHANNEL(1,\"渠道欢迎语\"),\n" +
            "    USER(2,\"员工欢迎语\");\n")
    private WeComContactWayWelcomeType welcomeType;

    @ApiModelProperty(name = " welcomeAttachments", notes = "欢迎语列表明细 ")
    private List<WeComMassTaskSendMsg> welcomeContent;
    @ApiModelProperty(name = "二维码中间头像", notes = "二维码logoFileId")
    private LogoMessage logoMedia;


    @Data
    public static class LogoMessage {
        @ApiModelProperty(value = "logo的uuid")
        private String mediaUuid = null;
        @ApiModelProperty(value = "logo的缩略图")
        private String imageContent = null;
    }

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


    @Data
    @ApiModel(description = "人员明细")
    public static class MemberInfo {

        @ApiModelProperty(name = "memberId", notes = "员工id")
        private String memberId;
        @ApiModelProperty(name = "thumbAvatar", notes = "头像url")
        private String thumbAvatar;
        @ApiModelProperty(name = "memberName", notes = "员工名称")
        private String memberName;
        @ApiModelProperty(name = "inUseStatus", notes = "在线状态，添加超过上线后或大于 微信默认上线（20000）后下线")
        private String inUseStatus;
        @ApiModelProperty(name = "addExtUserCeiling", notes = "添加客户上限", example = "100")
        private Integer addExtUserCeiling;
    }
}
