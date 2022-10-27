package com.easy.marketgo.core.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:19:05
 * @description : WeComUserTenantLinkEntity.java
 */
@Data
@Table("wecom_user_tenant_link")
public class WeComUserTenantLinkEntity extends BaseEntity{
    private String userUuid;
    private String tenantUuid;
}
