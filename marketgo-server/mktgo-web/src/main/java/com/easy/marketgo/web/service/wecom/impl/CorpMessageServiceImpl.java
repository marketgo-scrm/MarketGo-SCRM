package com.easy.marketgo.web.service.wecom.impl;

import cn.hutool.core.codec.Base62;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.easy.marketgo.api.model.request.WeComCheckAgentMessageRequest;
import com.easy.marketgo.api.model.request.customer.WeComQueryExternalUserDetailClientRequest;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.customer.WeComQueryExternalUserDetailClientResponse;
import com.easy.marketgo.api.service.WeComAgentRpcService;
import com.easy.marketgo.api.service.WeComExternalUserRpcService;
import com.easy.marketgo.biz.service.XxlJobManualTriggerService;
import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.enums.WeComCorpConfigStepEnum;
import com.easy.marketgo.common.exception.CommonException;
import com.easy.marketgo.common.utils.RandomUtils;
import com.easy.marketgo.core.entity.ProjectConfigEntity;
import com.easy.marketgo.core.entity.TenantConfigEntity;
import com.easy.marketgo.core.entity.WeComAgentMessageEntity;
import com.easy.marketgo.core.entity.WeComCorpMessageEntity;
import com.easy.marketgo.core.model.bo.BaseResponse;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.repository.wecom.ProjectConfigRepository;
import com.easy.marketgo.core.repository.wecom.TenantConfigRepository;
import com.easy.marketgo.core.repository.wecom.WeComAgentMessageRepository;
import com.easy.marketgo.core.repository.wecom.WeComCorpMessageRepository;
import com.easy.marketgo.web.model.request.WeComAgentMessageRequest;
import com.easy.marketgo.web.model.request.WeComCorpMessageRequest;
import com.easy.marketgo.web.model.request.WeComForwardServerMessageRequest;
import com.easy.marketgo.web.model.response.corp.*;
import com.easy.marketgo.web.service.wecom.CorpMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 6/22/22 10:49 PM
 * Describe:
 */
@Slf4j
@Service
public class CorpMessageServiceImpl implements CorpMessageService {
    @Resource
    private WeComAgentRpcService weComAgentRpcService;

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    private ProjectConfigRepository projectConfigRepository;

    @Resource
    private WeComExternalUserRpcService weComExternalUserRpcService;

    @Resource
    private WeComAgentMessageRepository weComAgentMessageRepository;

    @Resource
    private WeComCorpMessageRepository weComCorpMessageRepository;

    @Autowired
    private XxlJobManualTriggerService xxlJobManualTriggerService;

    @Autowired
    private RedisService redisService;

    @Override
    public BaseResponse checkAgentParams(String projectId, String corpId, String agentId, String secret) {
        WeComCheckAgentMessageRequest request = new WeComCheckAgentMessageRequest();
        request.setCorpId(corpId);
        request.setAgentId(agentId);
        request.setSecret(secret);
        RpcResponse response = weComAgentRpcService.checkAgentParams(request);
        log.info("check agent params. response={}", response);
        return BaseResponse.builder().code(response.getCode()).message(ErrorCodeEnum.getMessage(response.getCode(),
                response.getMessage())).build();
    }

    @Override
    public BaseResponse updateOrInsertCorpMessage(String projectId, WeComCorpMessageRequest weComCorpMessageRequest) {
        log.info("updateOrInsertCorpMessage params. projectId={}, weComCorpMessageRequest={}", projectId,
                weComCorpMessageRequest);
        BaseResponse response = BaseResponse.success();
        WeComCorpMessageEntity weComCorpMessageEntity =
                weComCorpMessageRepository.getCorpConfigByCorpId(weComCorpMessageRequest.getCorp().getCorpId());
        try {
            if (weComCorpMessageRequest.getConfigType().equalsIgnoreCase(WeComCorpConfigStepEnum.CONTACTS_MSG.getValue())) {
                if (weComCorpMessageEntity == null) {
                    return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_AGENT_PARAM);
                }
                if (StringUtils.isEmpty(weComCorpMessageEntity.getContactsSecret()) ||
                        !weComCorpMessageEntity.getContactsSecret().equals(weComCorpMessageRequest.getContacts().getSecret())) {
                    response = checkAgentParams(projectId, weComCorpMessageRequest.getCorp().getCorpId(),
                            Constants.AGENT_KEY_FOR_CONTACTS, weComCorpMessageRequest.getContacts().getSecret());
                    if (response.getCode().equals(ErrorCodeEnum.OK.getCode())) {
                        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
                        String encode = Base62.encode(key);
                        weComCorpMessageRepository.updateSecretByCorpId(weComCorpMessageRequest.getCorp().getCorpId(),
                                projectId,
                                weComCorpMessageRequest.getContacts().getSecret(),
                                RandomUtils.getRandomStr(16), encode);
                    }
                }
            } else if (weComCorpMessageRequest.getConfigType().equalsIgnoreCase(WeComCorpConfigStepEnum.EXTERNAL_USER_MSG.getValue())) {
                if (weComCorpMessageEntity == null) {
                    return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_AGENT_PARAM);
                }
                if (StringUtils.isEmpty(weComCorpMessageEntity.getExternalUserSecret()) ||
                        !weComCorpMessageEntity.getExternalUserSecret().equals(weComCorpMessageRequest.getExternalUser().getSecret())) {
                    response = checkAgentParams(projectId, weComCorpMessageRequest.getCorp().getCorpId(),
                            Constants.AGENT_KEY_FOR_EXTERNALUSER,
                            weComCorpMessageRequest.getExternalUser().getSecret());
                    if (response.getCode().equals(ErrorCodeEnum.OK.getCode())) {
                        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
                        String encode = Base62.encode(key);
                        weComCorpMessageRepository.updateExternalUserSecretByCorpId(weComCorpMessageRequest.getCorp().getCorpId(),
                                projectId, weComCorpMessageRequest.getExternalUser().getSecret(),
                                RandomUtils.getRandomStr(16), encode);
                        xxlJobManualTriggerService.manualTriggerHandler("syncCorpTags");
                        xxlJobManualTriggerService.manualTriggerHandler("syncMembers");
                        xxlJobManualTriggerService.manualTriggerHandler("syncGroupChats");
                    }
                }
            } else if (weComCorpMessageRequest.getConfigType().equalsIgnoreCase(WeComCorpConfigStepEnum.AGENT_MSG.getValue())) {
                if (weComCorpMessageEntity == null) {
                    return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_AGENT_PARAM);
                }

                WeComAgentMessageEntity weComAgentMessageEntity =
                        weComAgentMessageRepository.getAgentMessageByCorpId(weComCorpMessageEntity.getCorpId());
                if (weComAgentMessageEntity == null || !weComCorpMessageRequest.getAgent().getAgentId().equals(weComAgentMessageEntity.getAgentId())) {
                    weComAgentMessageEntity = (weComAgentMessageEntity == null ? new WeComAgentMessageEntity() :
                            weComAgentMessageEntity);
                    response = checkAgentParams(projectId, weComCorpMessageRequest.getCorp().getCorpId(),
                            weComCorpMessageRequest.getAgent().getAgentId(),
                            weComCorpMessageRequest.getAgent().getSecret());
                    if (response.getCode().equals(ErrorCodeEnum.OK.getCode())) {
                        weComAgentMessageEntity.setProjectUuid(projectId);
                        weComAgentMessageEntity.setAgentId(weComCorpMessageRequest.getAgent().getAgentId());
                        weComAgentMessageEntity.setCorpId(weComCorpMessageRequest.getCorp().getCorpId());
                        weComAgentMessageEntity.setSecret(weComCorpMessageRequest.getAgent().getSecret());
                        weComAgentMessageEntity.setIsChief(Boolean.TRUE);
                        weComAgentMessageEntity.setName("营销助手");
                        weComAgentMessageEntity.setEnableStatus("enable");
                        weComAgentMessageEntity.setAuthStatus("auth");
                        weComAgentMessageEntity.setHomePage("/home");
                        weComAgentMessageRepository.save(weComAgentMessageEntity);
                    }
                }
            } else if (weComCorpMessageRequest.getConfigType().equalsIgnoreCase(WeComCorpConfigStepEnum.CORP_MSG.getValue())) {
                if (weComCorpMessageEntity == null) {
                    weComCorpMessageEntity = new WeComCorpMessageEntity();
                }
                weComCorpMessageEntity.setProjectUuid(projectId);
                weComCorpMessageEntity.setCorpName(weComCorpMessageRequest.getCorp().getCorpName());
                weComCorpMessageEntity.setCorpId(weComCorpMessageRequest.getCorp().getCorpId());
                weComCorpMessageRepository.save(weComCorpMessageEntity);
            } else {
                log.info("not support config type.projectId={}, weComCorpMessageRequest={}", projectId,
                        weComCorpMessageRequest);
            }
        } catch (Exception e) {
            log.error("failed to save config message.", e);
        }
        return response;
    }

    @Override
    public BaseResponse updateOrInsertAgentMessage(String projectId, WeComAgentMessageRequest agentMessageRequest) {
        if (StringUtils.isEmpty(projectId) || agentMessageRequest == null ||
                StringUtils.isEmpty(agentMessageRequest.getAgentId()) ||
                StringUtils.isEmpty(agentMessageRequest.getCorpId()) ||
                StringUtils.isEmpty(agentMessageRequest.getSecret())) {
            return BaseResponse.builder().code(ErrorCodeEnum.ERROR_WEB_AGENT_PARAM.getCode())
                    .message(ErrorCodeEnum.ERROR_WEB_AGENT_PARAM.getMessage()).build();
        }
        WeComAgentMessageEntity agentEntity =
                weComAgentMessageRepository.getWeComAgentByCorpAndAgent(agentMessageRequest.getCorpId(),
                        agentMessageRequest.getAgentId());
        if (agentEntity == null) {
            WeComAgentMessageEntity entity = new WeComAgentMessageEntity();
            entity.setAgentId(agentMessageRequest.getAgentId());
            entity.setCorpId(agentMessageRequest.getCorpId());
            entity.setSecret(agentMessageRequest.getSecret());
            weComAgentMessageRepository.save(entity);
        } else {
            weComAgentMessageRepository.updateAgentMessageByCorpId(agentMessageRequest.getCorpId(), projectId,
                    agentMessageRequest.getAgentId(), agentMessageRequest.getSecret());
        }
        return BaseResponse.builder().code(ErrorCodeEnum.OK.getCode()).message(ErrorCodeEnum.OK.getMessage()).build();
    }

    @Override
    public BaseResponse getCorpConfig(String projectId) {
        WeComCorpConfigResponse response = new WeComCorpConfigResponse();
        List<WeComCorpMessageEntity> entities = weComCorpMessageRepository.getCorpConfigListByProjectUuid(projectId);
        if (CollectionUtils.isEmpty(entities)) {
            return BaseResponse.success();
        }
        List<WeComCorpConfigResponse.ConfigMessage> configList = new ArrayList<>();
        for (WeComCorpMessageEntity item : entities) {
            WeComAgentMessageEntity agentEntity = weComAgentMessageRepository.getWeComAgentByCorp(projectId,
                    item.getCorpId());

            WeComCorpConfigResponse.ConfigMessage config = new WeComCorpConfigResponse.ConfigMessage();
            WeComCorpConfigResponse.CorpConfig corpConfig = new WeComCorpConfigResponse.CorpConfig();
            corpConfig.setCorpId(StringUtils.isEmpty(item.getCorpId()) ? "" : item.getCorpId());
            corpConfig.setCorpName(StringUtils.isEmpty(item.getCorpName()) ? "" : item.getCorpName());
            config.setCorp(corpConfig);

            if (agentEntity != null) {
                WeComCorpConfigResponse.AgentConfig agentConfig = new WeComCorpConfigResponse.AgentConfig();
                agentConfig.setAgentId(agentEntity.getAgentId());
                agentConfig.setSecret(agentEntity.getSecret());
                config.setAgent(agentConfig);
            }

            ProjectConfigEntity projectConfigEntity = projectConfigRepository.findAllByUuid(projectId);
            if (projectConfigEntity == null) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_PROJECT_IS_ILLEGAL);
            }

            TenantConfigEntity tenantConfigEntity =
                    tenantConfigRepository.findByUuid(projectConfigEntity.getTenantUuid());
            if (tenantConfigEntity == null) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_ILLEGAL);
            }

            if (StringUtils.isNotEmpty(item.getContactsSecret())) {
                WeComCorpConfigResponse.ContactsConfig contactsConfig = new WeComCorpConfigResponse.ContactsConfig();
                contactsConfig.setEncodingAesKey(item.getContactsEncodingAesKey());
                contactsConfig.setToken(item.getContactsToken());
                contactsConfig.setUrl(tenantConfigEntity.getServerAddress() + Constants.WECOM_CALLBACK_CONSTACTS + item.getCorpId());
                contactsConfig.setSecret(item.getContactsSecret());
                config.setContacts(contactsConfig);
            }
            if (StringUtils.isNotEmpty(item.getExternalUserSecret())) {
                WeComCorpConfigResponse.ContactsConfig externalUserConfig =
                        new WeComCorpConfigResponse.ContactsConfig();
                externalUserConfig.setEncodingAesKey(item.getExternalUserEncodingAesKey());
                externalUserConfig.setToken(item.getExternalUserToken());
                externalUserConfig.setUrl(tenantConfigEntity.getServerAddress() + Constants.WECOM_CALLBACK_CUSTOMER + item.getCorpId());
                externalUserConfig.setSecret(item.getExternalUserSecret());
                config.setExternalUser(externalUserConfig);
            }
            configList.add(config);
        }
        response.setConfigs(configList);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getCallbackConfig(String projectId, String corpId, String configType) {
        ProjectConfigEntity projectConfigEntity = projectConfigRepository.findAllByUuid(projectId);
        if (projectConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PROJECT_IS_ILLEGAL);
        }

        TenantConfigEntity tenantConfigEntity = tenantConfigRepository.findByUuid(projectConfigEntity.getTenantUuid());
        if (tenantConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_ILLEGAL);
        }
        WeComCorpMessageEntity entity = weComCorpMessageRepository.getCorpConfigByCorp(projectId, corpId);
        log.info("start to query corp message. corpId={}, entity={}", corpId, entity);
        WeComCorpCallbackResponse response = new WeComCorpCallbackResponse();
        if (configType.equals(WeComCorpConfigStepEnum.CONTACTS_MSG.getValue())) {
            response.setToken(entity.getContactsToken());
            response.setEncodingAesKey(entity.getContactsEncodingAesKey());
            response.setCallbackUrl(tenantConfigEntity.getServerAddress() + Constants.WECOM_CALLBACK_CONSTACTS + corpId);
        } else {
            response.setToken(entity.getExternalUserToken());
            response.setEncodingAesKey(entity.getExternalUserEncodingAesKey());
            response.setCallbackUrl(tenantConfigEntity.getServerAddress() + Constants.WECOM_CALLBACK_CUSTOMER + corpId);
        }
        return BaseResponse.success(response);
    }

    @Override
    public void getExternalUserDetail(String corpId, String externalUserId) {
        WeComQueryExternalUserDetailClientRequest request = new WeComQueryExternalUserDetailClientRequest();
        request.setExternalUserId(externalUserId);
        request.setCorpId(corpId);
        request.setAgentId(Constants.AGENT_KEY_FOR_EXTERNALUSER);
        RpcResponse<WeComQueryExternalUserDetailClientResponse> responseRpcResponse =
                weComExternalUserRpcService.queryExternalUserDetail(request);
        System.out.println("responseRpcResponse= " + responseRpcResponse);
    }

    @Override
    public BaseResponse updateOrInsertForwardServer(String projectId, String corpId, String configType,
                                                    WeComForwardServerMessageRequest request) {
        log.info("start to save corp forward server message. corpId={}, request={}", corpId, request);
        try {
            if (request == null || CollectionUtils.isEmpty(request.getForwardServer())) {
                throw new CommonException(ErrorCodeEnum.ERROR_WEB_PARAM_IS_ILLEGAL);
            }
            String message = request.getForwardServer().stream().collect(Collectors.joining(","));
            log.info("save corp forward server message. corpId={}, request={}, message={}", corpId, request, message);
            if (configType.equals(WeComCorpConfigStepEnum.EXTERNAL_USER_MSG.getValue())) {
                weComCorpMessageRepository.updateForwardCustomerAddressByCorpId(projectId, corpId, message);
                redisService.set(String.format(Constants.WECOM_CALLBACK_CUSTOMER_FORWARD_URL, corpId), message, 0L);
            } else {
                weComCorpMessageRepository.updateForwardAddressByCorpId(projectId, corpId, message);
                redisService.set(String.format(Constants.WECOM_CALLBACK_FORWARD_URL, corpId), message, 0L);
            }
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("failed to save corp forward server message. corpId={}, request={}", corpId, request, e);
        }
        return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_CDP_FORWARD_SETTING);
    }

    @Override
    public BaseResponse getForwardServer(String projectId, String corpId, String configType) {
        log.info("start to get corp forward server message. corpId={}", corpId);
        WeComForwardServerMessageResponse response = new WeComForwardServerMessageResponse();
        WeComCorpMessageEntity entity = weComCorpMessageRepository.getCorpConfigByCorp(projectId, corpId);
        if (entity != null && StringUtils.isNotEmpty(entity.getForwardAddress())) {
            if (configType.equals(WeComCorpConfigStepEnum.EXTERNAL_USER_MSG.getValue())) {
                response.setForwardServer(Arrays.asList(entity.getForwardCustomerAddress().split(",")));
            } else {
                response.setForwardServer(Arrays.asList(entity.getForwardAddress().split(",")));
            }
        }
        log.info("get corp forward server message. response={}", response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse getSidebarServer(String projectId, String corpId) {
        log.info("start to get corp sidebar message. corpId={}", corpId);

        ProjectConfigEntity projectConfigEntity = projectConfigRepository.findAllByUuid(projectId);
        if (projectConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PROJECT_IS_ILLEGAL);
        }

        TenantConfigEntity tenantConfigEntity = tenantConfigRepository.findByUuid(projectConfigEntity.getTenantUuid());
        if (tenantConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_ILLEGAL);
        }

        WeComSidebarMessageResponse response = new WeComSidebarMessageResponse();

        response.setSidebarUrl(tenantConfigEntity.getServerAddress() + Constants.WECOM_SIDEBAR_MESSAGE + corpId);
        log.info("get corp sidebar message. response={}", response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse verifyCredFile(String projectId, String corpId, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_UPLOAD_OFFLINE_USER_GROUP_FILE_SIZE_EMPTY);
        }
        String fileName = multipartFile.getOriginalFilename();

        String type = multipartFile.getContentType();
        log.info("start to upload cred file message. fileName={}, type={}", fileName, type);
        try {
            byte[] content = multipartFile.getBytes();
            if (content.length > 0) {
                String fileContent = new String(content);
                log.info("upload cred file message. fileName={}, fileContent={}", fileName, fileContent);
                weComCorpMessageRepository.updateCredFileMessageByCorpId(projectId, corpId, fileName, fileContent);
            }
        } catch (IOException e) {
            log.error("failed to upload corp cred file message. corpId={}", corpId, e);
            return BaseResponse.failure(ErrorCodeEnum.ERROR_WEB_UPLOAD_FILE_FAILED);
        }

        return BaseResponse.success();
    }

    @Override
    public BaseResponse queryDomainUrl(String projectId, String corpId) {
        ProjectConfigEntity projectConfigEntity = projectConfigRepository.findAllByUuid(projectId);
        if (projectConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_PROJECT_IS_ILLEGAL);
        }

        TenantConfigEntity tenantConfigEntity = tenantConfigRepository.findByUuid(projectConfigEntity.getTenantUuid());
        if (tenantConfigEntity == null) {
            throw new CommonException(ErrorCodeEnum.ERROR_WEB_TENANT_IS_ILLEGAL);
        }

        WeComCorpMessageEntity entity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);
        String domain = tenantConfigEntity.getServerAddress();
        WeComCorpDomainResponse response = new WeComCorpDomainResponse();
        if (StringUtils.isNotEmpty(domain)) {
            String[] splitString = domain.split("/");
            domain = splitString[0] + "//" + splitString[2];
        }

        response.setDomainUrl(domain);
        if (StringUtils.isNotEmpty(entity.getCredFileName())) {
            response.setCredFileName(entity.getCredFileName());
        } else {
            response.setCredFileName("");
        }
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse deleteCredFile(String projectId, String corpId, String fileName) {
        WeComCorpMessageEntity entity = weComCorpMessageRepository.getCorpConfigByCorpId(corpId);
        if (entity.getCredFileName().equals(fileName)) {
            weComCorpMessageRepository.updateCredFileMessageByCorpId(projectId, corpId, "",
                    "");
        }
        return BaseResponse.success();
    }

}
