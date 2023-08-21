package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.DateFormatUtils;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.*;
import com.easy.marketgo.core.entity.tag.WeComCorpTagEntity;
import com.easy.marketgo.core.model.bo.QueryExternalUserBuildSqlParam;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.customer.*;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.web.model.response.customer.*;
import com.easy.marketgo.web.service.wecom.ContactsManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 7/14/22 4:31 PM
 * Describe:
 */
@Slf4j
@Service
public class ContactsManagerServiceImpl implements ContactsManagerService {

    private static Integer GROUP_CHAT_MEMBER_TYPE_EXTERNAL_USER = 2;
    @Resource
    private WeComGroupChatsRepository weComGroupChatsRepository;

    @Resource
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Resource
    private WeComGroupChatMembersRepository weComGroupChatMembersRepository;

    @Resource
    private WeComRelationMemberExternalUserRepository weComRelationMemberExternalUserRepository;

    @Autowired
    private WeComCorpTagRepository weComCorpTagRepository;

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComEventGroupChatsRepository weComEventGroupChatsRepository;

    @Autowired
    private WeComEventExternalUserRepository weComEventExternalUserRepository;

    private static final String EVENT_TYPE_UPDATE = "update";

    private static final String UPDATE_DETAIL_ADD_MEMBER = "add_member";

    private static final String UPDATE_DETAIL_DEL_MEMBER = "del_member";

    private static final String EVENT_TYPE_ADD_EXTERNAL_USER = "add_external_contact";

    private static final String EVENT_TYPE_DEL_EXTERNAL_USER = "del_external_contact";

    private static final String EVENT_TYPE_DEL_FOLLOW_USER = "del_follow_user";

    @Override
    public BaseResponse getExternalUserList(String projectId, Integer pageNum, Integer pageSize, String corpId,
                                            String externalUser,
                                            List<Integer> statuses,
                                            boolean duplicate,
                                            List<String> memberIds,
                                            List<Long> departments,
                                            List<String> tags,
                                            List<Integer> channels,
                                            List<String> groupChats,
                                            List<Integer> genders,
                                            String startTime,
                                            String endTime) {
        log.info("query external user list. projectId={}, corpId={}, pageNum={}, pageSize={}, externalUser={}, " +
                        "statuses={}, duplicate={}, memberIds={}, tags={}, channels={}, groupChats={}, genders={}, " +
                        "startTime={}, endTime={}", projectId, corpId, pageNum, pageSize, externalUser, statuses,
                duplicate, memberIds, tags, channels, groupChats, genders, startTime, endTime);

        WeComExternalUsersResponse response = new WeComExternalUsersResponse();
        List<String> newMemberIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(departments)) {
            QueryMemberBuildSqlParam paramBuilder =
                    QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departments).build();
            List<WeComMemberMessageEntity> memberEntities = weComMemberMessageRepository.listByParam(paramBuilder);
            log.info("query member list from db. memberEntities={}", memberEntities);

            memberEntities.forEach(entity -> {
                newMemberIds.add(entity.getMemberId());
            });
        }

        if (CollectionUtils.isNotEmpty(memberIds)) {
            newMemberIds.addAll(memberIds);
        }
        QueryExternalUserBuildSqlParam param = QueryExternalUserBuildSqlParam.builder().pageNum(pageNum)
                .pageSize(pageSize)
                .corpId(corpId)
                .externalUserName(externalUser)
                .duplicate(duplicate)
                .memberIds(newMemberIds)
                .tags(tags)
                .channels(channels)
                .groupChats(groupChats)
                .genders(genders)
                .statuses(statuses)
                .startTime(StringUtils.isNotEmpty(startTime) ? (startTime + "00:00:00") : startTime)
                .endTime(StringUtils.isNotEmpty(endTime) ? (endTime + "23:59:59") : endTime).build();

        Integer count = weComRelationMemberExternalUserRepository.countByCnd(param);
        log.info("query external user list count. corpId={}, response={}", corpId, count);

        List<WeComRelationMemberExternalUserEntity> entities =
                weComRelationMemberExternalUserRepository.listByCnd(param);
        if (CollectionUtils.isEmpty(entities)) {
            return BaseResponse.success(response);
        }
        List<WeComExternalUsersResponse.ExternalUser> externalUsers = new ArrayList<>();
        List<String> members = new ArrayList<>();
        List<String> users = new ArrayList<>();
        entities.forEach(item -> {
            WeComExternalUsersResponse.ExternalUser user = new WeComExternalUsersResponse.ExternalUser();
            BeanUtils.copyProperties(item, user);
            members.add(item.getMemberId());
            users.add(item.getExternalUserId());
            log.info("tags from db. tags={}", item.getTags());
            List<WeComExternalUsersResponse.ExternalUserFollowUserTag> externalUserFollowUserTags = new ArrayList<>();
            if (StringUtils.isNotEmpty(item.getTags())) {
                List<String> tagList = Arrays.asList(item.getTags().split(","));
                List<WeComCorpTagEntity> tagEntities = weComCorpTagRepository.findByCorpIdAnAndTagIdIn(corpId, tagList);
                log.info("query corp tags detail from db. tagList={}, tagEntities={}", tagList, tagEntities);
                tagEntities.forEach(tagEntity -> {
                    WeComExternalUsersResponse.ExternalUserFollowUserTag userTag =
                            new WeComExternalUsersResponse.ExternalUserFollowUserTag();
                    userTag.setTagId(tagEntity.getTagId());
                    userTag.setTagName(tagEntity.getName());
                    externalUserFollowUserTags.add(userTag);
                });
            }
            user.setTags(externalUserFollowUserTags);
            List<String> groupChatIds = weComGroupChatMembersRepository.queryByUserId(corpId, item.getExternalUserId());
            if (CollectionUtils.isNotEmpty(groupChatIds)) {
                List<WeComGroupChatsEntity> groupChatsEntities = weComGroupChatsRepository.queryByChatIds(corpId,
                        groupChatIds);
                List<WeComExternalUsersResponse.GroupChat> chats = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(groupChatsEntities)) {
                    groupChatsEntities.forEach(n -> {
                        WeComExternalUsersResponse.GroupChat chat = new WeComExternalUsersResponse.GroupChat();
                        chat.setGroupChatName(n.getGroupChatName());
                        chat.setGroupChatId(n.getGroupChatId());
                        chats.add(chat);
                    });
                }
                user.setGroupChats(chats);
            }
            externalUsers.add(user);
        });

        List<WeComMemberMessageEntity> memberMessageEntities = weComMemberMessageRepository.queryNameByMemberIds(corpId
                , members);
        externalUsers.forEach(item -> {
            memberMessageEntities.forEach(entity -> {
                if (entity.getMemberId().equals(item.getMemberId())) {
                    item.setMemberName(entity.getMemberName());
                }
            });
        });

        response.setExternalUsers(externalUsers);
        response.setTotalCount(count);

        log.info("query external user list response. corpId={}, response={}", corpId, JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getMemberList(String projectId, Integer pageNum, Integer pageSize, String corpId,
                                      Long departmentId, String memberName) {
        log.info("query member list. projectId={}, corpId={}, pageNum={}, pageSize={}, id={}, memberName={}",
                projectId, corpId, pageNum, pageSize, departmentId, memberName);
        WeComMembersResponse response = new WeComMembersResponse();
        QueryMemberBuildSqlParam param =
                QueryMemberBuildSqlParam.builder().corpId(corpId).pageNum(pageNum).pageSize(pageSize).keyword(memberName).build();
        if (departmentId != null) {
            List<Long> departments = new ArrayList<>();
            //获取人群条件中的部门列表
            departments.add(departmentId);
            List<WeComDepartmentEntity> departmentEntities =
                    weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departments);
            while (CollectionUtils.isNotEmpty(departmentEntities)) {
                List<Long> departmentList = new ArrayList<>();
                departmentEntities.forEach(entity -> {
                    departmentList.add(entity.getDepartmentId());
                });
                log.info("user group find department list. departmentList={}", departmentList);
                departments.addAll(departmentList);
                departmentEntities =
                        weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentList);
            }
            param.setDepartments(departments);
        }
        List<WeComMemberMessageEntity> entities = weComMemberMessageRepository.listByParam(param);
        log.info("query member list from db. entities={}", entities);
        Integer count = weComMemberMessageRepository.countByParam(param);
        if (CollectionUtils.isEmpty(entities)) {
            return BaseResponse.success(response);
        }
        List<WeComMembersResponse.MemberMessage> members = new ArrayList<>();
        entities.forEach(entity -> {
            WeComMembersResponse.MemberMessage message = new WeComMembersResponse.MemberMessage();
            message.setMemberId(entity.getMemberId());
            message.setMemberName(entity.getMemberName());
            message.setIsMember(Boolean.TRUE);
            members.add(message);
        });
        response.setMembers(members);
        response.setTotalCount(count);
        log.info("query member list response . projectId={}, corpId={}, pageNum={}, pageSize={}, id={}, " +
                "memberName={}, response={}", projectId, corpId, pageNum, pageSize, departmentId, memberName, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getGroupChatOwners(String projectId, String corpId) {
        log.info("query group chat list. projectId={}, corpId={}", projectId, corpId);
        WeComGroupChatOwnersResponse response = new WeComGroupChatOwnersResponse();
        List<String> owners = weComGroupChatsRepository.queryOwnersByCorpId(corpId);
        List<WeComGroupChatOwnersResponse.GroupChatOwner> groupChatOwners = new ArrayList<>();
        owners.forEach(ownerId -> {
            WeComGroupChatOwnersResponse.GroupChatOwner owner = new WeComGroupChatOwnersResponse.GroupChatOwner();
            String name = weComMemberMessageRepository.queryNameByMemberId(corpId, ownerId);
            owner.setOwnerName(StringUtils.isEmpty(name) ? "匿名" : name);
            owner.setOwnerId(ownerId);
            groupChatOwners.add(owner);
        });
        response.setGroupChatOwners(groupChatOwners);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getGroupChatList(String projectId, String corpId, Integer pageNum, Integer pageSize,
                                         String groupChat,
                                         List<String> members) {
        log.info("query group chat list. projectId={}, corpId={}, pageNum={}, pageSize={}, groupChat={}, members={}",
                projectId, corpId, pageNum, pageSize, groupChat, members);
        WeComGroupChatsResponse response = new WeComGroupChatsResponse();
        Integer count = weComGroupChatsRepository.countByGroupChatNameAndOwner(corpId, groupChat, members);
        response.setTotalCount(count);

        List<WeComGroupChatsEntity> entities = weComGroupChatsRepository.getByGroupChatNameAndOwner(corpId
                , groupChat, members, (pageNum - 1) * pageSize, pageSize);
        log.info("query group chat list from db. entities={}", entities);
        List<WeComGroupChatsResponse.GroupChat> groupChats = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entities)) {
            for (WeComGroupChatsEntity item : entities) {
                WeComGroupChatsResponse.GroupChat msg = new WeComGroupChatsResponse.GroupChat();
                msg.setGroupChatId(item.getGroupChatId());
                msg.setGroupChatName(StringUtils.isEmpty(item.getGroupChatName()) ? "匿名" : item.getGroupChatName());
                msg.setOwnerId(item.getOwner());
                msg.setCreateTime(DateFormatUtils.formatDateTime(item.getChatCreateTime()));
                groupChats.add(msg);
            }

            groupChats.forEach(chat -> {
                String ownerId = chat.getOwnerId();
                String name = weComMemberMessageRepository.queryNameByMemberId(corpId, ownerId);
                chat.setOwnerName(StringUtils.isEmpty(name) ? "匿名" : name);
                Integer memberCount = weComGroupChatMembersRepository.countByGroupChatId(corpId, chat.getGroupChatId());
                chat.setCount(memberCount);
            });
        }
        response.setGroupChats(groupChats);
        log.info("return group chat list. response={}", response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getExternalUserDetail(String corpId, String externalUserId, Integer pageNum, Integer pageSize) {
        log.info("query external user detail. corpId={}, pageNum={}, pageSize={}, externalUserId={}", corpId, pageNum
                , pageSize, externalUserId);
        WeComExternalUserDetailResponse response = new WeComExternalUserDetailResponse();
        Integer count = weComRelationMemberExternalUserRepository.countByExternalUser(corpId, externalUserId);
        List<WeComExternalUserDetailResponse.WeComMember> members = new ArrayList<>();
        if (count > 0) {
            List<WeComRelationMemberExternalUserEntity> entities =
                    weComRelationMemberExternalUserRepository.queryMembersByExternalUser(corpId, externalUserId,
                            (pageNum - 1) * pageSize, pageSize);
            if (CollectionUtils.isNotEmpty(entities)) {
                entities.forEach(entity -> {
                    WeComExternalUserDetailResponse.WeComMember member =
                            new WeComExternalUserDetailResponse.WeComMember();
                    member.setMemberId(entity.getMemberId());
                    WeComMemberMessageEntity name = weComMemberMessageRepository.getMemberMessgeByMemberId(corpId,
                            entity.getMemberId());
                    member.setMemberName(name.getMemberName());
                    member.setAvatar(name.getAvatar());
                    member.setRemark(entity.getRemark());
                    member.setGender(entity.getGender());

                    List<WeComExternalUserDetailResponse.ExternalUserFollowUserTag> externalUserFollowUserTags =
                            new ArrayList<>();
                    if (StringUtils.isNotEmpty(entity.getTags())) {
                        List<String> tagList = Arrays.asList(entity.getTags().split(","));
                        List<WeComCorpTagEntity> tagEntities = weComCorpTagRepository.findByCorpIdAnAndTagIdIn(corpId
                                , tagList);
                        log.info("query corp tags detail from db. tagList={}, tagEntities={}", tagList, tagEntities);
                        tagEntities.forEach(tagEntity -> {
                            WeComExternalUserDetailResponse.ExternalUserFollowUserTag userTag =
                                    new WeComExternalUserDetailResponse.ExternalUserFollowUserTag();
                            userTag.setTagId(tagEntity.getTagId());
                            userTag.setTagName(tagEntity.getName());
                            externalUserFollowUserTags.add(userTag);
                        });
                    }
                    member.setTags(externalUserFollowUserTags);

                    members.add(member);
                });
            }
        }
        response.setTotalCount(count);
        response.setMembers(members);
        log.info("query external user detail response. corpId={}, pageNum={}, pageSize={}, " +
                        "groupChat={}, externalUserId={}, response={}", corpId, pageNum, pageSize, externalUserId,
                JsonUtils.toJSONString(response));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getGroupChatDetail(String corpId, String groupChat, Integer pageNum, Integer pageSize) {
        log.info("query group chat member list. corpId={}, pageNum={}, pageSize={}, groupChat={}, " +
                "members={}", corpId, pageNum, pageSize, groupChat);
        WeComGroupChatMembersResponse response = new WeComGroupChatMembersResponse();

        List<WeComGroupChatMembersResponse.GroupChatMember> groupChatMembers = new ArrayList<>();
        List<WeComGroupChatMembersEntity> entities = weComGroupChatMembersRepository.queryByGroupChatId(corpId,
                groupChat, (pageNum - 1) * pageSize, pageSize);
        log.info("query group chat member list from db. entities={}", entities);
        if (CollectionUtils.isEmpty(entities)) {
            response.setGroupChatMembers(groupChatMembers);
            return BaseResponse.success(response);
        }
        for (WeComGroupChatMembersEntity item : entities) {
            WeComGroupChatMembersResponse.GroupChatMember member = new WeComGroupChatMembersResponse.GroupChatMember();
            member.setName(item.getName());
            member.setNickName(item.getGroupNickname());
            member.setJoinScene(item.getJoinScene());
            member.setJoinTime(DateFormatUtils.formatDate(item.getJoinTime()));
            member.setType(item.getType());
            member.setUserId(item.getUserId());
            if (member.getType().equals(GROUP_CHAT_MEMBER_TYPE_EXTERNAL_USER)) {
                WeComRelationMemberExternalUserEntity entity =
                        weComRelationMemberExternalUserRepository.queryByExternalUser(corpId, item.getUserId());
                if (entity != null) {
                    member.setAvatar(entity.getAvatar());
                }
            }

            groupChatMembers.add(member);
        }
        Integer count = weComGroupChatMembersRepository.countByGroupChatId(corpId, groupChat);
        response.setTotalCount(count);
        response.setGroupChatMembers(groupChatMembers);
        response.setTotalCount(count);
        log.info("query group chat member list response. corpId={}, pageNum={}, pageSize={}, " +
                "groupChat={}, members={}, response={}", corpId, pageNum, pageSize, groupChat, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getGroupChatStatistic(String corpId, String groupChat) {
        log.info("query group chat statistic and detail. projectId={}, corpId={}", corpId, groupChat);

        WeComGroupChatStatisticResponse response = new WeComGroupChatStatisticResponse();
        WeComGroupChatsEntity entity = weComGroupChatsRepository.queryByChatId(corpId, groupChat);
        response.setGroupChatName(entity.getGroupChatName());
        response.setGroupChatId(entity.getGroupChatId());
        response.setCreateTime(DateFormatUtils.formatDate(entity.getChatCreateTime()));
        response.setNotice(entity.getNotice());
        response.setOwnerId(entity.getOwner());
        String name = weComMemberMessageRepository.queryNameByMemberId(corpId, entity.getOwner());
        response.setOwnerName(StringUtils.isEmpty(name) ? "匿名" : name);

        WeComGroupChatStatisticResponse.GroupChatStatistic statistic =
                new WeComGroupChatStatisticResponse.GroupChatStatistic();
        Integer memberCount = weComGroupChatMembersRepository.countByGroupChatId(corpId, groupChat);
        statistic.setTotalCount(memberCount);
        statistic.setTodayAddCount(0);
        statistic.setTodayExitCount(0);
        response.setStatistic(statistic);
        log.info("query group chat statistic and detail response. corpId={}response={}", corpId, groupChat, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getStatistic(String projectId, String corpId) {
        log.info("start to query constants statistic. corpId={}, projectId={}", corpId, projectId);
        WeComCustomerStatisticResponse response = new WeComCustomerStatisticResponse();
        Integer count = weComRelationMemberExternalUserRepository.countByCorpId(corpId);

        Integer lossCount = weComEventExternalUserRepository.countByCorpIdAndEventType(corpId,
                EVENT_TYPE_DEL_EXTERNAL_USER);

        Integer delCount = weComEventExternalUserRepository.countByCorpIdAndEventType(corpId,
                EVENT_TYPE_DEL_FOLLOW_USER);

        Integer newCount = weComEventExternalUserRepository.countByCorpIdAndEventType(corpId,
                EVENT_TYPE_ADD_EXTERNAL_USER);
        log.info("query external user statistic. count={}, lossCount={}, delCount={}, newCount={}", count, lossCount,
                delCount, newCount);
        delCount = (delCount == null ? 0 : delCount);
        lossCount = (lossCount == null ? 0 : lossCount);

        response.setExternalUserCount(count);
        response.setTodayLossExternalUserCount(delCount + lossCount);
        response.setTodayNewExternalUserCount(newCount == null ? 0 : newCount);

        WeComEventExternalUserEntity entity = weComEventExternalUserRepository.queryByCorpId(corpId);

        response.setExternalUserUpdateTime(entity == null ? DateUtil.now() :
                DateUtil.formatDateTime(entity.getEventTime()));
        Integer chatsCount = weComGroupChatsRepository.countByCorpId(corpId);

        Integer quitCount = weComEventGroupChatsRepository.countByCorpIdAndEventType(corpId, EVENT_TYPE_UPDATE,
                UPDATE_DETAIL_DEL_MEMBER);

        Integer joinCount = weComEventGroupChatsRepository.countByCorpIdAndEventType(corpId, EVENT_TYPE_UPDATE,
                UPDATE_DETAIL_ADD_MEMBER);
        log.info("query group chat statistic. chatsCount={}, quitCount={}, joinCount={}", chatsCount, quitCount,
                joinCount);
        response.setGroupChatsCount(chatsCount);
        response.setTodayJoinGroupCount(joinCount == null ? 0 : joinCount);
        response.setTodayQuitGroupCount(quitCount == null ? 0 : quitCount);

        WeComEventGroupChatsEntity groupChatsEntity = weComEventGroupChatsRepository.queryByCorpId(corpId);

        response.setGroupChatsUpdateTime(groupChatsEntity == null ? DateUtil.now() :
                DateUtil.formatDateTime(groupChatsEntity.getEventTime()));
        log.info("query constants statistic response. corpId={}, response={}", corpId, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getStatisticDetail(String projectId, String corpId, String dateType, Boolean holidaySwitch,
                                           String metricType, String startTime, String endTime) {
        WeComCustomerStatisticDetailResponse response = new WeComCustomerStatisticDetailResponse();
        log.info("start to query constants statistic list detail. corpId={}, dateType={}, holidaySwitch={}, " +
                        "metricType={}, startTime={}, endTime={}", corpId, dateType, holidaySwitch, metricType,
                startTime, endTime);
        DateTime dtStartTime, dtEndTime;
        String strStartTime, strEndTime;
        if (StringUtils.isBlank(startTime)) {
            dtStartTime = DateUtil.lastWeek();
            dtStartTime = DateUtil.offsetDay(dtStartTime, 1);
        } else {
            dtStartTime = DateUtil.parse(startTime);
        }

        if (StringUtils.isBlank(endTime)) {
            dtEndTime = DateUtil.parse(DateUtil.now());
        } else {
            dtEndTime = DateUtil.parse(endTime);
        }
        Long currentTime = System.currentTimeMillis();
        if (dtStartTime.getTime() > currentTime || dtEndTime.getTime() > currentTime ||
                DateUtil.beginOfDay(dtStartTime).getTime() > DateUtil.beginOfDay(dtEndTime).getTime()) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_DATE_TIME_PARAM);
        }
        strStartTime = DateUtil.formatDate(dtStartTime);
        strEndTime = DateUtil.formatDate(dtEndTime);
        List<String> dateList = new ArrayList<>();

        while (!DateUtil.formatDate(dtStartTime).equals(DateUtil.formatDate(dtEndTime))) {
            String strTime = DateUtil.formatDate(dtStartTime);
            log.info("format date. strTime={}", strTime);
            dtStartTime = DateUtil.offsetDay(dtStartTime, 1);
            dateList.add(strTime);
        }
        dateList.add(DateUtil.formatDate(dtEndTime));
        List<WeComCustomerStatisticDetailResponse.ExternalUserStatistic> statistics = new ArrayList<>();
        dateList.forEach(date -> {
            WeComCustomerStatisticDetailResponse.ExternalUserStatistic statistic =
                    new WeComCustomerStatisticDetailResponse.ExternalUserStatistic();
            Integer count = 0;
            if (metricType.equals("add")) {
                count = weComEventExternalUserRepository.countByCorpIdAndEventTypeAndEventTime(corpId,
                        EVENT_TYPE_ADD_EXTERNAL_USER, date + "%");
            } else if (metricType.equals("delete")) {
                count = weComEventExternalUserRepository.countByCorpIdAndEventTypeAndEventTime(corpId,
                        EVENT_TYPE_DEL_EXTERNAL_USER, date + "%");
                count = (count == null ? 0 :
                        count) + weComEventExternalUserRepository.countByCorpIdAndEventTypeAndEventTime(corpId,
                        EVENT_TYPE_DEL_FOLLOW_USER, date + "%");
            } else {
                count = weComRelationMemberExternalUserRepository.countByCorpIdAndAddTime(corpId, date + " " +
                        "23:59:59");
            }
            statistic.setTodayCount(count == null ? 0 : count);
            statistic.setTodayTime(date);
            statistics.add(statistic);
        });
        response.setDetail(statistics);
        log.info("query constants statistic list response. corpId={}, response={}", corpId, response);
        return BaseResponse.success(response);
    }
}
