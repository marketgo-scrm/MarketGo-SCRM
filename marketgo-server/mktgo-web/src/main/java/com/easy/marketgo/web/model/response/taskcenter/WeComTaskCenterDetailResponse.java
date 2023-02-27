package com.easy.marketgo.web.model.response.taskcenter;

import com.easy.marketgo.common.enums.taskcenter.WeComTaskCenterRepeatType;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 11:28 PM
 * Describe:
 */

@Data
@ApiModel(description = "企微员工任务详情的response")
public class WeComTaskCenterDetailResponse {
    @ApiModelProperty(value = "任务id")
    private Integer id = null;
    @ApiModelProperty(value = "任务uuid")
    private String uuid = null;

    @ApiModelProperty(value = "企微CORP ID")
    private String corpId;

    @ApiModelProperty(value = "企微Agent ID")
    private String agentId;

    @ApiModelProperty(value = "任务名称")
    private String name;


    @ApiModelProperty(value = "创建人ID")
    private String creatorId;

    @ApiModelProperty(value = "创建人姓名")
    private String creatorName;

    @ApiModelProperty(value = "发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME")
    private String scheduleType;

    @ApiModelProperty(value = "计划发送时间")
    private String scheduleTime;

    @ApiModelProperty(value = "计划开始时间")
    private String repeatStartTime;

    @ApiModelProperty(value = "计划结束时间")
    private String repeatEndTime;

    @ApiModelProperty(value = "周期活动的类型，每天 DAILY/每周 WEEKLY/每月 MONTHLY", required = true, allowableValues = "DAILY, " +
            "WEEKLY,MONTHLY")
    private WeComTaskCenterRepeatType repeatType;
    @ApiModelProperty(value = "周期活动执行的日期，每天 0/每周 1-7/每月 1-31", required = true)
    private String repeatDay;

    @ApiModelProperty(value = "计划创建时间")
    private String createTime;

    @ApiModelProperty(value = "关联人群预估UUID")
    private String userGroupUuid;

    @ApiModelProperty(value = "推送消息内容")
    private List<WeComMassTaskSendMsg> msgContent = null;

    @ApiModelProperty(value = "任务状态:未开始 UNSTART; 人群计算中 COMPUTING; 计算完成 COMPUTED; 计算失败 COMPUTE_FAILED; 进行中 SENDING; " +
            "已结束 FINISHED; 执行失败 FAILED',")
    private String taskStatus;

    @ApiModelProperty(value = "消息类型：【SEND_MESSAGE】发送内容 【ASSIGN_TASK】指派任务 ")
    private String messageType;
    @ApiModelProperty(value = "目标类型:天 DAY/小时 HOUR/分钟 MINUTE")
    private String targetType;
    @ApiModelProperty(value = "目标的时间设置")
    private Integer targetTime;
}
