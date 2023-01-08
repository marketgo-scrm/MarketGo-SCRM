package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.MediaUploadRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/19/22 10:39 PM
 * Describe:
 */
public interface MediaUploadService {
    BaseResponse uploadMedia(String projectId, String corpId, MediaUploadRequest request);

    BaseResponse deleteMedia(String projectId, String corpId, String mediaUuid);

    BaseResponse queryMedia(String projectId, String corpId, String mediaUuid);
}
