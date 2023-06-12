package com.easy.marketgo.web.service.project.impl;

import cn.hutool.core.util.IdUtil;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.DateFormatUtils;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.entity.ProjectConfigEntity;
import com.easy.marketgo.core.entity.WeComSysCorpUserRoleLinkEntity;
import com.easy.marketgo.core.entity.WeComSysUserEntity;
import com.easy.marketgo.core.entity.WeComUserTenantLinkEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.repository.user.WeComSysUserRepository;
import com.easy.marketgo.core.repository.wecom.ProjectConfigRepository;
import com.easy.marketgo.core.repository.wecom.WeComSysCropUserRoleLinkRepository;
import com.easy.marketgo.core.repository.wecom.WeComUserTenantLinkRepository;
import com.easy.marketgo.core.repository.wecom.customer.WeComMemberMessageRepository;
import com.easy.marketgo.web.client.ClientRequestContextHolder;
import com.easy.marketgo.web.model.request.ProjectCreateRequest;
import com.easy.marketgo.web.model.response.ProjectFetchResponse;
import com.easy.marketgo.web.service.project.IProjectService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-18 20:11:35
 * @description : ProjectServiceImpl.java
 */
@Component
@Log4j2
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private WeComSysUserRepository sysUserRepository;

    @Autowired
    private WeComUserTenantLinkRepository userTenantLinkRepository;

    @Autowired
    private ProjectConfigRepository projectConfigRepository;

    @Autowired
    private WeComSysCropUserRoleLinkRepository weComSysCropUserRoleLinkRepository;

    @Autowired
    private WeComMemberMessageRepository weComMemberMessageRepository;

    @Override
    public ProjectFetchResponse fetchProjects() {

        String userName = ClientRequestContextHolder.current().getUserName();
        ProjectFetchResponse response = ProjectFetchResponse.builder().build();

        WeComSysUserEntity userEntity = sysUserRepository.queryByUserName(userName);
        if (Objects.isNull(userEntity)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS);
        }
        List<WeComUserTenantLinkEntity> linkEntities = userTenantLinkRepository.findByUserUuid(userEntity.getUuid());

        if (CollectionUtils.isEmpty(linkEntities)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY);
        }

        WeComUserTenantLinkEntity linkEntity = linkEntities
                .stream().findAny()
                .orElseThrow((Supplier<RuntimeException>) () -> new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY));

        response.setTenantUuid(linkEntity.getTenantUuid());

        WeComSysCorpUserRoleLinkEntity entity =
                weComSysCropUserRoleLinkRepository.findByCorpIdAndProjectUuidAndMemberId(linkEntity.getTenantUuid(),
                        linkEntity.getTenantUuid(), userName);
        response.setCanCreate(entity != null ? Boolean.TRUE : Boolean.FALSE);
        List<ProjectConfigEntity> configEntities = new ArrayList<>();
        if (response.getCanCreate()) {
            configEntities = projectConfigRepository.findByTenantUuid(linkEntity.getTenantUuid());
        } else {
            String memberId = weComMemberMessageRepository.queryMemberIdByMobile(userName);
            List<WeComSysCorpUserRoleLinkEntity> entities =
                    weComSysCropUserRoleLinkRepository.findByMemberId(memberId);
            List<String> projectUuids = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(entities)) {
                for (WeComSysCorpUserRoleLinkEntity item : entities) {
                    projectUuids.add(item.getProjectUuid());
                }
            }
            log.info("query to project list, memberId={}, projectUuids={}.", memberId,
                    JsonUtils.toJSONString(projectUuids));
            configEntities = projectConfigRepository.findByUuids(projectUuids);
        }
        List<ProjectFetchResponse.ProjectInfo> projectInfos = configEntities
                .stream()
                .map(c -> ProjectFetchResponse
                        .ProjectInfo.builder()
                        .projectName(c.getName())
                        .projectUuid(c.getUuid())
                        .status(c.getStatus())
                        .desc(c.getDesc())
                        .type(c.getType())
                        .createTime(DateFormatUtils.formatDate(c.getCreateTime()))
                        .build()).collect(Collectors.toList());
        response.setProjects(projectInfos);
        return response;
    }

    @Override
    public BaseResponse createProject(ProjectCreateRequest projectCreateRequest) {
        String userName = ClientRequestContextHolder.current().getUserName();
        log.info("begin to create project , userName={}, projectCreateRequest={}.", userName,
                JsonUtils.toJSONString(projectCreateRequest));
        WeComSysUserEntity userEntity = sysUserRepository.queryByUserName(userName);
        if (Objects.isNull(userEntity)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_EXISTS);
        }
        List<WeComUserTenantLinkEntity> linkEntities = userTenantLinkRepository.findByUserUuid(userEntity.getUuid());
        if (CollectionUtils.isEmpty(linkEntities)) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_USER_IS_NOT_CREATE_PERMISSION);
        }

        WeComUserTenantLinkEntity linkEntity = linkEntities
                .stream().findAny()
                .orElseThrow((Supplier<RuntimeException>) () -> new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY));
        WeComSysCorpUserRoleLinkEntity entity =
                weComSysCropUserRoleLinkRepository.findByCorpIdAndProjectUuidAndMemberId(linkEntity.getTenantUuid(),
                        linkEntity.getTenantUuid(), userName);
        if (entity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_EMPTY);
        }
        ProjectConfigEntity projectConfigEntity = new ProjectConfigEntity();
        projectConfigEntity.setDesc(projectCreateRequest.getDesc());
        projectConfigEntity.setName(projectCreateRequest.getName());
        projectConfigEntity.setUuid(IdUtil.simpleUUID());
        projectConfigEntity.setTenantUuid(linkEntity.getTenantUuid());
        projectConfigEntity.setStatus("publish");
        projectConfigEntity.setType("SCRM");
        projectConfigRepository.save(projectConfigEntity);
        return BaseResponse.success();
    }

    @Override
    public BaseResponse checkName(Integer projectId, String name) {
        try {
            log.info("begin to check project name, projectId={}, name={}.", projectId, name);
            ProjectConfigEntity projectConfigEntity = projectConfigRepository.queryByName(projectId, name);
            // 仅当非同一个项目且同名的情况，认为重名
            if (projectConfigEntity != null && !projectConfigEntity.getId().equals(projectId)) {
                log.info("failed to check project name, projectId={}, name={}.", projectId, name);
                return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WECOM_PROJECT_DUPLICATE_CNAME.getCode()).message(ErrorCodeEnum.ERROR_WECOM_PROJECT_DUPLICATE_CNAME.getMessage()).build();
            }
            log.info("succeed to check project name, projectId={}, name={}.", projectId, name);
            return BaseResponse.builder().code(ErrorCodeEnum.OK.getCode()).message(ErrorCodeEnum.OK.getMessage()).build();
        } catch (Exception e) {
            log.error("Failed to check weCom mass task cname, projectId={}, name={}.", projectId, name, e);
        }
        return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getCode()).message(ErrorCodeEnum.ERROR_WEB_WECOM_MASS_TASK_CHECK_NAME.getMessage()).build();
    }
}
