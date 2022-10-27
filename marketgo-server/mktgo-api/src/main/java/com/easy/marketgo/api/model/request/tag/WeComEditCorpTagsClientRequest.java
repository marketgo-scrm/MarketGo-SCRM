package com.easy.marketgo.api.model.request.tag;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 4:54 PM
 * Describe:
 */
@Data
public class WeComEditCorpTagsClientRequest extends BaseRpcRequest {
    private String id;

    private String name;

    private Integer order;
}
