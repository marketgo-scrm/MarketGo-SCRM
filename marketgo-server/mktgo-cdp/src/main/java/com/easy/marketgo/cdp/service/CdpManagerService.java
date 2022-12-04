package com.easy.marketgo.cdp.service;

import com.easy.marketgo.cdp.model.CdpCrowdListMessage;
import com.easy.marketgo.cdp.model.CdpTestSettingRequest;
import com.easy.marketgo.cdp.model.CdpTestSettingResponse;
import com.easy.marketgo.cdp.model.CrowdUsersBaseRequest;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 11/22/22 10:30 PM
 * Describe:
 */
public interface CdpManagerService {

    /**
     * 获取CDP的分群列表，分群的信息必须包含：分群的code【用于获取分群的用户列表】，分群的人群数量，分群的name；
     *
     * @param request
     * @return
     */
    CdpTestSettingResponse testCdpSetting(String corpId, String cdpType, CdpTestSettingRequest request);

    /**
     * 获取CDP的分群列表，分群的信息必须包含：分群的code【用于获取分群的用户列表】，分群的人群数量，分群的name；
     *
     * @param projectId
     * @param corpId
     * @return
     */
    CdpCrowdListMessage queryCrowdList(String projectId, String corpId);

    /**
     * 提交分群的用户数据查询记录
     *
     * @param
     * @param
     * @return
     */
    void queryCrowdUsers(CrowdUsersBaseRequest request);


    /**
     * 获取分群的用户数据，需要获取到客户id+员工id的对应于的列表；数据量大可以通过流式的方式下载
     *
     * @param
     * @param
     * @return
     */
    void downloadCrowdUsers(CrowdUsersBaseRequest request);
}
