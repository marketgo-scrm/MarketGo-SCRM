package com.easy.marketgo.core.service.contacts;

import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.model.bo.WeComLiveCodeMember;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
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
 * @data : 12/29/22 7:14 PM
 * Describe:
 */

@Slf4j
@Component
public class WeComMemberService {

    @Autowired
    private WeComDepartmentRepository weComDepartmentRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    public List<Long> getSubDepartmentList(String corpId, List<Long> departmentIds) {
        List<Long> departments = new ArrayList<>();
        List<WeComDepartmentEntity> departmentEntities =
                weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentIds);
        while (CollectionUtils.isNotEmpty(departmentEntities)) {
            List<Long> departmentList = new ArrayList<>();
            departmentEntities.forEach(entity -> {
                departmentList.add(entity.getDepartmentId());
            });
            log.info("find sub department list. departmentList={}", departmentList);
            departments.addAll(departmentList);
            departmentEntities =
                    weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentList);
        }
        departments.addAll(departmentIds);
        return departments;
    }

    public List<String> getMembersForDepartment(String corpId, List<Long> departments) {
        List<String> memberList = new ArrayList<>();
        //计算员工的数量
        QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departments).build();
        List<WeComMemberMessageEntity> entities =
                weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
        if (CollectionUtils.isNotEmpty(entities)) {
            memberList.addAll(entities.stream().map(WeComMemberMessageEntity::getMemberId).collect(Collectors.toList()));
           ;
        }
        return memberList;
    }

    public List<String> getMemberList(String corpId, WeComUserGroupAudienceRule weComUserGroupAudienceRule) {
        List<Long> departmentList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        if (!weComUserGroupAudienceRule.getMembers().getIsAll()) {
            if (CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getMembers().getUsers())) {
                weComUserGroupAudienceRule.getMembers().getUsers().forEach(user -> {
                    memberList.add(user.getMemberId());
                });
            }

            if (CollectionUtils.isNotEmpty(weComUserGroupAudienceRule.getMembers().getDepartments())) {
                weComUserGroupAudienceRule.getMembers().getDepartments().forEach(department -> {
                    departmentList.add(department.getId());
                });

                List<WeComDepartmentEntity> departmentEntities =
                        weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentList);
                while (CollectionUtils.isNotEmpty(departmentEntities)) {
                    List<Long> tempDepartmentList = new ArrayList<>();
                    departmentEntities.forEach(departmentEntity -> {
                        tempDepartmentList.add(departmentEntity.getDepartmentId());
                    });
                    log.info("find department list. tempDepartmentList={}", tempDepartmentList);
                    departmentList.addAll(tempDepartmentList);
                    departmentEntities =
                            weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, tempDepartmentList);
                }

                QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                        QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departmentList).build();
                List<WeComMemberMessageEntity> memberTmp =
                        weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
                log.info("query user group for member from db count. memberTmp={}", memberTmp.size());
                if (CollectionUtils.isNotEmpty(memberTmp)) {
                    log.info("user group for member estimate result. memberCount={}", memberTmp.size());
                    memberList.addAll(memberTmp.stream().map(WeComMemberMessageEntity::getMemberId).collect(Collectors.toList()));
                }
            }
        }
        return memberList.stream().distinct().collect(Collectors.toList());
    }

    public List<String> getMemberList(String corpId, String members) {
        if (StringUtils.isEmpty(members)) return null;
        WeComLiveCodeMember memberMsg = JsonUtils.toObject(members, WeComLiveCodeMember.class);
        List<Long> departmentList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(memberMsg.getUsers())) {
            memberList =
                    memberMsg.getUsers().stream().map(WeComLiveCodeMember.UserMessage::getMemberId).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(memberMsg.getDepartments())) {
            departmentList =
                    memberMsg.getDepartments().stream().map(WeComLiveCodeMember.DepartmentMessage::getId).collect(Collectors.toList());

            List<WeComDepartmentEntity> departmentEntities =
                    weComDepartmentRepository.findByCorpIdAndParentIdIn(corpId, departmentList);
            while (CollectionUtils.isNotEmpty(departmentEntities)) {
                List<Long> tempDepartmentList =
                        departmentEntities.stream().map(WeComDepartmentEntity::getDepartmentId).collect(Collectors.toList());

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
                memberList.addAll(memberTmp.stream().map(WeComMemberMessageEntity::getMemberId).collect(Collectors.toList()));
            }
        }
        return memberList.stream().distinct().collect(Collectors.toList());
    }
}
