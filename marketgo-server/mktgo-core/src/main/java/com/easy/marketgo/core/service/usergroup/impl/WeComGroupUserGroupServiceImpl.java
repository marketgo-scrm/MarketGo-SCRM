package com.easy.marketgo.core.service.usergroup.impl;

import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.bo.UserGroupEstimateResultBO;
import com.easy.marketgo.core.model.bo.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.model.taskcenter.QueryGroupChatsBuildSqlParam;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import com.easy.marketgo.core.service.usergroup.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 4:31 PM
 * Describe:
 */

@Slf4j
@Component
public class WeComGroupUserGroupServiceImpl implements WeComUserGroupService {

    @Autowired
    private WeComMemberService weComMemberService;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComUserGroupAudienceRepository weComUserGroupAudienceRepository;

    @Autowired
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Override
    public void userGroupEstimate(String projectId, String corpId, String requestId,
                                  WeComUserGroupAudienceRule weComUserGroupAudienceRule) {

        Integer memberCount = 0;
        Integer externalUserCount = 0;
        try {
            List<Long> departments = new ArrayList<>();
            List<String> memberList = new ArrayList<>();
            if (!weComUserGroupAudienceRule.getMembers().getIsAll()) {
                //获取人群条件中的部门列表
                if (CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getMembers().getDepartments())) {
                    weComUserGroupAudienceRule.getMembers().getDepartments().forEach(department -> {
                        departments.add(department.getId());
                    });

                    List<Long> departmentList = weComMemberService.getSubDepartmentList(departments);
                    if (CollectionUtils.isNotEmpty(departmentList)) {
                        departments.addAll(departmentList);
                    }
                    memberList.addAll(weComMemberService.getMembers(corpId, departments));
                }

                //获取人群条件中的员工列表
                if (CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getMembers().getUsers())) {
                    weComUserGroupAudienceRule.getMembers().getUsers().forEach(user -> {
                        memberList.add(user.getMemberId());
                    });
                    memberCount = memberList.size();
                }
            }

            List<String> distinctMemberList = memberList.stream().distinct().collect(Collectors.toList());
            memberCount = distinctMemberList.size();
            log.info("user group for member estimate result. memberCount={}, distinctMemberList={}", memberCount,
                    distinctMemberList);


            QueryGroupChatsBuildSqlParam paramBuilder =
                    QueryGroupChatsBuildSqlParam.builder().corpId(corpId).memberIds(distinctMemberList).build();
            paramBuilder.setRelation(weComUserGroupAudienceRule.getGroupChats().getRelation());
            if (!weComUserGroupAudienceRule.getGroupChats().getIsAll()) {
                //创建时间的条件
                if (weComUserGroupAudienceRule.getGroupChats().isCreateTimeSwitch()) {
                    paramBuilder.setStartTime(weComUserGroupAudienceRule.getGroupChats().getStartTime());
                    paramBuilder.setEndTime(weComUserGroupAudienceRule.getGroupChats().getEndTime());
                }

                //客户群名称条件
                if (weComUserGroupAudienceRule.getGroupChats().isGroupChatNameSwitch() && StringUtils.isNotBlank(weComUserGroupAudienceRule.getGroupChats().getGroupChatName())) {
                    paramBuilder.setGroupChatName(weComUserGroupAudienceRule.getGroupChats().getGroupChatName());
                }

                //客户群人数条件
                if (weComUserGroupAudienceRule.getGroupChats().isUserCountSwitch() &&
                        weComUserGroupAudienceRule.getGroupChats().getUserCount() != null &&
                        StringUtils.isNotBlank(weComUserGroupAudienceRule.getGroupChats().getUserCountRule())) {
                    paramBuilder.setUserCount(weComUserGroupAudienceRule.getGroupChats().getUserCount());
                    paramBuilder.setUserCountFunction(weComUserGroupAudienceRule.getGroupChats().getUserCountRule());
                }

                //客户群条件
                if (weComUserGroupAudienceRule.getGroupChats().isGroupChatsSwitch() &&
                        CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getGroupChats().getGroupChats())) {
                    List<String> groups = new ArrayList<>();
                    weComUserGroupAudienceRule.getGroupChats().getGroupChats().forEach(group -> {
                        groups.add(group.getChatId());
                    });

                    paramBuilder.setGroupChatIds(groups);
                }
            }
            //排除客户条件
            if (weComUserGroupAudienceRule.isExcludeGroupChatSwitch()) {
                //创建时间的条件
                if (weComUserGroupAudienceRule.getExcludeGroupChats().isCreateTimeSwitch()) {
                    paramBuilder.setExcludeStartTime(weComUserGroupAudienceRule.getGroupChats().getStartTime());
                    paramBuilder.setExcludeEndTime(weComUserGroupAudienceRule.getGroupChats().getEndTime());
                }

                //客户群名称条件
                if (weComUserGroupAudienceRule.getExcludeGroupChats().isGroupChatNameSwitch() && StringUtils.isNotBlank(weComUserGroupAudienceRule.getExcludeGroupChats().getGroupChatName())) {
                    paramBuilder.setExcludeGroupChatName(weComUserGroupAudienceRule.getExcludeGroupChats().getGroupChatName());
                }

                //客户群人数条件
                if (weComUserGroupAudienceRule.getExcludeGroupChats().isUserCountSwitch() &&
                        weComUserGroupAudienceRule.getExcludeGroupChats().getUserCount() != null &&
                        StringUtils.isNotBlank(weComUserGroupAudienceRule.getExcludeGroupChats().getUserCountRule())) {
                    paramBuilder.setExcludeUserCount(weComUserGroupAudienceRule.getExcludeGroupChats().getUserCount());
                    paramBuilder.setExcludeUserCountFunction(weComUserGroupAudienceRule.getExcludeGroupChats().getUserCountRule());
                }

                //客户群条件
                if (weComUserGroupAudienceRule.getExcludeGroupChats().isGroupChatsSwitch() &&
                        CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExcludeGroupChats().getGroupChats())) {
                    List<String> groups = new ArrayList<>();
                    weComUserGroupAudienceRule.getExcludeGroupChats().getGroupChats().forEach(group -> {
                        groups.add(group.getChatId());
                    });

                    paramBuilder.setExcludeGroupChatIds(groups);
                }

            }

            externalUserCount = weComGroupChatsRepository.countByCnd(paramBuilder);
            log.info("user group to query group chat count. groupChatCount={}", externalUserCount);

        } catch (Exception e) {
            log.error("failed to user group for external user estimate result. requestId={}", requestId, e);
        }
        UserGroupEstimateResultBO userGroupEstimateResultBO = new UserGroupEstimateResultBO();
        userGroupEstimateResultBO.setExternalUserCount(externalUserCount);
        userGroupEstimateResultBO.setMemberCount(memberCount);

        weComUserGroupAudienceRepository.updateResultByRequestId(requestId, projectId,
                JsonUtils.toJSONString(userGroupEstimateResultBO),
                UserGroupAudienceStatusEnum.SUCCEED.getValue());
    }
}
