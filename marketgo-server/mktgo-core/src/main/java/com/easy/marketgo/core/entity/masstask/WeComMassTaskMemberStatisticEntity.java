package com.easy.marketgo.core.entity.masstask;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 12:24 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_mass_task_statistic_member")
public class WeComMassTaskMemberStatisticEntity extends BaseEntity {
    private String projectUuid;
    private String taskUuid;
    private String memberId;
    private String memberName;
    private Integer externalUserCount;
    private Integer deliveredCount;
    private Integer nonFriendCount;
    private Integer exceedLimitCount;
    private String status;
    private String sendId;
}
