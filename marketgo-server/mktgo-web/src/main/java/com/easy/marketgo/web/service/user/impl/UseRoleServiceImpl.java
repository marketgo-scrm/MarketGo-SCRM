package com.easy.marketgo.web.service.user.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.easy.marketgo.common.enums.BaseRoleEnum;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.UuidUtils;
import com.easy.marketgo.core.entity.customer.WeComDepartmentEntity;
import com.easy.marketgo.core.entity.customer.WeComMemberMessageEntity;
import com.easy.marketgo.core.entity.WeComSysBaseRoleEntity;
import com.easy.marketgo.core.entity.WeComSysCorpUserRoleLinkEntity;
import com.easy.marketgo.core.repository.wecom.WeComDepartmentRepository;
import com.easy.marketgo.core.repository.wecom.WeComSysBaseRoleRepository;
import com.easy.marketgo.core.repository.wecom.WeComSysCropUserRoleLinkRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComRelationMemberExternalUserRepository;
import com.easy.marketgo.web.client.ClientRequestContextHolder;
import com.easy.marketgo.web.model.request.user.RolePermissionsRequest;
import com.easy.marketgo.web.model.request.user.UserRoleAddRequest;
import com.easy.marketgo.web.model.request.user.UserRoleAuthorizationRequest;
import com.easy.marketgo.web.model.request.user.UserRoleInfoRequest;
import com.easy.marketgo.web.model.request.user.UserRoleListQueryRequest;
import com.easy.marketgo.web.model.request.user.UserRoleListRequest;
import com.easy.marketgo.web.model.response.user.RolePermissionsResponse;
import com.easy.marketgo.web.model.response.user.UserRoleInfo;
import com.easy.marketgo.web.model.response.user.UserRoleListQueryResponse;
import com.easy.marketgo.web.model.response.user.UserRoleListResponse;
import com.easy.marketgo.web.service.user.IUserPermissionsService;
import com.easy.marketgo.web.service.user.IUserRoleService;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-16 09:24:11
 * @description : UseRoleServiceImpl.java
 */
@Component
@Log4j2
public class UseRoleServiceImpl implements IUserRoleService {

    @Autowired
    private WeComSysCropUserRoleLinkRepository sysCropUserRoleLinkRepository;
    @Autowired
    private WeComSysBaseRoleRepository baseRoleRepository;
    @Autowired
    private WeComMemberMessageRepository memberMessageRepository;
    @Autowired
    private WeComDepartmentRepository departmentRepository;
    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;
    @Autowired
    private IUserPermissionsService userPermissionsService;
    @Autowired
    private WeComRelationMemberExternalUserRepository relationMemberExternalUserRepository;

    @Override
    @Transactional
    public UserRoleListResponse userRoleList(UserRoleListRequest request) {

        List<WeComSysCorpUserRoleLinkEntity> userRoleLinkEntities = sysCropUserRoleLinkRepository.findByCorpIdAndProjectUuid(request.getCorpId(),
                request.getProjectUuid());

        List<WeComSysBaseRoleEntity> baseRoleEntities = baseRoleRepository.findByProjectUuid(request.getProjectUuid());
        if (CollectionUtils.isEmpty(userRoleLinkEntities)) {

            WeComSysBaseRoleEntity roleEntity = baseRoleEntities
                    .stream().
                    filter(role -> StringUtils.equals(BaseRoleEnum.ordinary_employees.name(), role.getCode()))
                    .findAny()
                    .get();


            List<WeComMemberMessageEntity> memberMessageEntities = memberMessageRepository.findByCorpId(request.getCorpId());
            userRoleLinkEntities = memberMessageEntities.stream().map(member -> {
                WeComSysCorpUserRoleLinkEntity link = new WeComSysCorpUserRoleLinkEntity();
                link.setCorpId(request.getCorpId());
                link.setRoleUuid(roleEntity.getUuid());
                link.setMemberId(member.getMemberId());
                link.setProjectUuid(request.getProjectUuid());

                return link;

            }).collect(Collectors.toList());

            sysCropUserRoleLinkRepository.saveAll(userRoleLinkEntities);

            log.info("memberMessageEntities {}", memberMessageEntities);


        }
        Map<String, WeComSysBaseRoleEntity> baseCodeMap = baseRoleEntities
                .stream()
                .collect(Collectors.toMap(WeComSysBaseRoleEntity::getCode, Function.identity()));

        List<WeComSysCorpUserRoleLinkEntity> finalUserRoleLinkEntities = userRoleLinkEntities;
        List<UserRoleInfo> roleInfoList = baseCodeMap.entrySet().stream().map(entity -> {
            long count = CollectionUtils.isEmpty(finalUserRoleLinkEntities) ? 0L : finalUserRoleLinkEntities
                    .stream()
                    .filter(link -> StringUtils.equals(link.getRoleUuid(), entity.getValue().getUuid()))
                    .count();

            return UserRoleInfo.builder()
                               .code(entity.getKey())
                               .roleUuid(entity.getValue().getUuid())
                               .desc(entity.getValue().getDesc())
                               .count(count)
                               .build();

        }).collect(Collectors.toList());

        return UserRoleListResponse.builder().infos(roleInfoList).build();


    }

    @Override
    @Transactional
    public UserRoleListResponse userRoleAdd(UserRoleAddRequest request) {

        List<WeComSysBaseRoleEntity> entities = baseRoleRepository.findByProjectUuid(request.getProjectUuid());
        if (StringUtils.isEmpty(StringUtils.trim(request.getRoleDesc()))) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_ROLE_DESC_EMPTY);
        }

        boolean anyMatch = entities.stream().anyMatch(e ->
                StringUtils.equals(request.getRoleDesc(), e.getDesc()));
        if (anyMatch) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_ROLE_EXISTS);
        }
        WeComSysBaseRoleEntity roleEntity = new WeComSysBaseRoleEntity();
        roleEntity.setCode(DigestUtil.md5Hex(StringUtils.trim(request.getRoleDesc())));
        roleEntity.setDesc(request.getRoleDesc());
        roleEntity.setCorpId(request.getCorpId());
        roleEntity.setProjectUuid(request.getProjectUuid());
        roleEntity.setUuid(UuidUtils.generateUuid());
        baseRoleRepository.save(roleEntity);

        List<WeComSysCorpUserRoleLinkEntity> userRoleLinkEntities = sysCropUserRoleLinkRepository.findByCorpIdAndProjectUuid(request.getCorpId(),
                request.getProjectUuid());
        List<WeComSysBaseRoleEntity> baseRoleEntities = baseRoleRepository.findByProjectUuid(request.getProjectUuid());
        Map<String, WeComSysBaseRoleEntity> baseCodeMap = baseRoleEntities
                .stream()
                .collect(Collectors.toMap(WeComSysBaseRoleEntity::getCode, Function.identity()));

        List<UserRoleInfo> roleInfoList = baseCodeMap.entrySet().stream().map(entity -> {
            long count = CollectionUtils.isEmpty(userRoleLinkEntities) ? 0L : userRoleLinkEntities
                    .stream()
                    .filter(link -> StringUtils.equals(link.getRoleUuid(), entity.getValue().getUuid()))
                    .count();
            return UserRoleInfo.builder()
                               .code(entity.getKey())
                               .roleUuid(entity.getValue().getUuid())
                               .desc(entity.getValue().getDesc())
                               .count(count)
                               .build();

        }).collect(Collectors.toList());

        return UserRoleListResponse.builder().infos(roleInfoList).build();

    }

    @Override
    public UserRoleListQueryResponse queryMembers(UserRoleListQueryRequest request) {

        if (StringUtils.isNotEmpty(request.getRoleUuid())) {
            List<WeComSysBaseRoleEntity> baseRoleList = baseRoleRepository.findByProjectUuidAndUuid(request.getProjectUuid(), request.getRoleUuid());
            if (CollectionUtils.isEmpty(baseRoleList)) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_ROLE_UUID_EMPTY);
            }

        }


        List<WeComSysCorpUserRoleLinkEntity> links = sysCropUserRoleLinkRepository.findByCorpIdAndRoleUuidAndProjectUuid(request.getCorpId(), request.getRoleUuid(), request.getProjectUuid());
        List<WeComMemberMessageEntity> members = StringUtils.isBlank(request.getSearchMemberName()) ?
                memberMessageRepository.findByCorpId(request.getCorpId()) :
                memberMessageRepository.findTopByCorpIdAndMemberNameLike(request.getCorpId(), StringUtils.trim(request.getSearchMemberName()));
        List<WeComDepartmentEntity> departmentEntities = departmentRepository.findByCorpId(request.getCorpId());


        Map<String, List<WeComDepartmentEntity>> leaderDepartmentMap = departmentEntities
                .stream().filter(d -> StringUtils.isNotEmpty(d.getDepartmentLeader()))
                .collect(Collectors.groupingBy(WeComDepartmentEntity::getDepartmentLeader));

        Map<String, WeComDepartmentEntity> departmentEntityMap =
                CollectionUtils.isEmpty(departmentEntities) ?
                        Collections.emptyMap() :
                        departmentEntities
                                .stream()
                                .collect(Collectors.toMap(e -> e.getDepartmentId() + "", Function.identity()));

        Map<String, WeComMemberMessageEntity> memberMessageEntityMap =
                CollectionUtils.isEmpty(members) ?
                        Collections.emptyMap() :
                        members.stream()
                               .collect(Collectors.toMap(WeComMemberMessageEntity::getMemberId, Function.identity()));


        List<UserRoleListQueryResponse.WeComMemberInfo> memberInfoList =
                links.stream().map(l -> {
                         WeComMemberMessageEntity defaultMemberEntity = new WeComMemberMessageEntity();
                         defaultMemberEntity.setDepartment("");
                         defaultMemberEntity.setMainDepartment("");
                         WeComMemberMessageEntity message = memberMessageEntityMap.getOrDefault(l.getMemberId(), defaultMemberEntity);
                         String[] split = StringUtils.split(message.getDepartment(), ",");
                         int count = relationMemberExternalUserRepository.countByCorpIdAndMemberId(request.getCorpId(), message.getMemberId());
                         return UserRoleListQueryResponse
                                 .WeComMemberInfo.builder()
                                                 .memberId(message.getMemberId())
                                                 .memberName(message.getMemberName())
                                                 .corpId(message.getCorpId())
                                                 .departmentId(message.getDepartment())
                                                 .externalUserCount(count + "")
                                                 .departmentName(Arrays
                                                         .stream(split)
                                                         .map(departmentId -> departmentEntityMap
                                                                 .getOrDefault(departmentId, new WeComDepartmentEntity())
                                                                 .getDepartmentName())
                                                         .filter(StringUtils::isNotEmpty)
                                                         .collect(Collectors.joining(",")))
                                                 .mainDepartmentId(
                                                         leaderDepartmentMap.getOrDefault(message.getMemberId(), Lists.newArrayList()).stream()
                                                                            .map(department -> department.getDepartmentId() + "")
                                                                            .filter(StringUtils::isNotEmpty)
                                                                            .collect(Collectors.joining(","))

                                                 )
                                                 .mainDepartmentName(
                                                         leaderDepartmentMap.getOrDefault(message.getMemberId(), Lists.newArrayList()).stream()
                                                                            .map(department -> department.getDepartmentName() + "")
                                                                            .filter(StringUtils::isNotEmpty)
                                                                            .collect(Collectors.joining(","))

                                                 )
                                                 .thumbAvatar(message.getThumbAvatar())
                                                 .avatar(message.getAvatar())
                                                 .build();
                     }).filter(info -> StringUtils.isNotEmpty(info.getMemberId()))

                     .collect(Collectors.toList());

        return UserRoleListQueryResponse.builder().infos(memberInfoList).build();
    }

    @Override
    @Transactional
    public UserRoleListResponse userRoleChange(UserRoleAuthorizationRequest request) {


        if (CollectionUtils.isEmpty(request.getChangeRoleUserInfos())) {
            return userRoleList(UserRoleListRequest.builder()
                                                   .projectUuid(request.getProjectUuid())
                                                   .tenantUuid(request.getTenantUuid())
                                                   .corpId(request.getCorpId()).build());
        }

        boolean anyMatch = request.getChangeRoleUserInfos().stream().anyMatch(info -> StringUtils.isEmpty(info.getTargetRoleUuid()));
        if (anyMatch) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_ROLE_ID_EMPTY);
        }

        List<WeComSysBaseRoleEntity> baseRoleEntities = baseRoleRepository.findByProjectUuid(request.getProjectUuid());
        WeComSysBaseRoleEntity superAdministratorRole = baseRoleEntities
                .stream()
                .filter(b -> StringUtils.equals(b.getCode(), BaseRoleEnum.super_administrator.name()))
                .findAny()
                .orElse(new WeComSysBaseRoleEntity());
        boolean baseRoleMatch = request
                .getChangeRoleUserInfos()
                .stream()
                .anyMatch(info -> StringUtils.equals(superAdministratorRole.getUuid(), info.getTargetRoleUuid()));
        if (baseRoleMatch) {
            throw new CommonException(ErrorCodeEnum.ERROR_NOT_SUPPORT_ROLE);
        }

        String[] roleRoleUuids = CollectionUtils.isEmpty(baseRoleEntities) ? new String[]{} :
                ArrayUtil.toArray(baseRoleEntities.stream().map(WeComSysBaseRoleEntity::getUuid).collect(Collectors.toList()), String.class);
        String[] targetRoleUuids =
                ArrayUtil.toArray(
                        request
                                .getChangeRoleUserInfos()
                                .stream()
                                .map(UserRoleAuthorizationRequest.RoleUserInfo::getTargetRoleUuid)
                                .collect(Collectors.toList()), String.class);

        if (!ArrayUtil.containsAll(roleRoleUuids, targetRoleUuids)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_ROLE_UUID_EMPTY);
        }


        List<WeComSysCorpUserRoleLinkEntity> links = sysCropUserRoleLinkRepository.findByCorpIdAndProjectUuid(request.getCorpId(), request.getProjectUuid());
        Map<String, WeComSysCorpUserRoleLinkEntity> linkEntityMap = links
                .stream()
                .collect(Collectors.toMap(l -> l.getProjectUuid() + l.getCorpId() + l.getMemberId(), Function.identity()));


        List<WeComSysCorpUserRoleLinkEntity> newLinkEntities = request.getChangeRoleUserInfos().stream()

                                                                      .map(info -> {
                                                                          WeComSysCorpUserRoleLinkEntity userRoleLink = linkEntityMap.get(request.getProjectUuid() + request.getCorpId() + info.getMemberId());
                                                                          if (Objects.isNull(userRoleLink)) {
                                                                              userRoleLink = new WeComSysCorpUserRoleLinkEntity();
                                                                              userRoleLink.setCorpId(request.getCorpId());
                                                                              userRoleLink.setRoleUuid(info.getTargetRoleUuid());
                                                                              userRoleLink.setMemberId(info.getMemberId());
                                                                              userRoleLink.setProjectUuid(request.getProjectUuid());
                                                                          } else {
                                                                              userRoleLink.setRoleUuid(info.getTargetRoleUuid());

                                                                          }
                                                                          return userRoleLink;

                                                                      }).collect(Collectors.toList());
        sysCropUserRoleLinkRepository.saveAll(newLinkEntities);

        return userRoleList(UserRoleListRequest.builder()
                                               .projectUuid(request.getProjectUuid())
                                               .tenantUuid(request.getTenantUuid())
                                               .corpId(request.getCorpId()).build());
    }

    @Override
    public List<RolePermissionsResponse> userRolePermissionsInfo(UserRoleInfoRequest request) {

        String userName = ClientRequestContextHolder.current().getUserName();
        List<WeComSysBaseRoleEntity> baseRoleEntities = baseRoleRepository.findByProjectUuid(request.getProjectUuid());
        if (StringUtils.equals(userName, "admin")) {
            WeComSysBaseRoleEntity baseRole = baseRoleEntities
                    .stream()
                    .filter(b -> StringUtils.equals(b.getCode(), BaseRoleEnum.super_administrator.name()))
                    .findAny()
                    .orElseThrow((Supplier<RuntimeException>) () -> new CommonException(ErrorCodeEnum.ERROR_WEB_BASE_ROLE_IS_EMPTY));
            RolePermissionsRequest permissionsRequest = RolePermissionsRequest.builder()
                                                                              .roleUuid(baseRole.getUuid())
                                                                              .tenantUuid(request.getTenantUuid())
                                                                              .projectUuid(request.getProjectUuid())
                                                                              .corpId(request.getCorpId())
                                                                              .build();
            return userPermissionsService.fetchUserPermissions(permissionsRequest);
        }
        List<WeComMemberMessageEntity> messageEntities = weComMemberMessageRepository.findTopByCorpIdAndMobile(request.getCorpId(), userName);
        WeComMemberMessageEntity messageEntity = messageEntities
                .stream()
                .findAny()
                .orElseThrow((Supplier<RuntimeException>) () -> new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS));
        WeComSysCorpUserRoleLinkEntity linkEntity = sysCropUserRoleLinkRepository.findByCorpIdAndProjectUuidAndMemberId(request.getCorpId(), request.getProjectUuid(),
                messageEntity.getMemberId());
        RolePermissionsRequest permissionsRequest = RolePermissionsRequest.builder()
                                                                          .roleUuid(StringUtils.isEmpty(linkEntity.getRoleUuid()) ? "" : linkEntity.getRoleUuid())
                                                                          .tenantUuid(request.getTenantUuid())
                                                                          .projectUuid(request.getProjectUuid())
                                                                          .corpId(request.getCorpId())
                                                                          .build();
        return userPermissionsService.fetchUserPermissions(permissionsRequest);
    }

}
