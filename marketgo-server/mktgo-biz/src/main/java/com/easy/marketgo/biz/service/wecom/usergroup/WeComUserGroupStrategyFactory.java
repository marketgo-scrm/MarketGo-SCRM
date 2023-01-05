package com.easy.marketgo.biz.service.wecom.usergroup;

import com.easy.marketgo.common.enums.UserGroupAudienceTypeEnum;
import com.easy.marketgo.biz.service.wecom.usergroup.impl.CdpUserGroupServiceImpl;
import com.easy.marketgo.biz.service.wecom.usergroup.impl.OfflineUserGroupServiceImpl;
import com.easy.marketgo.biz.service.wecom.usergroup.impl.WeComUserGroupManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/25/22 5:34 PM
 * Describe:
 */
@Slf4j
@Component
public class WeComUserGroupStrategyFactory {

    @Autowired
    private Map<String, UserGroupService> userGroupMap;

    @Autowired
    private CdpUserGroupServiceImpl cdpUserGroupService;

    @Autowired
    private OfflineUserGroupServiceImpl offlineUserGroupService;

    @Autowired
    private WeComUserGroupManagerImpl weComUserGroupService;

    @PostConstruct
    private void init() {
        userGroupMap.put(UserGroupAudienceTypeEnum.WECOM_USER_GROUP.getValue(), this.weComUserGroupService);
        userGroupMap.put(UserGroupAudienceTypeEnum.CDP_USER_GROUP.getValue(), this.cdpUserGroupService);
        userGroupMap.put(UserGroupAudienceTypeEnum.OFFLINE_USER_GROUP.getValue(), this.offlineUserGroupService);
    }

    public UserGroupService getUserGroupService(String userGroupType) {
        return userGroupMap.get(userGroupType);
    }

}
