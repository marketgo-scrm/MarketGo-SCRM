package com.easy.marketgo.core.entity.cdp;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/24/22 3:18 PM
 * Describe:
 */
@Data
@Table("cdp_crowd_users_sync")
public class CdpCrowdUsersSyncEntity extends BaseEntity {
    private String projectUuid;
    private String taskUuid;
    private String corpId;
    private String cdpType;
    private String projectName;
    private String crowdCode;
    private String crowdName;
    private Integer userCount;
    private Integer syncUserCount;
    private String syncStatus;
    private String syncFailedDesc;
}
