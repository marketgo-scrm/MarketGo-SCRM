package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.tags.WeComAddCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComDeleteCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComEditCorpTagRequest;
import com.easy.marketgo.web.model.request.tags.WeComMarkCorpTagsRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/14/22 4:30 PM
 * Describe:
 */
public interface CorpTagsManagerService {

    BaseResponse listCorpTags(String projectId, String corpId, String keyword, String syncStatus);

    BaseResponse markCorpTags(String projectId, String corpId, WeComMarkCorpTagsRequest request);

    BaseResponse addCorpTags(String projectId, String corpId, WeComAddCorpTagRequest request);

    BaseResponse deleteCorpTags(String projectId, String corpId, WeComDeleteCorpTagRequest request);

    BaseResponse editCorpTags(String projectId, String corpId, WeComEditCorpTagRequest request);
}
