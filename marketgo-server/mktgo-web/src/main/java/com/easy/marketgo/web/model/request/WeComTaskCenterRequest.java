package com.easy.marketgo.web.model.request;

import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.taskcenter.WeComTaskCenterRepeatType;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/19/22 6:49 PM
 * Describe:
 */
@Data
@ApiModel(description = "任务中心创建和更新request")
public class WeComTaskCenterRequest {
    @ApiModelProperty(value = "任务id")
    private Integer id;
    @ApiModelProperty(value = "任务uuid")
    private String uuid;
    @ApiModelProperty(value = "任务名称", required = true)
    private String name;
    @ApiModelProperty(value = "创建人名称", required = true)
    private String creatorName;
    @ApiModelProperty(value = "创建人id", required = true)
    private String creatorId;
    @ApiModelProperty(value = "任务类型， 立即发送 IMMEDIATE/定时发送 FIXED_TIME/周期发送 REPEAT_TIME", required = true, allowableValues = "IMMEDIATE, " +
            "FIXED_TIME,REPEAT_TIME")
    private WeComMassTaskScheduleType scheduleType;

    @ApiModelProperty(value = "周期活动的类型，每天 DAILY/每周 WEEKLY/每月 MONTHLY", required = true, allowableValues = "DAILY, " +
            "WEEKLY,MONTHLY")
    private WeComTaskCenterRepeatType repeatType;
    @ApiModelProperty(value = "周期活动执行的日期，每天 0/每周 1-7/每月 1-31", required = true)
    private String repeatDay;
    @ApiModelProperty(value = "定时执行时间")
    private String scheduleTime;
    @ApiModelProperty(value = "周期活动的开始时间")
    private String repeatStartTime;
    @ApiModelProperty(value = "周期活动的结束时间")
    private String repeatEndTime;
    @ApiModelProperty(value = "人群的圈选条件的uuid", required = true)
    private String userGroupUuid;
    @ApiModelProperty(value = "群发的内容", required = true)
    private List<WeComMassTaskSendMsg> content;
    @ApiModelProperty(value = "任务的执行状态 【UNSTART】未开始 【COMPUTING】人群计算中 【COMPUTE_FAILED】人群获取失败 【SENDING】发送中 " +
            "【SEND_FAILED】发送失败 【FINISHED】执行完成")
    private WeComMassTaskStatus taskStatus;
    @ApiModelProperty(value = "消息类型：【SEND_MESSAGE】发送内容 【ASSIGN_TASK】指派任务 ")
    private String messageType;
    @ApiModelProperty(value = "目标类型:天 DAY/小时 HOUR/分钟 MINUTE")
    private String targetType;
    @ApiModelProperty(value = "目标的时间设置")
    private Integer targetTime;
}
