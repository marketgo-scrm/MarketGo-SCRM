package com.easy.marketgo.cdp.service;

import com.easy.marketgo.core.model.cdp.CdpCrowdListMessage;
import com.easy.marketgo.core.model.cdp.CrowdBaseRequest;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/23/22 10:46 AM
 * Describe:
 */
public interface CdpCrowdService {
    CdpCrowdListMessage queryCrowdList(CrowdBaseRequest request);

    void queryCrowdUsers();
}
