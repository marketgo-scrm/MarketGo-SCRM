package com.easy.marketgo.core.entity.cdp;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/24/22 3:18 PM
 * Describe:
 */
@Data
@Table("cdp_config")
public class CdpConfigEntity extends UuidBaseEntity {
    private String projectUuid;
    private String corpId;
    private String cdpType;
    private String apiUrl;
    private String dataUrl;
    private String appKey;
    private String apiSecret;
    private String projectName;
    private Boolean status;
}
