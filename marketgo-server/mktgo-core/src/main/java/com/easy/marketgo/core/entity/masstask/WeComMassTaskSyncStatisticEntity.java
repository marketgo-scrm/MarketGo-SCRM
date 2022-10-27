package com.easy.marketgo.core.entity.masstask;

import com.easy.marketgo.common.enums.WeComMassTaskSendIdType;
import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 4:37 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_mass_task_statistic_sync")
public class WeComMassTaskSyncStatisticEntity extends UuidBaseEntity {
    private String taskUuid;
    private WeComMassTaskTypeEnum taskType;
    private String sendId;
    private WeComMassTaskSendIdType sendIdType;
    private Boolean deleted;
    private String syncStatus;
}
