package com.easy.marketgo.web.model.response.masstask;

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
@ApiModel(description = "企微群发详情的response")
public class WeComMassTaskDetailResponse {
    @ApiModelProperty(value = "群发的任务id")
    private Integer id = null;
    @ApiModelProperty(value = "群发的任务uuid")
    private String uuid = null;

    @ApiModelProperty(value = "企微CORP ID")
    private String corpId;

    @ApiModelProperty(value = "企微Agent ID")
    private String agentId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "创建人ID")
    private String creatorId;

    @ApiModelProperty(value = "创建人姓名")
    private String creatorName;

    @ApiModelProperty(value = "发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME")
    private String scheduleType;

    @ApiModelProperty(value = "计划发送时间")
    private String scheduleTime;

    @ApiModelProperty(value = "计划创建时间")
    private String createTime;

    @ApiModelProperty(value = "关联人群预估UUID")
    private String userGroupUuid;

    @ApiModelProperty(value = "推送消息内容")
    private List<WeComMassTaskSendMsg> msgContent = null;

    @ApiModelProperty(value = "任务状态:未开始 UNSTART; 人群计算中 COMPUTING; 计算完成 COMPUTED; 计算失败 COMPUTE_FAILED; 进行中 SENDING; " +
            "已结束 FINISHED; 执行失败 FAILED',")
    private String taskStatus;

    @ApiModelProperty(value = "提醒发送的开关")
    private Boolean canRemind = false;
}
