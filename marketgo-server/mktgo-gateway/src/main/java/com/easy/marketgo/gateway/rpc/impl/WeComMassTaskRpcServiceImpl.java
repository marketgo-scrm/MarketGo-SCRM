package com.easy.marketgo.gateway.rpc.impl;

import com.easy.marketgo.api.model.request.masstask.*;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.service.WeComMassTaskRpcService;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.gateway.wecom.request.masstask.*;
import com.easy.marketgo.gateway.wecom.sevice.MassTaskManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-24 21:03
 * Describe:
 */
@Slf4j
@DubboService
public class WeComMassTaskRpcServiceImpl implements WeComMassTaskRpcService {

    @Autowired
    private MassTaskManagerService massTaskManagerService;

    @Override
    public RpcResponse sendMassTask(WeComMassTaskClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request send mass task param. request={}", request);
        SendMassTaskRequest requestBody = new SendMassTaskRequest();
        requestBody.setChatType(request.getChatType());
        requestBody.setSender(request.getSender());
        requestBody.setExternalUserid(request.getExternalUserId());
        SendMassTaskRequest.TextMessage textMessage = new SendMassTaskRequest.TextMessage();
        textMessage.setContent(request.getText().getContent());
        requestBody.setText(textMessage);
        if (CollectionUtils.isNotEmpty(request.getAttachments())) {
            List<SendMassTaskRequest.AttachmentsMessage> attachmentsMessageList = new ArrayList<>();
            request.getAttachments().forEach(attach -> {

                SendMassTaskRequest.AttachmentsMessage attachmentsMessage =
                        new SendMassTaskRequest.AttachmentsMessage();
                BeanUtils.copyProperties(attach, attachmentsMessage);
                if (attach.getImage() != null) {
                    SendMassTaskRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                            new SendMassTaskRequest.ImageAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getImage(), imageAttachmentsMessage);
                    attachmentsMessage.setImage(imageAttachmentsMessage);
                } else if (attach.getLink() != null) {
                    SendMassTaskRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                            new SendMassTaskRequest.LinkAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getLink(), linkAttachmentsMessage);
                    attachmentsMessage.setLink(linkAttachmentsMessage);
                } else if (attach.getMiniProgram() != null) {
                    SendMassTaskRequest.MiniprogramAttachmentsMessage miniProgramAttachmentsMessage =
                            new SendMassTaskRequest.MiniprogramAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getMiniProgram(), miniProgramAttachmentsMessage);
                    attachmentsMessage.setMiniprogram(miniProgramAttachmentsMessage);
                } else if (attach.getVideo() != null) {
                    SendMassTaskRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                            new SendMassTaskRequest.VideoAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getVideo(), videoAttachmentsMessage);
                    attachmentsMessage.setVideo(videoAttachmentsMessage);
                } else if (attach.getFile() != null) {
                    SendMassTaskRequest.FileAttachmentsMessage fileAttachmentsMessage =
                            new SendMassTaskRequest.FileAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getFile(), fileAttachmentsMessage);
                    attachmentsMessage.setFile(fileAttachmentsMessage);
                }
                attachmentsMessageList.add(attachmentsMessage);
            });
            requestBody.setAttachments(attachmentsMessageList);
        }
        return massTaskManagerService.sendMassTask(request.getCorpId(), request.getAgentId(), requestBody);
    }

    @Override
    public RpcResponse sendMomentMassTask(WeComMomentMassTaskClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request send moment mass task param. request={}", request);
        SendMomentMassTaskRequest requestBody = new SendMomentMassTaskRequest();
        SendMomentMassTaskRequest.VisibleRangeMessage visibleRangeMessage =
                new SendMomentMassTaskRequest.VisibleRangeMessage();

        BeanUtils.copyProperties(request.getVisibleRange(), visibleRangeMessage);
        requestBody.setVisibleRange(visibleRangeMessage);
        SendMomentMassTaskRequest.TextMessage textMessage = new SendMomentMassTaskRequest.TextMessage();
        BeanUtils.copyProperties(request.getText(), textMessage);
        requestBody.setText(textMessage);
        if (CollectionUtils.isNotEmpty(request.getAttachments())) {
            List<SendMomentMassTaskRequest.AttachmentsMessage> attachmentsMessageList = new ArrayList<>();
            request.getAttachments().forEach(attach -> {
                SendMomentMassTaskRequest.AttachmentsMessage attachmentsMessage =
                        new SendMomentMassTaskRequest.AttachmentsMessage();

                BeanUtils.copyProperties(attach, attachmentsMessage);
                if (attach.getImage() != null) {
                    SendMomentMassTaskRequest.ImageAttachmentsMessage imageAttachmentsMessage =
                            new SendMomentMassTaskRequest.ImageAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getImage(), imageAttachmentsMessage);
                    attachmentsMessage.setImage(imageAttachmentsMessage);
                } else if (attach.getLink() != null) {
                    SendMomentMassTaskRequest.LinkAttachmentsMessage linkAttachmentsMessage =
                            new SendMomentMassTaskRequest.LinkAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getLink(), linkAttachmentsMessage);
                    attachmentsMessage.setLink(linkAttachmentsMessage);
                } else if (attach.getVideo() != null) {
                    SendMomentMassTaskRequest.VideoAttachmentsMessage videoAttachmentsMessage =
                            new SendMomentMassTaskRequest.VideoAttachmentsMessage();
                    BeanUtils.copyProperties(attach.getVideo(), videoAttachmentsMessage);
                    attachmentsMessage.setVideo(videoAttachmentsMessage);
                }
                attachmentsMessageList.add(attachmentsMessage);
            });
            requestBody.setAttachments(attachmentsMessageList);
        }
        return massTaskManagerService.sendMomentMassTask(request.getCorpId(), request.getAgentId(), requestBody);
    }

    @Override
    public RpcResponse queryMassTaskForMemberResult(WeComQueryMemberResultClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query mass task member result param. request={}", request);
        QueryMassTaskMemberResultRequest requestBody = new QueryMassTaskMemberResultRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.queryMassTaskMemberResult(request.getCorpId(), request.getAgentId(), requestBody);
    }

    @Override
    public RpcResponse queryMassTaskForExternalUserResult(WeComQueryExternalUserResultClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query mass task external user result param. request={}", request);
        QueryMassTaskExternalUserResultRequest requestBody = new QueryMassTaskExternalUserResultRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.queryMassTaskExternalUserResult(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse queryMomentMassTaskForCreateResult(WeComMomentMassTaskCreateResultClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query moment mass task create result param. request={}", request);
        QueryMomentMassTaskCreateResultRequest requestBody = new QueryMomentMassTaskCreateResultRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.queryMomentMassTaskCreateResult(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse queryMomentMassTaskForPublishResult(WeComMomentMassTaskPublishResultClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query moment mass task member public result param. request={}", request);
        QueryMomentMassTaskMemberResultRequest requestBody = new QueryMomentMassTaskMemberResultRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.queryMomentMassTaskPublishResult(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse queryMomentMassTaskForSendResult(WeComMomentMassTaskSendResultClientRequest request) {
        if (request == null) {
            log.error("query moment mass task for send result param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query moment mass task member send result param. request={}", request);
        QueryMomentMassTaskExternalUserResultRequest requestBody = new QueryMomentMassTaskExternalUserResultRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.queryMomentMassTaskSendResult(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse queryMomentMassTaskForCommentsResult(WeComMomentMassTaskCommentsClientRequest request) {
        if (request == null) {
            log.error("send moment mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query mass task comments result param. request={}", request);
        QueryMomentMassTaskCommentsRequest requestBody = new QueryMomentMassTaskCommentsRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.queryMomentMassTaskCommentsResult(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse remindMemberMessage(WeComRemindMemberMessageClientRequest request) {
        if (request == null) {
            log.error("send remind member message for mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query mass task remind member message param. request={}", request);
        RemindMemberMessageRequest requestBody = new RemindMemberMessageRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.memberRemindMessage(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse stopMassTask(WeComRemindMemberMessageClientRequest request) {
        if (request == null) {
            log.error("send stop message for mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query stop message param. request={}", request);
        RemindMemberMessageRequest requestBody = new RemindMemberMessageRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.stopMassTask(request.getCorpId(), request.getAgentId(),
                requestBody);
    }

    @Override
    public RpcResponse stopMomentMassTask(WeComStopMomentMassTaskClientRequest request) {
        if (request == null) {
            log.error("send stop moment message for mass task param is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        log.info("rpc request query stop moment message param. request={}", request);
        StopMomentMassTaskRequest requestBody = new StopMomentMassTaskRequest();
        BeanUtils.copyProperties(request, requestBody);
        return massTaskManagerService.stopMomentMassTask(request.getCorpId(), request.getAgentId(),
                requestBody);
    }
}
