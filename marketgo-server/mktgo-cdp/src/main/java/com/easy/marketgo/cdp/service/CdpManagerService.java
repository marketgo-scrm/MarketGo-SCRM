package com.easy.marketgo.cdp.service;

import com.easy.marketgo.cdp.model.CdpCrowdListMessage;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/22/22 10:30 PM
 * Describe:
 */
public interface CdpManagerService {

    CdpCrowdListMessage queryCrowdList(String projectId, String corpId);

    void queryUsersForCrowd();
}
