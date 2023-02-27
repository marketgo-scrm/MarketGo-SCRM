package com.easy.marketgo.web.model.response.corp;

import com.easy.marketgo.core.model.bo.BaseResponse;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/23/22 10:43 PM
 * Describe:
 */
@Data
public class WeComCorpCallbackResponse extends BaseResponse {
    private String corpName;
    private String corpId;
    private String addressSecret;
    private String callbackUrl;
    private String token;
    private String encodingAesKey;
}
