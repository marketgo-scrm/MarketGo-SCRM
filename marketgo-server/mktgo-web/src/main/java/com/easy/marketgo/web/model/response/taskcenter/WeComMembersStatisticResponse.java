package com.easy.marketgo.web.model.response.taskcenter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 11:17 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微群发员工统计列表的response")
public class WeComMembersStatisticResponse {
    @ApiModelProperty(value = "员工的总数量")
    private Integer count = null;
    @ApiModelProperty(value = "员工的状态")
    private String status;
    @ApiModelProperty(value = "员工的列表")
    private List<MemberDetail> members;

    @ApiModelProperty(value = "评论的列表")
    private List<DayDetail> dayDetails;

    @ApiModelProperty(value = "是否可以发送提醒")
    private Boolean canRemind;

    @Data
    public static class MemberDetail {
        @ApiModelProperty(value = "员工id")
        private String memberId = null;
        @ApiModelProperty(value = "员工名称")
        private String memberName = null;
        @ApiModelProperty(value = "员工的头像")
        private String avatar;
        @ApiModelProperty(value = "员工的客户数量")
        private Integer externalUserCount = null;
    }

    @Data
    public static class DayDetail {
        @ApiModelProperty(value = "客户数量")
        private Integer externalUserCount = null;
        @ApiModelProperty(value = "员工数量")
        private Integer memberCount = null;
        @ApiModelProperty(value = "任务完成率")
        private String completeRate = null;
        @ApiModelProperty(value = "状态")
        private String status;
        @ApiModelProperty(value = "计划时间")
        private String planTime = null;
    }
}
