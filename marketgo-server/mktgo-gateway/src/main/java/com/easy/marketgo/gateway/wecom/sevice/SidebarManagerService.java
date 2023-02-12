package com.easy.marketgo.gateway.wecom.sevice;

import com.easy.marketgo.core.model.bo.BaseResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/6/23 1:29 PM
 * Describe:
 */

public interface SidebarManagerService {

    BaseResponse getSidebarContent(String corpId, String contentType, String memberId, String taskUuid);
}
