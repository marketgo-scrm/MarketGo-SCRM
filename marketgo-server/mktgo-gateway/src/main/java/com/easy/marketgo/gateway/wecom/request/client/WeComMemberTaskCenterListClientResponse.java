package com.easy.marketgo.gateway.wecom.request.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/4/23 12:03 PM
 * Describe:
 */
@Data
@ApiModel(description = "员工任务中心的列表response")
public class WeComMemberTaskCenterListClientResponse {

    @ApiModelProperty(value = "员工任务总数")
    private Integer totalCount;
    @ApiModelProperty(value = "员工任务中心的列表详情")
    private List<MemberTaskCenterDetail> list;

    @Data
    public static class MemberTaskCenterDetail {
        @ApiModelProperty(value = "任务id")
        private Integer id;
        @ApiModelProperty(value = "员工任务的uuid")
        private String uuid;
        @ApiModelProperty(value = "企业ID")
        private String corpId;
        @ApiModelProperty(value = "员工ID")
        private String memberId;
        @ApiModelProperty(value = "任务的uuid")
        private String taskUuid;
        @ApiModelProperty(value = "任务名称")
        private String name;
        @ApiModelProperty(value = "任务类型")
        private String taskType;
        @ApiModelProperty(value = "任务的执行类型")
        private String scheduleType;
        @ApiModelProperty(value = "任务执行时间")
        private String planTime;
        @ApiModelProperty(value = "任务状态")
        private String taskStatus;
    }
}