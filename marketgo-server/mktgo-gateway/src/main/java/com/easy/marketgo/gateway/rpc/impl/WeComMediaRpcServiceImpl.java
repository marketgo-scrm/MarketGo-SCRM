package com.easy.marketgo.gateway.rpc.impl;

import cn.hutool.core.bean.BeanUtil;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.request.WeComUploadMediaRequest;
import com.easy.marketgo.api.service.WeComMediaRpcService;
import com.easy.marketgo.gateway.wecom.request.UploadMediaRequest;
import com.easy.marketgo.gateway.wecom.sevice.MediaManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-08 21:02
 * Describe:
 */
@Slf4j
@DubboService
public class WeComMediaRpcServiceImpl implements WeComMediaRpcService {

    @Autowired
    private MediaManagerService mediaManagerService;

    @Override
    public RpcResponse uploadMediaResource(WeComUploadMediaRequest request) {
        UploadMediaRequest uploadMediaRequest = new UploadMediaRequest();
        BeanUtil.copyProperties(request, uploadMediaRequest);
        uploadMediaRequest.setFileData(request.getFileData());

        return mediaManagerService.uploadMedia(request.getCorpId(), request.getAgentId(), uploadMediaRequest);
    }

    @Override
    public RpcResponse uploadWeComAttachment(WeComUploadMediaRequest request) {
        UploadMediaRequest uploadMediaRequest = new UploadMediaRequest();
        BeanUtil.copyProperties(request, uploadMediaRequest);
        uploadMediaRequest.setFileData(request.getFileData());

        return mediaManagerService.uploadWeComAttachment(request.getCorpId(), request.getAgentId(), uploadMediaRequest);
    }
}
