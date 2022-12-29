package com.easy.marketgo.core.service.contacts;

import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.model.bo.QueryMemberBuildSqlParam;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
}
