package com.easy.marketgo.core.service.contacts;

import com.easy.marketgo.core.entity.customer.WeComRelationMemberExternalUserEntity;
import com.easy.marketgo.core.model.bo.QueryUserGroupBuildSqlParam;
import com.easy.marketgo.core.model.taskcenter.QueryGroupChatsBuildSqlParam;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/29/22 7:14 PM
 * Describe:
 */

@Slf4j
@Component
public class WeComCustomerService {

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Autowired
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    public List<WeComRelationMemberExternalUserEntity> getExternalUsers(String corpId, List<String> memberList,
                                                                        WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        int offset = 0;
        int limitSize = 20000;
        List<WeComRelationMemberExternalUserEntity> entities = new ArrayList<>();
        QueryUserGroupBuildSqlParam paramExternalUser =
                buildQueryExternalUserSqlParam(corpId, memberList, weComUserGroupAudienceRule, offset, limitSize);

        List<WeComRelationMemberExternalUserEntity> externalUserEntities =
                weComRelationMemberExternalUserRepository.listByUserGroupCnd(paramExternalUser);
        log.info("user group for external user estimate result. externalUserCount={}",
                externalUserEntities.size());
        entities.addAll(externalUserEntities);
        return entities;
    }

    public QueryUserGroupBuildSqlParam buildQueryExternalUserSqlParam(String corpId, List<String> memberList,
                                                                      WeComUserGroupAudienceRule weComUserGroupAudienceRule, Integer offset, Integer limitSize) {
        QueryUserGroupBuildSqlParam paramBuilder =
                QueryUserGroupBuildSqlParam.builder().corpId(corpId).memberIds(memberList).build();
        if (offset != null) {
            paramBuilder.setPageNum(offset);
        }
        if (limitSize != null) {
            paramBuilder.setPageSize(limitSize);
        }
        paramBuilder.setRelation(weComUserGroupAudienceRule.getExternalUsers().getRelation());
        if (!weComUserGroupAudienceRule.getExternalUsers().getIsAll()) {
            //添加时间的条件
            if (weComUserGroupAudienceRule.getExternalUsers().isAddTimeSwitch()) {
                paramBuilder.setStartTime(weComUserGroupAudienceRule.getExternalUsers().getStartTime());
                paramBuilder.setEndTime(weComUserGroupAudienceRule.getExternalUsers().getEndTime());
            }

            //标签条件
            if (weComUserGroupAudienceRule.getExternalUsers().isCorpTagSwitch() &&
                    weComUserGroupAudienceRule.getExternalUsers().getCorpTags() != null &&
                    CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getTags())) {
                paramBuilder.setTagRelation(weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getRelation());
                List<String> tags = new ArrayList<>();
                weComUserGroupAudienceRule.getExternalUsers().getCorpTags().getTags().forEach(weComCorpTag -> {
                    tags.add(weComCorpTag.getId());
                });

                paramBuilder.setTags(tags);
            }

            //性别条件
            if (weComUserGroupAudienceRule.getExternalUsers().isGenderSwitch() &&
                    CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExternalUsers().getGenders())) {
                paramBuilder.setGenders(weComUserGroupAudienceRule.getExternalUsers().getGenders());
            }

            //客户群条件
            if (weComUserGroupAudienceRule.getExternalUsers().isGroupChatsSwitch() &&
                    CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExternalUsers().getGroupChats())) {
                List<String> groups = new ArrayList<>();
                weComUserGroupAudienceRule.getExternalUsers().getGroupChats().forEach(group -> {
                    groups.add(group.getChatId());
                });

                paramBuilder.setGroupChats(groups);
            }
        }
        //排除客户条件
        if (weComUserGroupAudienceRule.isExcludeSwitch()) {
            //添加排除客户的时间条件
            paramBuilder.setExcludeRelation(weComUserGroupAudienceRule.getExcludeExternalUsers().getRelation());
            if (weComUserGroupAudienceRule.getExcludeExternalUsers().isAddTimeSwitch()) {
                paramBuilder.setExcludeStartTime(weComUserGroupAudienceRule.getExcludeExternalUsers().getStartTime());
                paramBuilder.setExcludeEndTime(weComUserGroupAudienceRule.getExcludeExternalUsers().getEndTime());
            }

            //添加排除客户的标签条件
            if (weComUserGroupAudienceRule.getExcludeExternalUsers().isCorpTagSwitch()
                    && weComUserGroupAudienceRule.getExcludeExternalUsers().getCorpTags() != null
                    && CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExcludeExternalUsers().getCorpTags().getTags())) {
                paramBuilder.setExcludeTagRelation(weComUserGroupAudienceRule.getExcludeExternalUsers().getCorpTags().getRelation());
                List<String> tags = new ArrayList<>();
                weComUserGroupAudienceRule.getExcludeExternalUsers().getCorpTags().getTags().forEach(weComCorpTag -> {
                    tags.add(weComCorpTag.getId());
                });
                paramBuilder.setExcludeTags(tags);
            }

            //添加排除客户的性别条件
            if (weComUserGroupAudienceRule.getExcludeExternalUsers().isGenderSwitch() &&
                    CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExcludeExternalUsers().getGenders())) {
                paramBuilder.setExcludeGenders(weComUserGroupAudienceRule.getExcludeExternalUsers().getGenders());
            }

            //添加排除客户的客户群条件
            if (weComUserGroupAudienceRule.getExcludeExternalUsers().isGroupChatsSwitch() &&
                    CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getExcludeExternalUsers().getGroupChats())) {
                List<String> groups = new ArrayList<>();
                weComUserGroupAudienceRule.getExcludeExternalUsers().getGroupChats().forEach(group -> {
                    groups.add(group.getChatId());
                });

                paramBuilder.setExcludeGroupChats(groups);
            }

        }
        return paramBuilder;
    }

    public QueryGroupChatsBuildSqlParam buildQueryGroupChatsSqlParam(String corpId, List<String> memberList,
                                                                     WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        QueryGroupChatsBuildSqlParam paramBuilder =
                QueryGroupChatsBuildSqlParam.builder().corpId(corpId).memberIds(memberList).build();

        if (weComUserGroupAudienceRule.getGroupChats() != null && !weComUserGroupAudienceRule.getGroupChats().getIsAll()) {
            paramBuilder.setRelation(weComUserGroupAudienceRule.getGroupChats().getRelation());
            //创建时间的条件
            if (weComUserGroupAudienceRule.getGroupChats().isCreateTimeSwitch()) {
                paramBuilder.setStartTime(weComUserGroupAudienceRule.getGroupChats().getStartTime());
                paramBuilder.setEndTime(weComUserGroupAudienceRule.getGroupChats().getEndTime());
            }

            //客户群名称条件
            if (weComUserGroupAudienceRule.getGroupChats().isGroupChatNameSwitch()
                    && StringUtils.isNotBlank(weComUserGroupAudienceRule.getGroupChats().getGroupChatName())) {
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
        if (weComUserGroupAudienceRule.isExcludeGroupChatSwitch() && weComUserGroupAudienceRule.getExcludeGroupChats() != null) {
            //创建时间的条件
            if (weComUserGroupAudienceRule.getExcludeGroupChats().isCreateTimeSwitch()) {
                paramBuilder.setExcludeStartTime(weComUserGroupAudienceRule.getGroupChats().getStartTime());
                paramBuilder.setExcludeEndTime(weComUserGroupAudienceRule.getGroupChats().getEndTime());
            }

            //客户群名称条件
            if (weComUserGroupAudienceRule.getExcludeGroupChats().isGroupChatNameSwitch() &&
                    StringUtils.isNotBlank(weComUserGroupAudienceRule.getExcludeGroupChats().getGroupChatName())) {
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
        return paramBuilder;
    }
}
