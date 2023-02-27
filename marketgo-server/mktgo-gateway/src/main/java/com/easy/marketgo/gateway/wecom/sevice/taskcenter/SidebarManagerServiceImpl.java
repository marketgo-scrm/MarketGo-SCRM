package com.easy.marketgo.gateway.wecom.sevice.taskcenter;

import com.easy.marketgo.common.enums.WeComSidebarTypeEnum;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.gateway.wecom.sevice.SidebarManagerService;
import com.easy.marketgo.react.service.WeComClientTaskCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/6/23 1:29 PM
 * Describe:
 */

@Slf4j
@Service
public class SidebarManagerServiceImpl implements SidebarManagerService {

    @Autowired
    private WeComClientTaskCenterService weComClientTaskCenterService;

    @Override
    public BaseResponse getSidebarContent(String corpId, String contentType, String memberId, String taskUuid) {
        if (contentType.equals(WeComSidebarTypeEnum.TASK_CENTER_SEND_CONTENT.getValue())) {
            return BaseResponse.success(weComClientTaskCenterService.getTaskCenterContent(corpId, memberId, taskUuid));
        }
        return BaseResponse.success();
    }
}
