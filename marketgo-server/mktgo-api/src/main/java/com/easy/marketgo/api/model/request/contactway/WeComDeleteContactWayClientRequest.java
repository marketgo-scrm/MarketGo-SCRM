package com.easy.marketgo.api.model.request.contactway;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 3:04 PM
 * Describe:
 */
@Data
public class WeComDeleteContactWayClientRequest extends BaseRpcRequest {
    private String configId;
}
