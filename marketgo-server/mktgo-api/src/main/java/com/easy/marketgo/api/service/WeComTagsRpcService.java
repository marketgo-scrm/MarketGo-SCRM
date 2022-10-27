package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.tag.WeComAddCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComDeleteCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComEditCorpTagsClientRequest;
import com.easy.marketgo.api.model.request.tag.WeComQueryTagsClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/29/22 9:25 PM
 * Describe:
 */
public interface WeComTagsRpcService {
    RpcResponse addCorpTags(WeComAddCorpTagsClientRequest request);
    RpcResponse queryCorpTags(WeComQueryTagsClientRequest request);
    RpcResponse editCorpTags(WeComEditCorpTagsClientRequest request);
    RpcResponse deleteCorpTags(WeComDeleteCorpTagsClientRequest request);
}
