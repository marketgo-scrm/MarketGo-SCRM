package com.easy.marketgo.core.entity.taskcenter;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/24/22 9:19 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_task_center")
public class WeComTaskCenterEntity extends UuidBaseEntity {

    /**
     * 项目ID
     */
    private String projectUuid;
    /**
     * 企微CORP ID
     */
    private String corpId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型:群发好友 SINGLE/群发客户群 GROUP/群发朋友圈 MOMENT
     */
    private String taskType;
    /**
     * 发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME/周期发送 REPEAT_TIME
     */
    private String scheduleType;

    /**
     * 重复类型:每天 DAILY/每周 WEEKLY/每月 MONTHLY
     */
    private String repeatType;

    /**
     * 周期活动执行的日期，每天 0/每周 1-7/每月 1-31
     */
    private String repeatDay;

    /**
     * 计划发送时间
     */
    private Date scheduleTime;

    /**
     * 重复执行的开始时间
     */
    private Date repeatStartTime;

    /**
     * 重复执行的结束时间
     */
    private Date repeatEndTime;

    /**
     * 关联人群预估UUID
     */
    private String userGroupUuid;

    /**
     * 消息类型：【SEND_MESSAGE】发送内容 【ASSIGN_TASK】指派任务
     */
    private String messageType;

    /**
     * 推送消息内容
     */
    private String content;

    /**
     * 任务状态:未开始 UNSTART; 人群计算中 COMPUTING; 计算完成 COMPUTED; 计算失败 COMPUTE_FAILED; 进行中 SENDING; 已结束 FINISHED; 执行失败 FAILED',
     */
    private String taskStatus;
    /**
     * 创建人ID
     */
    private String creatorId;
    /**
     * 创建人姓名
     */
    private String creatorName;

    private String targetType;

    private Integer targetTime;

    private Date planTime;

    private Date finishTime;
}
