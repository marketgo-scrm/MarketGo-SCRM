package com.easy.marketgo.core.entity.taskcenter;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 12:24 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_task_center_statistic_member")
public class WeComTaskCenterMemberStatisticEntity extends UuidBaseEntity {
    private String projectUuid;
    private String taskUuid;
    private String memberId;
    private String memberName;
    private Integer externalUserCount;
    private Integer deliveredCount;
    private Integer nonFriendCount;
    private String status;
    private Date planTime;
    private Date sentTime;
}
