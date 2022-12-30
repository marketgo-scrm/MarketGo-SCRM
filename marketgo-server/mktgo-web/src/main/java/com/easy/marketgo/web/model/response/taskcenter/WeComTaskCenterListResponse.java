package com.easy.marketgo.web.model.response.taskcenter;

import com.easy.marketgo.common.enums.WeComMassTaskScheduleType;
import com.easy.marketgo.common.enums.WeComMassTaskStatus;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 10:54 PM
 * Describe:
 */
@Data
@ApiModel(description = "企微任务中心列表的response")
public class WeComTaskCenterListResponse {
    @ApiModelProperty(value = "任务的数量")
    private Integer totalCount;
    @ApiModelProperty(value = "任务的列表详情")
    private List<MassTaskDetail> list;

    @Data
    public static class MassTaskDetail {
        @ApiModelProperty(value = "任务的id")
        private Integer id;
        @ApiModelProperty(value = "任务的uuid")
        private String uuid;
        @ApiModelProperty(value = "任务的名称")
        private String name;

        @ApiModelProperty(value = "任务的执行方式")
        private WeComMassTaskScheduleType scheduleType;
        @ApiModelProperty(value = "创建人名称")
        private String creatorName;
        @ApiModelProperty(value = "创建人id")
        private String creatorId;
        @ApiModelProperty(value = "任务的执行时间")
        private String scheduleTime;
        @ApiModelProperty(value = "任务状态；UNSTART 未开始；COMPUTING， COMPUTED， SENDING 进行中； FINISHED 已结束")
        private WeComMassTaskStatus taskStatus;
        @ApiModelProperty(value = "任务类型；SINGLE 客户任务；GROUP 客户群任务； MOMENT 朋友圈任务")
        private WeComMassTaskTypeEnum taskType;
        @ApiModelProperty(value = "是否可以发送提醒")
        private Boolean canRemind;

        @ApiModelProperty(value = "任务完成率")
        private String completeRate;
    }
}
