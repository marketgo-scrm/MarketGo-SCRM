package com.easy.marketgo.core.service.usergroup.impl;

import com.easy.marketgo.common.enums.UserGroupAudienceStatusEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.bo.UserGroupEstimateResultBO;
import com.easy.marketgo.core.model.bo.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComUserGroupAudienceRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComGroupChatsRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.service.contacts.WeComMemberService;
import com.easy.marketgo.core.service.usergroup.WeComUserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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

            externalUserCount = weComGroupChatsRepository.countByOwner(corpId, distinctMemberList);
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
