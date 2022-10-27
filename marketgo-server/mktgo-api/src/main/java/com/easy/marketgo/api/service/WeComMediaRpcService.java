package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.request.WeComUploadMediaRequest;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 18:16
 * Describe:
 */
public interface WeComMediaRpcService {
    RpcResponse uploadMediaResource(WeComUploadMediaRequest request);

    /**
     * 上传企微附件：朋友圈附件和商品图册
     *
     * @param request
     * @return
     */
    RpcResponse uploadWeComAttachment(WeComUploadMediaRequest request);
}
