package com.easy.marketgo.web.service.user.impl;

import cn.hutool.core.util.ArrayUtil;
import com.easy.marketgo.core.entity.WeComSysUserEntity;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.WeComSysBaseRoleEntity;
import com.easy.marketgo.core.entity.WeComSysCorpUserRoleLinkEntity;
import com.easy.marketgo.core.repository.user.WeComSysUserRepository;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.WeComSysBaseRoleRepository;
import com.easy.marketgo.core.repository.wecom.WeComSysCropUserRoleLinkRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.web.model.request.user.OrganizationalStructureQueryRequest;
import com.easy.marketgo.web.model.request.user.OrganizationalStructureRequest;
import com.easy.marketgo.web.model.response.user.OrganizationalStructureQueryResponse;
import com.easy.marketgo.web.model.response.user.OrganizationalStructureResponse;
import com.easy.marketgo.web.service.user.IOrganizationalStructureService;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-17 20:06:34
 * @description : OrganizationalStructureServiceImpl.java
 */
@Log4j2
@Component
public class OrganizationalStructureServiceImpl implements IOrganizationalStructureService {

    @Autowired
    private WeComDepartmentRepository departmentRepository;
    @Autowired
    private WeComMemberMessageRepository memberMessageRepository;
    @Autowired
    private WeComSysCropUserRoleLinkRepository userRoleLinkRepository;
    @Autowired
    private WeComSysBaseRoleRepository baseRoleRepository;
    @Autowired
    private WeComRelationMemberExternalUserRepository relationMemberExternalUserRepository;
    @Autowired
    private WeComSysUserRepository sysUserRepository;

    @Override
    public OrganizationalStructureResponse fetchStructures(OrganizationalStructureRequest request) {

        OrganizationalStructureResponse response = OrganizationalStructureResponse.builder().build();

        List<WeComDepartmentEntity> departmentEntities = departmentRepository.findByCorpId(request.getCorpId());
        if (CollectionUtils.isEmpty(departmentEntities)) {
            return response;
        }
        List<OrganizationalStructureResponse.StructureInfo> structureInfoList =
                departmentEntities.stream()
                        .map(d -> OrganizationalStructureResponse.
                                StructureInfo.builder()
                                .parentDepartmentId(d.getParentId() + "")
                                .name(d.getDepartmentName())
                                .id(d.getDepartmentId() + "")
                                .build()).collect(Collectors.toList());

        StructureInfoTree structureInfoTree = new StructureInfoTree(structureInfoList);
        response.setStructures(structureInfoTree.buildTree("0"));
        return response;
    }

    @Override
    public OrganizationalStructureQueryResponse fetchStructures(OrganizationalStructureQueryRequest request) {

        OrganizationalStructureQueryResponse response = OrganizationalStructureQueryResponse.builder().build();
        List<WeComDepartmentEntity> departmentEntities = departmentRepository.findByCorpId(request.getCorpId());
        if (CollectionUtils.isEmpty(departmentEntities)) {
            return response;
        }

        Map<String, List<WeComDepartmentEntity>> leaderDepartmentMap = departmentEntities
                .stream().filter(d -> StringUtils.isNotEmpty(d.getDepartmentLeader()))
                .collect(Collectors.groupingBy(WeComDepartmentEntity::getDepartmentLeader));

        Map<String, WeComDepartmentEntity> departmentMap = departmentEntities
                .stream()
                .collect(Collectors.toMap(d -> d.getDepartmentId() + "", Function.identity()));

        List<OrganizationalStructureResponse.StructureInfo> structureInfoList =
                departmentEntities.stream()
                        .map(d -> OrganizationalStructureResponse.
                                StructureInfo.builder()
                                .parentDepartmentId(d.getParentId() + "")
                                .name(d.getDepartmentName())
                                .id(d.getDepartmentId() + "")
                                .build()).collect(Collectors.toList());
        StructureInfoTree structureInfoTree = new StructureInfoTree(structureInfoList);
        List<OrganizationalStructureResponse.StructureInfo> structureInfoListTree =
                structureInfoTree.buildTree(request.getDepartmentId());
        List<OrganizationalStructureResponse.StructureInfo> structureInfos =
                structureInfoTree.getAllNode(structureInfoListTree,
                        request.getDepartmentId());
        if (CollectionUtils.isEmpty(structureInfos)) {
            return response;
        }
        List<String> searchDepartmentIds =
                structureInfos.stream().map(OrganizationalStructureResponse.StructureInfo::getId)
                        .collect(Collectors.toList());


        List<WeComMemberMessageEntity> members =
                StringUtils.isEmpty(request.getSearchMemberName()) ?
                        memberMessageRepository.findByCorpId(request.getCorpId())
                        : memberMessageRepository.findByCorpIdAndMemberNameLike(request.getCorpId(),
                        request.getSearchMemberName());

        members = members.stream().filter(m -> {
            String[] split = StringUtils.split(m.getDepartment(), ",");
            String[] strings = ArrayUtil.toArray(searchDepartmentIds, String.class);
            return ArrayUtil.containsAny(strings, split);

        }).collect(Collectors.toList());

        List<String> memberIds =
                CollectionUtils.isEmpty(members) ? Collections.emptyList() :
                        members.stream().map(WeComMemberMessageEntity::getMemberId).collect(Collectors.toList());

        List<WeComSysCorpUserRoleLinkEntity> linkEntities =
                userRoleLinkRepository.findByCorpIdAndProjectUuidAndMemberIdIn(request.getCorpId(),
                        request.getProjectUuid(), memberIds);
        List<WeComSysBaseRoleEntity> baseRoleEntities = baseRoleRepository.findByProjectUuid(request.getProjectUuid());

        Map<String, WeComSysBaseRoleEntity> baseRoleEntityMap =
                CollectionUtils.isEmpty(baseRoleEntities)
                        ? Collections.emptyMap() :
                        baseRoleEntities
                                .stream()
                                .filter(b -> {
                                    if (StringUtils.isNotEmpty(request.getRoleCode())) {
                                        return StringUtils.equals(request.getRoleCode(), b.getCode());
                                    } else {
                                        return true;
                                    }

                                })
                                .collect(Collectors.toMap(WeComSysBaseRoleEntity::getUuid, Function.identity()));


        Map<String, WeComSysCorpUserRoleLinkEntity> memberRoleMap = CollectionUtils.isEmpty(linkEntities)
                ? Collections.emptyMap() :
                linkEntities
                        .stream()
                        .collect(Collectors.toMap(WeComSysCorpUserRoleLinkEntity::getMemberId, Function.identity()));


        List<OrganizationalStructureQueryResponse.OrganizationalStructureMember> structureMemberList =
                members.stream()
                        .map(m -> {
                            String roleUuid = memberRoleMap
                                    .getOrDefault(m.getMemberId(), new WeComSysCorpUserRoleLinkEntity()).getRoleUuid();
                            WeComSysBaseRoleEntity baseRole4Member = baseRoleEntityMap.getOrDefault(roleUuid,
                                    new WeComSysBaseRoleEntity());
                            String[] split = StringUtils.split(m.getDepartment(), ",");
                            OrganizationalStructureQueryResponse.OrganizationalStructureMember member =
                                    new OrganizationalStructureQueryResponse.OrganizationalStructureMember();
                            member.setMemberId(m.getMemberId());
                            member.setMemberName(m.getMemberName());
                            member.setCorpId(m.getCorpId());
                            member.setDepartmentId(m.getDepartment());
                            member.setDepartmentName(Arrays
                                    .stream(split)
                                    .map(departmentId -> departmentMap.getOrDefault(departmentId,
                                            new WeComDepartmentEntity()).getDepartmentName())
                                    .filter(StringUtils::isNotEmpty)
                                    .collect(Collectors.joining(",")));


                            member.setLeaderDepartmentId(
                                    leaderDepartmentMap.getOrDefault(m.getMemberId(), Lists.newArrayList()).stream()
                                            .map(department -> department.getDepartmentId() + "")
                                            .filter(StringUtils::isNotEmpty)
                                            .collect(Collectors.joining(","))
                            );
                            member.setLeaderDepartmentName(
                                    leaderDepartmentMap.getOrDefault(m.getMemberId(), Lists.newArrayList()).stream()
                                            .map(department -> department.getDepartmentName() + "")
                                            .filter(StringUtils::isNotEmpty)
                                            .collect(Collectors.joining(",")));
                            member.setThumbAvatar(m.getThumbAvatar());
                            member.setAvatar(m.getAvatar());
                            member.setRoleCode(baseRole4Member.getCode());
                            member.setRoleDesc(baseRole4Member.getDesc());
                            member.setMobile(StringUtils.isBlank(m.getMobile()) ? "" : m.getMobile());
                            WeComSysUserEntity entity = sysUserRepository.queryByUserName(m.getMobile());
                            if (entity != null) {
                                member.setAuthStatus(entity.getAuthStatus());
                            } else {
                                member.setAuthStatus(Boolean.FALSE);
                            }
                            int count =
                                    relationMemberExternalUserRepository.countByCorpIdAndMemberId(request.getCorpId()
                                            , m.getMemberId());
                            member.setExternalUserCount(count + "");
                            member.setAuthorizationStatus(StringUtils.isNotEmpty(member.getRoleCode()));
                            member.setRoleUuid(baseRole4Member.getUuid());
                            return member;

                        }).collect(Collectors.toList());


        response.setMembers(structureMemberList);
        return response;
    }


    public static class StructureInfoTree {

        private List<OrganizationalStructureResponse.StructureInfo> structureInfoList = Lists.newArrayList();

        public StructureInfoTree(List<OrganizationalStructureResponse.StructureInfo> structureInfoList) {

            this.structureInfoList = structureInfoList;
        }

        public List<OrganizationalStructureResponse.StructureInfo> getAllNode(List<OrganizationalStructureResponse.StructureInfo> structureInfoListTree,
                                                                              String parentDepartmentId) {

            List<OrganizationalStructureResponse.StructureInfo> structureInfoList = Lists.newArrayList();
            recursiveGetNodes(structureInfoList, structureInfoListTree, parentDepartmentId);
            return structureInfoList;
        }

        private void recursiveGetNodes(List<OrganizationalStructureResponse.StructureInfo> infos,
                                       List<OrganizationalStructureResponse.StructureInfo> structureInfoListTree
                , String parentDepartmentId) {

            for (OrganizationalStructureResponse.StructureInfo structureInfo : structureInfoListTree) {
                if (StringUtils.equals(structureInfo.getParentDepartmentId(), parentDepartmentId)) {
                    infos.add(structureInfo);
                    recursiveGetNodes(infos, structureInfo.getChildren(), structureInfo.getId());
                } else if (StringUtils.equals(structureInfo.getId(), parentDepartmentId)) {
                    infos.add(structureInfo);
                }

            }

        }


        //建立树形结构
        public List<OrganizationalStructureResponse.StructureInfo> buildTree(String parentDepartmentId) {

            List<OrganizationalStructureResponse.StructureInfo> structureInfoList = Lists.newArrayList();
            for (OrganizationalStructureResponse.StructureInfo structureInfo : getRootNode(parentDepartmentId)) {
                structureInfo = buildChildTree(structureInfo);
                structureInfoList.add(structureInfo);
            }
            return structureInfoList;
        }

        //递归，建立子树形结构
        public OrganizationalStructureResponse.StructureInfo buildChildTree(OrganizationalStructureResponse.StructureInfo pNode) {

            List<OrganizationalStructureResponse.StructureInfo> childStructureInfo = Lists.newArrayList();
            for (OrganizationalStructureResponse.StructureInfo structInfoNode : structureInfoList) {
                if (structInfoNode.getParentDepartmentId().equals(pNode.getId())) {
                    childStructureInfo.add(buildChildTree(structInfoNode));
                }
            }
            pNode.setChildren(childStructureInfo);
            return pNode;
        }

        //获取根节点(获取所有的父节点)
        public List<OrganizationalStructureResponse.StructureInfo> getRootNode(String parentDepartmentId) {

            List<OrganizationalStructureResponse.StructureInfo> rootStructureInfo = Lists.newArrayList();
            for (OrganizationalStructureResponse.StructureInfo structureInfo : structureInfoList) {
                if (StringUtils.equals(structureInfo.getParentDepartmentId(), parentDepartmentId)) {
                    rootStructureInfo.add(structureInfo);
                }
            }
            if (CollectionUtils.isEmpty(rootStructureInfo)) {
                List<OrganizationalStructureResponse.StructureInfo> leaf = structureInfoList
                        .stream()
                        .filter(info -> StringUtils.equals(info.getId(), parentDepartmentId))
                        .collect(Collectors.toList());
                rootStructureInfo.addAll(leaf);
            }
            return rootStructureInfo;
        }

    }


}
