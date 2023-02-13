package com.easy.marketgo.web.model.response.contactway;

import com.easy.marketgo.common.enums.WeComContactWayWelcomeType;
import com.easy.marketgo.web.model.bo.WeComCorpTag;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import com.easy.marketgo.web.model.request.contactway.ChannelLiveCodeCreateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-23 13:50:29
 * @description : ChannelContactWayResponse.java
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "活码详情")
public class ChannelLiveCodeDetailResponse {

    @ApiModelProperty(name = "自增id", notes = "自增id")
    private int id;
    @ApiModelProperty(name = "创建活码的uuid", notes = "创建活码的uuid")
    private String uuid;
    @ApiModelProperty(name = "二维码", notes = "二维码")
    private String qrCode;
    @ApiModelProperty(name = "名称", notes = "名称")
    private String name;

    @ApiModelProperty(name = "添加客户数", notes = "添加客户数")
    private Integer addExtCount;
    @ApiModelProperty(name = "客户标签", notes = "客户标签")
    private List<WeComCorpTag> tags;
    @ApiModelProperty(name = "员工列表", notes = "员工列表")
    private ChannelLiveCodeCreateRequest.MembersMessage members;
    @ApiModelProperty(name = "备选员工列表", notes = "备选员工列表")
    private ChannelLiveCodeCreateRequest.MembersMessage backupMembers;
    @ApiModelProperty(name = "在线类型", notes = "在线类型")
    private Integer onlineType;
    @ApiModelProperty(name = "添加客户上限", notes = "添加客户上限")
    private Boolean addExtUserLimitStatus;
    @ApiModelProperty(name = "addExtUserLimit", notes = "员工添加外部联系人的上限明细")
    private List<ChannelLiveCodeCreateRequest.MemberInfo> addExtUserLimit;
    @ApiModelProperty(name = "自动通过好友", notes = "自动通过好友")
    private Boolean skipVerify;
    @ApiModelProperty(name = "欢迎语的类型", notes = "欢迎语的类型")
    private WeComContactWayWelcomeType welcomeType;
    @ApiModelProperty(name = "二维码中间头像", notes = "二维码logoFileId")
    private LogoMessage logoMedia;

    @Data
    public static class LogoMessage {
        @ApiModelProperty(value = "logo的uuid")
        private String mediaUuid = null;
        @ApiModelProperty(value = "logo的缩略图")
        private String imageContent = null;
    }

    private String createTime;

    @ApiModelProperty(name = " welcomeAttachments", notes = "欢迎语列表明细 ")
    private List<WeComMassTaskSendMsg> welcomeContent;

    @Data
    @ApiModel(description = "人员明细")
    public static class MemberInfo {
        @ApiModelProperty(name = "memberId", notes = "员工id")
        private String memberId;
        @ApiModelProperty(name = "memberName", notes = "员工名称")
        private String memberName;
        @ApiModelProperty(name = "inUseStatus", notes = "在线状态，添加超过上线后或大于 微信默认上线（20000）后下线")
        private String inUseStatus;
        @ApiModelProperty(name = "thumbAvatar", notes = "头像url")
        private String thumbAvatar;

    }
}
