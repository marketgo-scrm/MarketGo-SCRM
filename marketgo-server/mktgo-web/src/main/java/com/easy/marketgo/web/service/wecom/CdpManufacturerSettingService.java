package com.easy.marketgo.web.service.wecom;

import com.easy.marketgo.web.model.request.CdpManufacturerMessageRequest;
import com.easy.marketgo.web.model.request.UserGroupAudienceRules;
import com.easy.marketgo.web.model.response.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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

    BaseResponse CdpSettingTest(String projectId, String corpId, String cdpType,
                                CdpManufacturerMessageRequest request);
}
