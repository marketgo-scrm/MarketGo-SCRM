package com.easy.marketgo.core.entity.taskcenter;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 3:46 PM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("wecom_user_group_detail")
public class WeComUserGroupDetailEntity extends BaseEntity {
    private String projectUuid;
    private String userGroupUuid;
    private String memberId;
    private String memberName;
    private Integer deleted;
    private Integer externalUserCount;
    private String externalUsers;
    private String syncStatus;
    private String sendStatus;
}
