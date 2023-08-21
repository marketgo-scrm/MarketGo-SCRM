package com.easy.marketgo.biz.service.wecom.livecode;

import cn.hutool.core.date.DateTime;
import com.easy.marketgo.api.model.request.contactway.WeComContactWayClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComContactWayRpcService;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeMembersEntity;
import com.easy.marketgo.core.entity.channellive.WeComChannelLiveCodeStatisticEntity;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.model.bo.WeComLiveCodeMember;
import com.easy.marketgo.core.model.callback.WeComExternalUserMsg;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeMembersRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeRepository;
import com.easy.marketgo.core.repository.wecom.channelLivecode.WeComChannelLiveCodeStatisticRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-20 15:21:19
 * @description : ChannelLiveCodeRefreshService.java
 */
@Slf4j
@Component
public class ChannelLiveCodeRefreshService {

    @Autowired
    private WeComChannelLiveCodeRepository weComChannelLiveCodeRepository;

    @Autowired
    private WeComChannelLiveCodeMembersRepository weComChannelLiveCodeMembersRepository;

    @Autowired
    private WeComChannelLiveCodeStatisticRepository statisticRepository;

    @Resource
    private WeComContactWayRpcService weComContactWayRpcService;

    @Autowired
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    public void refresh() {
        Iterator<WeComChannelLiveCodeEntity> liveCodes = weComChannelLiveCodeRepository.findAll().iterator();
        while (liveCodes.hasNext()) {
            WeComChannelLiveCodeEntity liveCode = liveCodes.next();
            refreshMember(liveCode);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void refreshMember(WeComChannelLiveCodeEntity liveCode) {
        log.info("start to refresh live code members. liveCode={}", liveCode);
        try {
            if (StringUtils.isBlank(liveCode.getMembers())) {
                return;
            }
            WeComLiveCodeMember memberList = JsonUtils.toObject(liveCode.getMembers(), WeComLiveCodeMember.class);
            if (memberList == null || CollectionUtils.isEmpty(memberList.getDepartments())) {
                return;
            }
            getMemberList(liveCode.getCorpId(), liveCode.getUuid(), memberList);
            refreshOnlineMember(liveCode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getMemberList(String corpId, String uuid, WeComLiveCodeMember members) {

        List<Long> departmentList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(members.getDepartments())) {
            members.getDepartments().forEach(department -> {
                departmentList.add(department.getId());
            });

            List<WeComDepartmentEntity> departmentEntities =
                    weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentList);
            while (CollectionUtils.isNotEmpty(departmentEntities)) {
                List<Long> tempDepartmentList = new ArrayList<>();
                departmentEntities.forEach(departmentEntity -> {
                    tempDepartmentList.add(departmentEntity.getDepartmentId());
                });
                log.info("find department list for channel live code. tempDepartmentList={}", tempDepartmentList);
                departmentList.addAll(tempDepartmentList);
                departmentEntities = weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, tempDepartmentList);
            }

            QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                    QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departmentList).build();
            List<WeComMemberMessageEntity> memberTmp =
                    weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
            log.info("query members from db count. memberTmp={}", memberTmp.size());
            if (CollectionUtils.isNotEmpty(memberTmp)) {
                log.info("query members result for channel live code. memberCount={}", memberTmp.size());

                memberTmp.forEach(item -> {
                    List<WeComChannelLiveCodeMembersEntity> entities =
                            weComChannelLiveCodeMembersRepository.queryByMemberId(uuid, item.getMemberId());
                    if (CollectionUtils.isEmpty(entities)) {
                        WeComChannelLiveCodeMembersEntity entity = new WeComChannelLiveCodeMembersEntity();
                        entity.setChannelLiveCodeUuid(uuid);
                        entity.setMemberId(item.getMemberId());
                        entity.setMemberName(item.getMemberName());
                        entity.setIsBackup(Boolean.FALSE);
                        entity.setOnlineStatus(Boolean.TRUE);
                        weComChannelLiveCodeMembersRepository.save(entity);
                    }
                });
            }
        }
    }

    public void refreshOnlineMember(WeComChannelLiveCodeEntity liveCode) {
        log.info("start to refresh live code members. liveCode={}", liveCode);
        try {
            weComChannelLiveCodeMembersRepository.updateOnlineStatusByUuidAndIsBackup(liveCode.getUuid(),
                    Boolean.TRUE, Boolean.FALSE);
            weComChannelLiveCodeMembersRepository.updateOnlineStatusByUuidAndIsBackup(liveCode.getUuid(),
                    Boolean.FALSE, Boolean.TRUE);
            RefreshWeComLiveCode(liveCode.getCorpId(), liveCode.getUuid());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void refreshByCallBackMessage(WeComExternalUserMsg message, String liveCodeUuid) {

        List<WeComChannelLiveCodeMembersEntity> liveCodeMembers =
                weComChannelLiveCodeMembersRepository.queryByMemberId(liveCodeUuid, message.getMemberId());
        WeComChannelLiveCodeMembersEntity liveCodeMember = liveCodeMembers
                .stream()
                .filter(mem -> !mem.getIsBackup())
                .findAny()
                .orElse(null);
        log.info("query member message from db. liveCodeUuid={}, member={}, entity={}", liveCodeUuid,
                message.getMemberId(), liveCodeMember);
        if (Objects.isNull(liveCodeMember) || Objects.isNull(liveCodeMember.getAddLimitCount())) {
            return;
        }

        WeComChannelLiveCodeStatisticEntity statistic =
                statisticRepository.queryByMemberAndEventDate(liveCodeUuid, message.getMemberId(),
                        DateTime.of(message.getCreateTime() * 1000).toJdkDate());

        if (Objects.nonNull(statistic)
                && statistic.getDailyIncreasedExtUserCount() >= liveCodeMember.getAddLimitCount()) {
            liveCodeMember.setOnlineStatus(false);
            weComChannelLiveCodeMembersRepository.save(liveCodeMember);
            log.info("set member to offline. liveCodeMember={}", liveCodeMember);

            RefreshWeComLiveCode(message.getCorpId(), liveCodeUuid);
        }

    }

    private void RefreshWeComLiveCode(String corpId, String liveCodeUuid) {
        WeComChannelLiveCodeEntity entity = weComChannelLiveCodeRepository.queryByCorpAndUuid(corpId, liveCodeUuid);
        if (Objects.isNull(entity)) {
            return;
        }

        WeComAgentMessageEntity weComAgentMessageEntity =
                weComAgentMessageRepository.getWeComAgentByCorp(entity.getProjectUuid(), corpId);
        String agentId = weComAgentMessageEntity != null ? weComAgentMessageEntity.getAgentId() : entity.getAgentId();
        List<WeComChannelLiveCodeMembersEntity> liveCodeMembers =
                weComChannelLiveCodeMembersRepository.queryByLiveCodeUuid(liveCodeUuid);

        List<String> onlineMembers = new ArrayList<>();
        List<String> backupMembers = new ArrayList<>();
        liveCodeMembers.forEach(item -> {
            if (item.getOnlineStatus()) {
                onlineMembers.add(item.getMemberId());
            } else if (item.getIsBackup() && !item.getOnlineStatus()) {
                backupMembers.add(item.getMemberId());
            }
        });
        if (CollectionUtils.isEmpty(onlineMembers)) {
            weComChannelLiveCodeMembersRepository.updateOnlineStatusByUuidAndIsBackup(liveCodeUuid,
                    Boolean.TRUE, Boolean.TRUE);
        }
        WeComContactWayClientRequest clientRequest = new WeComContactWayClientRequest();
        clientRequest.setConfigId(entity.getConfigId());
        clientRequest.setType(2);
        clientRequest.setScene(2);
        clientRequest.setStyle(0);
        clientRequest.setSkipVerify(entity.getSkipVerify());
        clientRequest.setState(entity.getState());

        if (CollectionUtils.isNotEmpty(onlineMembers) || CollectionUtils.isNotEmpty(backupMembers)) {
            clientRequest.setUser(CollectionUtils.isNotEmpty(onlineMembers) ? onlineMembers : backupMembers);
            clientRequest.setCorpId(corpId);
            clientRequest.setAgentId(agentId);
            RpcResponse rpcResponse = weComContactWayRpcService.updateContactWay(clientRequest);
            log.info("update live code config to weCom. clientRequest={}, rpcResponse={}", clientRequest, rpcResponse);
        } else {
            log.info("live code all member add count is max. entity={}", entity);
//            refresh(liveCode);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void statisticRefresh(WeComXmlMessage weComXmlMessage) {

        String corpId = weComXmlMessage.getToUserName();
        String userId = weComXmlMessage.getUserID();
        List<WeComChannelLiveCodeEntity> liveCodes = weComChannelLiveCodeRepository.queryByCorp(corpId);
        if (CollectionUtils.isEmpty(liveCodes)) {
            return;
        }
        for (WeComChannelLiveCodeEntity liveCode : liveCodes) {

            List<WeComChannelLiveCodeMembersEntity> members =
                    weComChannelLiveCodeMembersRepository.queryByMemberId(liveCode.getUuid(), userId);
            if (CollectionUtils.isEmpty(members)) {
                continue;
            }

            WeComChannelLiveCodeStatisticEntity statistic =
                    statisticRepository.queryByMemberAndEventDate(liveCode.getUuid(), userId,
                            Objects.isNull(weComXmlMessage.getCreateTime()) ? new Date() :
                                    DateTime
                                            .of(weComXmlMessage.getCreateTime() * 1000)
                                            .toJdkDate()
                    );

            if (Objects.isNull(statistic)) {
                statistic = new WeComChannelLiveCodeStatisticEntity();
                statistic.setCorpId(corpId);
                statistic.setChannelLiveCodeUuid(liveCode.getUuid());
                statistic.setDailyIncreasedExtUserCount(0);
                statistic.setDailyDecreaseExtUserCount(1);
                statistic.setMemberId(userId);
                statistic.setEventDate(DateTime
                        .of(weComXmlMessage.getCreateTime() * 1000)
                        .toJdkDate());
                statisticRepository.save(statistic);
            } else {
                statistic.setDailyDecreaseExtUserCount(statistic.getDailyDecreaseExtUserCount() + 1);
                statistic.setEventDate(
                        Objects.isNull(weComXmlMessage.getCreateTime()) ? new Date() :
                                DateTime
                                        .of(weComXmlMessage.getCreateTime() * 1000)
                                        .toJdkDate());
                statisticRepository.save(statistic);
            }
        }
    }

}
