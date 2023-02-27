package com.easy.marketgo.core.service.contacts;

import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.model.usergroup.WeComUserGroupAudienceRule;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
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

    public List<Long> getSubDepartmentList(List<Long> departmentIds) {
        List<Long> departments = new ArrayList<>();
        List<WeComDepartmentEntity> departmentEntities =
                weComDepartmentRepository.findByParentIdIn(departmentIds);
        while (CollectionUtils.isNotEmpty(departmentEntities)) {
            List<Long> departmentList = new ArrayList<>();
            departmentEntities.forEach(entity -> {
                departmentList.add(entity.getDepartmentId());
            });
            log.info("find sub department list. departmentList={}", departmentList);
            departments.addAll(departmentList);
            departmentEntities =
                    weComDepartmentRepository.findByParentIdIn(departmentList);
        }
        departments.addAll(departmentIds);
        return departments;
    }

    public List<String> getMembers(String corpId, List<Long> departments) {
        List<String> memberList = new ArrayList<>();
        //计算员工的数量
        QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departments).build();
        List<WeComMemberMessageEntity> entities =
                weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
        if (CollectionUtils.isNotEmpty(entities)) {
            entities.forEach(entity -> {
                memberList.add(entity.getMemberId());
            });
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
                        weComDepartmentRepository.findByParentIdIn(departmentList);
                while (CollectionUtils.isNotEmpty(departmentEntities)) {
                    List<Long> tempDepartmentList = new ArrayList<>();
                    departmentEntities.forEach(departmentEntity -> {
                        tempDepartmentList.add(departmentEntity.getDepartmentId());
                    });
                    log.info("find department list. tempDepartmentList={}", tempDepartmentList);
                    departmentList.addAll(tempDepartmentList);
                    departmentEntities =
                            weComDepartmentRepository.findByParentIdIn(tempDepartmentList);
                }

                QueryMemberBuildSqlParam queryMemberBuildSqlParam =
                        QueryMemberBuildSqlParam.builder().corpId(corpId).departments(departmentList).build();
                List<WeComMemberMessageEntity> memberTmp =
                        weComMemberMessageRepository.listByParam(queryMemberBuildSqlParam);
                log.info("query user group for member from db count. memberTmp={}", memberTmp.size());
                if (CollectionUtils.isNotEmpty(memberTmp)) {
                    log.info("user group for member estimate result. memberCount={}", memberTmp.size());
                    memberTmp.forEach(item -> {
                        memberList.add(item.getMemberId());
                    });
                }
            }
        }
        return memberList.stream().distinct().collect(Collectors.toList());
    }
}
