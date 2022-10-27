package com.easy.marketgo.gateway.wecom.response;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/6/22 10:51 PM
 * Describe:
 */
@Data
public class UploadImageResponse extends WeComBaseResponse {
    private String url;
}
