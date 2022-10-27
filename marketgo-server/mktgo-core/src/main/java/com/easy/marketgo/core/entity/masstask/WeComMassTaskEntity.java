package com.easy.marketgo.core.entity.masstask;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/24/22 9:19 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_mass_task")
public class WeComMassTaskEntity extends UuidBaseEntity {

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
     * 发送类型:立即发送 IMMEDIATE/定时发送 FIXED_TIME
     */
    private String scheduleType;
    /**
     * 计划发送时间
     */
    private Date scheduleTime;
    /**
     * 关联人群预估UUID
     */
    private String userGroupUuid;
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


    private Date remindTime;

    private Date finishTime;
}
