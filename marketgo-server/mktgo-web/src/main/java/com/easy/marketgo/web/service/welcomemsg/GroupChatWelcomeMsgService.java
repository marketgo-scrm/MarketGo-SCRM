package com.easy.marketgo.web.service.welcomemsg;

import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.request.welcomemsg.WelcomeMsgGroupChatSaveRequest;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 4/21/23 6:08 PM
 * Describe:
 */
public interface GroupChatWelcomeMsgService {

    BaseResponse queryWelcomeMsgList(String projectUuid, String corpId, Integer pageNum,
                                     Integer pageSize, String keyword);

    BaseResponse createOrUpdate(String projectUuid, String corpId, WelcomeMsgGroupChatSaveRequest request);

    BaseResponse checkTitle(String projectId, String corpId, Integer channelId, String name);

    BaseResponse getWelcomeMsgDetail(String projectId, String corpId, String uuid);

    BaseResponse deleteWelcomeMsg(String projectId, String corpId, String uuid);
}
