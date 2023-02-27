package com.easy.marketgo.biz.service.wecom.usergroup;

import com.easy.marketgo.common.enums.WeComMassTaskTypeEnum;
import com.easy.marketgo.biz.service.wecom.usergroup.impl.*;
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
public class WeComTaskTypeStrategyFactory {

    @Autowired
    private Map<String, WeComUserGroupService> userGroupMap;

    @Autowired
    private WeComSingleUserGroupServiceImpl weComSingleUserGroupService;

    @Autowired
    private WeComGroupUserGroupServiceImpl weComGroupUserGroupService;

    @Autowired
    private WeComMomentUserGroupServiceImpl weComMomentUserGroupService;

    @PostConstruct
    private void init() {
        userGroupMap.put(WeComMassTaskTypeEnum.SINGLE.name(), this.weComSingleUserGroupService);
        userGroupMap.put(WeComMassTaskTypeEnum.GROUP.name(), this.weComGroupUserGroupService);
        userGroupMap.put(WeComMassTaskTypeEnum.MOMENT.name(), this.weComMomentUserGroupService);
    }

    public WeComUserGroupService getUserGroupTaskService(String userGroupTaskType) {
        return userGroupMap.get(userGroupTaskType);
    }
}
