package com.easy.marketgo.gateway.wecom.sevice;

import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.WeComUploadMediaClientResponse;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.UploadMediaRequest;
import com.easy.marketgo.gateway.wecom.response.UploadMediaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-06-06 10:01
 * Describe:
 */
@Slf4j
@Service
public class MediaManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    public RpcResponse uploadMedia(String corpId, String agentId, UploadMediaRequest request) {
        log.info("begin to upload weCom media. corpId={}, agentId={}, request={}", corpId, agentId, request);
        Map<String, String> paramsHeader = new HashMap<>();
        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String url = WeComHttpConstants.UPLOAD_TEMP_MEDIA_URL;
        if (request.getTempUpload()) {
            paramsHeader.put("type", request.getMediaType().getType().toLowerCase());
        } else {
            url = WeComHttpConstants.UPLOAD_IMAGE_URL;
        }
        return uploadWeComMedia(request, url, paramsHeader);
    }

    /**
     * 上传附件 目前企微限制商品图册仅支持图片， 朋友圈附件仅支持：图片和视频
     *
     * @param request
     * @return
     */
    public RpcResponse uploadWeComAttachment(String corpId, String agentId, UploadMediaRequest request) {
        log.info("begin to upload weCom attachment. corpId={}, agentId={}, request={}", corpId, agentId, request);
        Map<String, String> queryParams = new HashMap<>();
        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        queryParams.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        queryParams.put("media_type", request.getMediaType().getType().toLowerCase());
        queryParams.put("attachment_type", String.valueOf(request.getAttachmentType().getValue()));
        RpcResponse rpcResponse = uploadWeComMedia(request, WeComHttpConstants.UPLOAD_ATTACHMENT_URL,
                queryParams);
        return rpcResponse;
    }

    /**
     * 上传素材
     *
     * @param request
     * @return
     */
    private RpcResponse uploadWeComMedia(UploadMediaRequest request, String requestUrl,
                                         Map<String, String> paramsHeader) {
        WeComUploadMediaClientResponse clientResponse = new WeComUploadMediaClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        log.info("upload media to weCom. request={}, paramsHeader={}", request, paramsHeader);
        String response = null;
        try {
            response = OkHttpUtils.getInstance().PostFileDataSync(requestUrl, paramsHeader, request.getFilename(),
                    request.getFileData());
        } catch (Exception e) {
            log.error("failed to upload media. request={}", request, e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("upload media response from weCom. response={}", response);
        UploadMediaResponse uploadMediaResponse = JsonUtils.toObject(response, UploadMediaResponse.class);
        if (uploadMediaResponse != null) {
            rpcResponse.setCode(uploadMediaResponse.getErrcode());
            rpcResponse.setMessage(uploadMediaResponse.getErrmsg());
            BeanUtils.copyProperties(uploadMediaResponse, clientResponse);
        }
        rpcResponse.setData(clientResponse);
        log.info("return upload media rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }
}
