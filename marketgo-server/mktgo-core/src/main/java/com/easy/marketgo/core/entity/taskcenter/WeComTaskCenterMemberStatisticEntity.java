package com.easy.marketgo.core.entity.taskcenter;

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
@Table("wecom_task_center_statistic_member")
public class WeComTaskCenterMemberStatisticEntity extends BaseEntity {
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
