package com.easy.marketgo.core.entity.taskcenter;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/27/22 11:27 AM
 * Describe:
 */
@Data
@Accessors(chain = true)
@Table("user_group_estimate")
public class WeComUserGroupAudienceEntity extends UuidBaseEntity {
    private String projectUuid;
    private String taskType;
    private String requestId;
    private String userGroupType;
    private String wecomConditions;
    private String cdpConditions;
    private String offlineConditions;
    private String conditionsRelation;
    private String status;
    private String estimateResult;
}
