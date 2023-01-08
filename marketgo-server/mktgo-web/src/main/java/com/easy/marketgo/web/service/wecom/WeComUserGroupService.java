package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.core.model.bo.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 4:50 PM
 * Describe:
 */

public interface WeComUserGroupService {

    BaseResponse estimate(String projectId, UserGroupAudienceRules audienceRules, String requestId,
                          String corpId, String taskType);

    BaseResponse queryUserGroup(String projectId, String corpId, String taskType, String groupUuid);

    BaseResponse offlineUserGroup(String projectId, String corpId, String groupUuid, String fileType,
                                  MultipartFile multipartFile);

    BaseResponse getExcelTemplate(String projectId, String corpId, HttpServletResponse httpServletResponse);

    BaseResponse deleteOfflineUserGroup(String corpId, String groupUuid);

    BaseResponse queryCrowdList(String projectId, String corpId);
}
