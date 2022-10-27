package com.easy.marketgo.api.model.request.tag;

import com.easy.marketgo.api.model.request.BaseRpcRequest;
import lombok.Data;

import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/31/22 5:41 PM
 * Describe:
 */
@Data
public class WeComEditExternalUserCorpTagClientRequest extends BaseRpcRequest {
    private String userId;
    private String externalUserId;
    private List<String> addTag;
    private List<String> removeTag;
}
