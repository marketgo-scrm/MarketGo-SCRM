package com.easy.marketgo.web.model.response.contactway;

import com.easy.marketgo.web.model.bo.WeComCorpTag;
import com.easy.marketgo.web.model.request.contactway.ChannelLiveCodeCreateRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
@ApiModel(description = "活码列表响应")
public class ChannelLiveCodeResponse {

    @ApiModelProperty(name = "queryListByPage", notes = "分页index", example = "0")
    private int page = 0;
    @ApiModelProperty(name = "pageSize", notes = "分页大小", example = "20")
    private int pageSize = 20;
    @ApiModelProperty(name = "total", notes = "总记录数", example = "2000")
    private long total;
    @ApiModelProperty(name = "infos", notes = "明细")
    private List<ChannelContactWayInfo> infos;
    @ApiModelProperty(name = "createTime", notes = "创建时间")
    private LocalDateTime createTime;

    @Data
    @ApiModel(description = "二维码列表明细")
    public static class ChannelContactWayInfo {

        @ApiModelProperty(name = "自增id", notes = "自增id")
        private int id;
        @ApiModelProperty(name = "创建活码的uuid", notes = "创建活码的uuid")
        private String uuid;
        @ApiModelProperty(name = "二维码", notes = "二维码")
        private String qrCode;
        @ApiModelProperty(name = "是否上传logo", notes = "是否上传logo")
        private Boolean isUploadLogo;
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
        @ApiModelProperty(name = "创建时间", notes = "创建时间")
        private String  createTime;
    }

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
