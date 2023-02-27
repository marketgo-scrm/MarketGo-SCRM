package com.easy.marketgo.core.model.usergroup;

import lombok.Data;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 1/1/23 12:15 PM
 * Describe:
 */
@Data
public class UserGroupRules {
    /**
     * 企微 wecom
     * 通过数据仓库 cdp
     * 离线   offline
     */
    private String userGroupType;
    // "企微人群的计算条件"
    private WeComUserGroupAudienceRule weComUserGroupRule;
    //"数据仓库人群的计算条件"
    private CdpUserGroupAudienceRule cdpUserGroupRule;
    // "离线人群的计算条件"
    private OfflineUserGroupAudienceRule offlineUserGroupRule;
    //"人群的计算条件关系。 OR或  AND并且"
    private String relation;
}
