package com.easy.marketgo.core.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 08:35:42
 * @description : TenantConfigEntity.java
 */
@Data
@Table("tenant_config")
public class TenantConfigEntity extends UuidBaseEntity{
    private String name;
    private String mediaStorageType;
    private String status;
    private String serverAddress;
}
