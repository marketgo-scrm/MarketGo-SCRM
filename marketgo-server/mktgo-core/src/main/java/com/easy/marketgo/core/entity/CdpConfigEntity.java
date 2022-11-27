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
@Table("cdp_config")
public class CdpConfigEntity extends BaseEntity {
    private String projectUuid;
    private String cdpType;
    private String apiUrl;
    private String dataUrl;
    private String appKey;
    private String apiSecret;
    private String projectName;
    private Boolean status;
}
