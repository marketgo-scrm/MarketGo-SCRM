package com.easy.marketgo.core.entity.masstask;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/12/22 3:49 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_mass_task_send_queue")
public class WeComMassTaskSendQueueEntity extends UuidBaseEntity {
    private String memberMd5;
    private String taskUuid;
    private String memberId;
    private String externalUserIds;
    private String status;
}
