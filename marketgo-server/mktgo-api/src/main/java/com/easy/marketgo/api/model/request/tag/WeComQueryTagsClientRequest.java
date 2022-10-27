package com.easy.marketgo.api.model.request.tag;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/30/22 12:36 PM
 * Describe:
 */
@Data
public class WeComQueryTagsClientRequest extends BaseRpcRequest {
    private List<String> tagIds;
    private List<String> groupIds;
}
