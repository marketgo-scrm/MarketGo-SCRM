package com.easy.marketgo.api.model.request.tag;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 4:54 PM
 * Describe:
 */
@Data
public class WeComDeleteCorpTagsClientRequest extends BaseRpcRequest {
    private List<String> groupId;

    private List<String> tagId;

    private String agentId;
}
