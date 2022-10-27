package com.easy.marketgo.web.service.user.impl;

import com.easy.marketgo.common.enums.PermissionsEnum;
import com.easy.marketgo.core.entity.WeComSysBasePermissionsEntity;
import com.easy.marketgo.core.entity.WeComSysCorpRolePermissionsLinkEntity;
import com.easy.marketgo.core.repository.wecom.WeComSysBasePermissionsRepository;
import com.easy.marketgo.core.repository.wecom.WeComSysCropRolePermissionsLinkRepository;
import com.easy.marketgo.web.client.ClientRequestContextHolder;
import com.easy.marketgo.web.model.request.user.RolePermissionsAuthorizationRequest;
import com.easy.marketgo.web.model.request.user.RolePermissionsRequest;
import com.easy.marketgo.web.model.response.user.RolePermissionsResponse;
import com.easy.marketgo.web.service.user.IUserPermissionsService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-17 17:17:27
 * @description : UserPermissionsService.java
 */
@Component
public class UserPermissionsServiceImpl implements IUserPermissionsService {

    @Autowired
    private WeComSysCropRolePermissionsLinkRepository rolePermissionsLinkRepository;
    @Autowired
    private WeComSysBasePermissionsRepository basePermissionsRepository;

    @Override
    public List<RolePermissionsResponse> fetchUserPermissions(RolePermissionsRequest request) {

        String userName = ClientRequestContextHolder.current().getUserName();
        List<WeComSysCorpRolePermissionsLinkEntity> links = rolePermissionsLinkRepository.findByCorpIdAndRoleUuid(request.getCorpId(), request.getRoleUuid());
        List<WeComSysBasePermissionsEntity> permissionsEntities = basePermissionsRepository.findByProjectUuid(request.getProjectUuid());
        if (CollectionUtils.isEmpty(links) || StringUtils.isEmpty(request.getRoleUuid())) {
            if (CollectionUtils.isEmpty(permissionsEntities)) {
                return Lists.newArrayList();
            }
            List<RolePermissionsResponse> permissionsResponses = permissionsEntities
                    .stream()
                    .map(p -> RolePermissionsResponse
                            .builder()
                            .permissionsUuid(p.getUuid())
                            .name(p.getName())
                            .title(p.getTitle())
                            .code(p.getCode())
                            .parentCode(p.getParentCode())
                            .status(StringUtils.equals(userName, "admin"))
                            .build())
                    .collect(Collectors.toList());
            PermissionsTree tree = new PermissionsTree(permissionsResponses);
            return tree.buildTree();

        } else {
            Map<String, WeComSysBasePermissionsEntity> permissionsEntityMap =
                    permissionsEntities.stream()
                                       .collect(Collectors.toMap(WeComSysBasePermissionsEntity::getUuid, Function.identity()));

            Map<String, WeComSysCorpRolePermissionsLinkEntity> linkEntityMap = links
                    .stream()
                    .collect(Collectors.toMap(WeComSysCorpRolePermissionsLinkEntity::getPermissionsUuid, Function.identity()));

            List<RolePermissionsResponse> basePermissionsNotInLink =
                    permissionsEntityMap.entrySet().stream()
                                        .filter(e -> !linkEntityMap.containsKey(e.getKey()))
                                        .map(e -> {
                                            WeComSysBasePermissionsEntity value = e.getValue();
                                            return RolePermissionsResponse
                                                    .builder()
                                                    .permissionsUuid(value.getUuid())
                                                    .name(value.getName())
                                                    .title(value.getTitle())
                                                    .code(value.getCode())
                                                    .parentCode(value.getParentCode())
                                                    .status(false)
                                                    .build();
                                        }).collect(Collectors.toList());


            List<RolePermissionsResponse> permissionsResponses =
                    links.stream().map(l -> {
                        WeComSysBasePermissionsEntity permissionsEntity = permissionsEntityMap.getOrDefault(l.getPermissionsUuid(), new WeComSysBasePermissionsEntity());

                        return RolePermissionsResponse.builder()
                                                      .permissionsUuid(permissionsEntity.getUuid())
                                                      .code(permissionsEntity.getCode())
                                                      .name(permissionsEntity.getName())
                                                      .title(permissionsEntity.getTitle())
                                                      .parentCode(permissionsEntity.getParentCode())
                                                      .status(l.getStatus() == PermissionsEnum.enable)
                                                      .build();
                    }).collect(Collectors.toList());

            permissionsResponses.addAll(CollectionUtils.isEmpty(basePermissionsNotInLink) ? Lists.newArrayList() : basePermissionsNotInLink);


            PermissionsTree tree = new PermissionsTree(permissionsResponses);
            return tree.buildTree();
        }


    }


    @Override
    @Transactional
    public List<RolePermissionsResponse> permissionsAuthorization(RolePermissionsAuthorizationRequest request) {

        RolePermissionsRequest permissionsRequest = RolePermissionsRequest.builder()
                                                                          .corpId(request.getCorpId())
                                                                          .projectUuid(request.getProjectUuid())
                                                                          .roleUuid(request.getRoleUuid())
                                                                          .tenantUuid(request.getTenantUuid())
                                                                          .build();
        if (CollectionUtils.isEmpty(request.getPermissions())) {
            return fetchUserPermissions(permissionsRequest);
        } else {
            List<WeComSysCorpRolePermissionsLinkEntity> rolelinks = rolePermissionsLinkRepository.findByCorpIdAndRoleUuid(request.getCorpId(), request.getRoleUuid());
            if (CollectionUtils.isNotEmpty(rolelinks)) {
                for (WeComSysCorpRolePermissionsLinkEntity rolelink : rolelinks) {
                    rolePermissionsLinkRepository.deleteById(rolelink.getId());
                }
            }
            saveRolePermissionsLink(request.getPermissions(), request.getCorpId(), request.getRoleUuid());
        }

        return fetchUserPermissions(permissionsRequest);
    }

    private void saveRolePermissionsLink(List<RolePermissionsAuthorizationRequest.RolePermissionsInfo> permissions, String corpId, String roleUuid) {

        for (RolePermissionsAuthorizationRequest.RolePermissionsInfo p : permissions) {
            WeComSysCorpRolePermissionsLinkEntity entity = new WeComSysCorpRolePermissionsLinkEntity();
            entity.setCorpId(corpId);
            entity.setRoleUuid(roleUuid);
            entity.setPermissionsUuid(p.getPermissionsUuid());
            entity.setStatus(p.getStatus() ? PermissionsEnum.enable : PermissionsEnum.disable);
            rolePermissionsLinkRepository.save(entity);
            if (CollectionUtils.isNotEmpty(p.getChildren())) {
                saveRolePermissionsLink(p.getChildren(), corpId, roleUuid);
            }
        }
    }


    public static class PermissionsTree {

        private List<RolePermissionsResponse> permissionList = Lists.newArrayList();

        public PermissionsTree(List<RolePermissionsResponse> permissionsList) {

            this.permissionList = permissionsList;
        }

        //建立树形结构
        public List<RolePermissionsResponse> buildTree() {

            List<RolePermissionsResponse> treePermission = Lists.newArrayList();
            for (RolePermissionsResponse permissionsNode : getRootNode()) {
                permissionsNode = buildChildTree(permissionsNode);
                treePermission.add(permissionsNode);
            }
            return treePermission;
        }

        //递归，建立子树形结构
        public RolePermissionsResponse buildChildTree(RolePermissionsResponse pNode) {

            List<RolePermissionsResponse> childPermissions = Lists.newArrayList();
            for (RolePermissionsResponse permissionsNodeNode : permissionList) {
                if (permissionsNodeNode.getParentCode().equals(pNode.getCode())) {
                    childPermissions.add(buildChildTree(permissionsNodeNode));
                }
            }
            pNode.setChildren(childPermissions);
            return pNode;
        }

        //获取根节点(获取所有的父节点)
        public List<RolePermissionsResponse> getRootNode() {

            List<RolePermissionsResponse> rootPermissionLists = Lists.newArrayList();
            for (RolePermissionsResponse permissionsNode : permissionList) {
                if (StringUtils.isEmpty(permissionsNode.getParentCode())) {
                    rootPermissionLists.add(permissionsNode);
                }
            }
            return rootPermissionLists;
        }

    }

}
