package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.CdpManufacturerMessageRequest;
import com.easy.marketgo.core.model.bo.BaseResponse;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/21/22 4:50 PM
 * Describe:
 */

public interface CdpManufacturerSettingService {

    BaseResponse queryCdpList(String projectId, String corpId);

    BaseResponse saveOrUpdateCdpMessage(String projectId, String corpId, String cdpType,
                                        CdpManufacturerMessageRequest request);

    BaseResponse queryCdpMessage(String projectId, String corpId, String cdpType);

    BaseResponse changeCdpStatus(String projectId, String corpId, String cdpType, Boolean status);

    BaseResponse deleteCdpMessage(String projectId, String corpId, String cdpType);

    BaseResponse cdpSettingTest(String projectId, String corpId, String cdpType,
                                CdpManufacturerMessageRequest request);

    BaseResponse cdpSettingStatus(String projectId, String corpId);
}
