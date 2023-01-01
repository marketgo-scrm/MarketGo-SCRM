package com.easy.marketgo.core.model.cdp;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 11:16 AM
 * Describe:
 */
@Data
public class CrowdBaseRequest {
    private String corpId;
    private String cdpType;
    private String apiUrl;
    private String dataUrl;
    private String appKey;
    private String apiSecret;
    private String projectName;
}
