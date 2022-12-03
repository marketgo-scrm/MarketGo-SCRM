package com.easy.marketgo.core.entity.usergroup;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : wangkevin
 * @version : v1.0
 * @date :  2022-07-16 08:19:05
 * @description : WeComUserTenantLinkEntity.java
 */
@Data
@Table("user_group_cdp")
public class UserGroupCdpEntity extends BaseEntity {
    private String cdpUuid;
    private String corpId;
    private String cdpType;
    private String projectName;
    private String crowdCode;
    private String externalUserId;
    private String memberId;
}
