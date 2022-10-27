package com.easy.marketgo.web.model.request;

import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.web.model.bo.WeComMassTaskSendMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 3:38 PM
 * Describe:
 */
@Data
@ApiModel(description = "群发活动request")
public class WeComMassTaskRequest {
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
    @ApiModelProperty(value = "群发活动类型， IMMEDIATE 立即；FIXED_TIME 定时", required = true, allowableValues = "IMMEDIATE, " +
            "FIXED_TIME")
    private WeComMassTaskScheduleType scheduleType;
    @ApiModelProperty(value = "定时执行时间")
    private String scheduleTime;
    @ApiModelProperty(value = "人群的圈选条件的uuid", required = true)
    private String userGroupUuid;
    @ApiModelProperty(value = "群发的内容", required = true)
    private List<WeComMassTaskSendMsg> content;
    @ApiModelProperty(value = "任务的执行状态 【UNSTART】未开始 【COMPUTING】人群计算中 【COMPUTE_FAILED】人群获取失败 【SENDING】发送中 " +
            "【SEND_FAILED】发送失败 【FINISHED】执行完成")
    private WeComMassTaskStatus taskStatus;
    @ApiModelProperty(value = "今天是否提醒发送")
    private Boolean canRemind;
}
