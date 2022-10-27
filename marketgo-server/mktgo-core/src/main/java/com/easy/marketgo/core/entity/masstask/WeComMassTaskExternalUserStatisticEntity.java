package com.easy.marketgo.core.entity.masstask;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 3:47 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_mass_task_statistic_external_user")
public class WeComMassTaskExternalUserStatisticEntity extends BaseEntity {
    private String projectUuid;
    private String taskUuid;
    private String memberId;
    private String externalUserId;
    private String externalUserName;
    private String externalUserType;
    private String status;
    private Date addCommentsTime;
}
