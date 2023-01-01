package com.easy.marketgo.core.model.cdp;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/4/22 11:28 AM
 * Describe:
 */
@Data
public class CdpTestSettingRequest {
    private String apiUrl;
    private String appKey;
    private String projectName;
    private String apiSecret;
    private String dataUrl;
}
