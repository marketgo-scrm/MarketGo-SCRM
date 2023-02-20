package com.easy.marketgo.gateway.wecom.sevice;

import com.alibaba.fastjson.JSON;
import com.easy.marketgo.api.model.response.RpcResponse;
import com.easy.marketgo.api.model.response.masstask.*;
import com.easy.marketgo.common.constants.wecom.WeComHttpConstants;
import com.easy.marketgo.common.enums.ErrorCodeEnum;
import com.easy.marketgo.common.utils.JsonUtils;
import com.easy.marketgo.core.model.wecom.WeComBaseResponse;
import com.easy.marketgo.core.service.wecom.token.AccessTokenManagerService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.gateway.wecom.request.masstask.*;
import com.easy.marketgo.gateway.wecom.response.SendAgentMessageResponse;
import com.easy.marketgo.gateway.wecom.response.masstask.*;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 15:05
 * Describe:
 */
@Slf4j
@Service
public class MassTaskManagerService {

    @Autowired
    private AccessTokenManagerService accessTokenManagerService;

    /**
     * 企业发表内容到客户的朋友圈 https://developer.work.weixin.qq.com/document/path/95094
     *
     * @param corpId
     * @param agentId
     * @param request
     */
    public RpcResponse sendMomentMassTask(String corpId, String agentId, SendMomentMassTaskRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("send moment mass task is failed for request param is null.corpId={}, agentId={}, request={}",
                    corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComSendMomentMassTaskClientResponse clientResponse = new WeComSendMomentMassTaskClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("start send moment mass task. requestBody={}, paramsHeader={}, url={}", requestBody, paramsHeader,
                WeComHttpConstants.SEND_MOMENT_MASS_TASK_URL);
        String response = null;
        try {
            response =
                    OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.SEND_MOMENT_MASS_TASK_URL, paramsHeader
                            , requestBody);
        } catch (Exception e) {
            log.error("failed to send moment mass task. corpId={}, agentId={}, request={}", corpId, agentId, request,
                    e);
        }

        if (StringUtils.isEmpty(response)) {
            log.error("failed to send moment mass task. corpId={}, agentId={}, request={}", corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("send moment mass task response from weCom. response={}", StringUtils.isEmpty(response) ? "" :
                response);
        SendMomentMassTaskResponse sendMomentMassTaskResponse =
                JsonUtils.toObject(response, SendMomentMassTaskResponse.class);
        rpcResponse.setCode(sendMomentMassTaskResponse.getErrcode());
        rpcResponse.setMessage(sendMomentMassTaskResponse.getErrmsg());
        if (!sendMomentMassTaskResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to send moment task. response={}", response);
        }
        clientResponse.setJobId(sendMomentMassTaskResponse.getJobId());
        rpcResponse.setData(clientResponse);
        log.info("return send moment mass task result. commonResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 企业群发给个人、客户群 https://developer.work.weixin.qq.com/document/path/92135
     *
     * @param corpId
     * @param agentId
     * @param request
     */
    public RpcResponse sendMassTask(String corpId, String agentId, SendMassTaskRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.error("send single or group task is failed for request param is null. corpId={}, agentId={}, " +
                    "request={}", corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComSendMassTaskClientResponse clientResponse = new WeComSendMassTaskClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        log.info("start send single or group mass task.corpId={}, agentId={}, request={}", corpId, agentId, request);
        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("send single or group mass task param. requestBody={}, paramsHeader={}, url={}", requestBody,
                paramsHeader, WeComHttpConstants.SEND_SINGLE_OR_GROUP_MASS_TASK_URL);
        String response = null;
        try {
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.SEND_SINGLE_OR_GROUP_MASS_TASK_URL,
                    paramsHeader,
                    requestBody);
        } catch (Exception e) {
            log.error("failed to send mass task. corpId={}, agentId={}, request={}", corpId, agentId, request,
                    e);
        }

        if (StringUtils.isBlank(response)) {
            log.error("failed to send single or group mass task. corpId={}, agentId={}, request={}", corpId, agentId,
                    request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }

        log.info("send single or group mass task response from weCom. response={}", response);
        SendMassTaskResponse sendMassTaskResponse = JsonUtils.toObject(response, SendMassTaskResponse.class);
        rpcResponse.setCode(sendMassTaskResponse.getErrcode());
        rpcResponse.setMessage(sendMassTaskResponse.getErrmsg());
        if (!sendMassTaskResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to send single or group mass task, return error message. response={}",
                    sendMassTaskResponse);
        }

        clientResponse.setFailList(sendMassTaskResponse.getFailList());
        clientResponse.setMsgId(sendMassTaskResponse.getMsgId());
        rpcResponse.setData(clientResponse);
        log.info("return send mass task result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 获取群发成员发送任务列表 https://developer.work.weixin.qq.com/document/path/93338
     *
     * @param corpId  企业id
     * @param agentId 应用id
     * @param request 请求的参数
     * @return
     */
    public RpcResponse queryMassTaskMemberResult(String corpId, String agentId,
                                                 QueryMassTaskMemberResultRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("query mass task is failed for request is null. corpId={}, agentId={}, request={}", corpId,
                    agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComQueryMemberResultClientResponse clientResponse = new WeComQueryMemberResultClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("start get single or group task send result. requestBody={}, paramsHeader={}, url={}", requestBody,
                paramsHeader, WeComHttpConstants.QUERY_MASS_TASK_MEMBER_RESULT_URL);
        String response = null;
        try {
            response =
                    OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_MASS_TASK_MEMBER_RESULT_URL,
                            paramsHeader, requestBody);
        } catch (Exception e) {
            log.error("failed to  query mass task member result. corpId={}, agentId={}, request={}", corpId, agentId,
                    request, e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        if (StringUtils.isBlank(response)) {
            log.error("failed to query mass task member result. corpId={}, agentId={}, request={}", corpId, agentId,
                    request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("query mass task member result response from weCom. response={}", response);
        QueryMassTaskMemberResultResponse queryMassTaskMemberResultResponse =
                JsonUtils.toObject(response, QueryMassTaskMemberResultResponse.class);
        if (!queryMassTaskMemberResultResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to get single or group task send result. response={}", response);
        }

        rpcResponse.setCode(queryMassTaskMemberResultResponse.getErrcode());
        rpcResponse.setMessage(queryMassTaskMemberResultResponse.getErrmsg());
        clientResponse.setNextCursor(queryMassTaskMemberResultResponse.getNextCursor());
        List<WeComQueryMemberResultClientResponse.TaskListMessage> taskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryMassTaskMemberResultResponse.getTaskList())) {
            for (QueryMassTaskMemberResultResponse.TaskListMessage item :
                    queryMassTaskMemberResultResponse.getTaskList()) {
                WeComQueryMemberResultClientResponse.TaskListMessage message =
                        new WeComQueryMemberResultClientResponse.TaskListMessage();
                BeanUtils.copyProperties(item, message);
                taskList.add(message);
            }
        }
        clientResponse.setTaskList(taskList);
        rpcResponse.setData(clientResponse);
        log.info("return query mass task member result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 获取企业群发成员执行结果 https://developer.work.weixin.qq.com/document/path/93338
     *
     * @param corpId  企业id
     * @param agentId 应用id
     * @param request 请求的参数
     * @return
     */
    public RpcResponse queryMassTaskExternalUserResult(String corpId, String agentId,
                                                       QueryMassTaskExternalUserResultRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("query mass task is failed for request param is null. corpId={}, agentId={}, request={}", corpId
                    , agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComQueryExternalUserResultClientResponse clientResponse = new WeComQueryExternalUserResultClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();
        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("start query single or group task external user result. requestBody={}, paramsHeader={}, url={}",
                requestBody,
                paramsHeader, WeComHttpConstants.QUERY_MASS_TASK_EXTERNAL_USER_RESULT_URL);
        String response = null;
        try {
            response =
                    OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_MASS_TASK_EXTERNAL_USER_RESULT_URL,
                            paramsHeader, requestBody);
        } catch (Exception e) {
            log.error("failed to query single or group external user result. corpId={}, agentId={}, request={}", corpId,
                    agentId, request, e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }

        if (StringUtils.isBlank(response)) {
            log.error("failed to query single or group task external user result.corpId={}, agentId={}, request={}",
                    corpId, agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("query mass task external user result response from weCom. response={}", response);
        QueryMassTaskExternalUserResultResponse queryMassTaskExternalUserResultResponse = JsonUtils.toObject(response
                , QueryMassTaskExternalUserResultResponse.class);
        if (!queryMassTaskExternalUserResultResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query mass task external user result. response={}", response);
        }
        rpcResponse.setCode(queryMassTaskExternalUserResultResponse.getErrcode());
        rpcResponse.setMessage(queryMassTaskExternalUserResultResponse.getErrmsg());
        clientResponse.setNextCursor(queryMassTaskExternalUserResultResponse.getNextCursor());
        List<WeComQueryExternalUserResultClientResponse.SendListMessage> taskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryMassTaskExternalUserResultResponse.getSendList())) {
            for (QueryMassTaskExternalUserResultResponse.SendListMessage item :
                    queryMassTaskExternalUserResultResponse.getSendList()) {
                WeComQueryExternalUserResultClientResponse.SendListMessage message =
                        new WeComQueryExternalUserResultClientResponse.SendListMessage();
                BeanUtils.copyProperties(item, message);
                taskList.add(message);
            }
        }
        clientResponse.setSendList(taskList);
        rpcResponse.setData(clientResponse);
        log.info("return query mass task external user result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 获取朋友圈的任务创建结果 https://developer.work.weixin.qq.com/document/path/95094
     *
     * @param corpId  企业id
     * @param agentId 应用id
     * @param request 请求的参数
     * @return
     */
    public RpcResponse queryMomentMassTaskCreateResult(String corpId, String agentId,
                                                       QueryMomentMassTaskCreateResultRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("query moment mass task for request param is null. corpId={}, agentId={}, request={}", corpId,
                    agentId, request);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComMassTaskForMomentCreateResponse clientResponse = new WeComMassTaskForMomentCreateResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        paramsHeader.put("jobid", request.getJobid());
        log.info("start query moment mass task create result. paramsHeader={}, url={}", paramsHeader,
                WeComHttpConstants.QUERY_MOMENT_MASS_TASK_CREATE_RESULT_URL);
        String response = null;
        try {
            response =
                    OkHttpUtils.getInstance().getDataSync(WeComHttpConstants.QUERY_MOMENT_MASS_TASK_CREATE_RESULT_URL
                            , paramsHeader);
        } catch (Exception e) {
            log.error("failed to query moment mass task create result. corpId={}, agentId={}, request={}", corpId,
                    agentId, request, e);
        }
        if (StringUtils.isBlank(response)) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("query moment mass task create result response from weCom. response={}", response);
        QueryMomentMassTaskCreateResultResponse queryMomentMassTaskCreateResultResponse =
                JsonUtils.toObject(response, QueryMomentMassTaskCreateResultResponse.class);
        if (!queryMomentMassTaskCreateResultResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query moment mass task create result. response={}", response);
        }

        rpcResponse.setCode(queryMomentMassTaskCreateResultResponse.getErrcode());
        rpcResponse.setMessage(queryMomentMassTaskCreateResultResponse.getErrmsg());
        clientResponse.setStatus(queryMomentMassTaskCreateResultResponse.getStatus());
        clientResponse.setType(queryMomentMassTaskCreateResultResponse.getType());
        WeComMassTaskForMomentCreateResponse.ResultMessage message =
                new WeComMassTaskForMomentCreateResponse.ResultMessage();
        BeanUtils.copyProperties(queryMomentMassTaskCreateResultResponse.getResult(), message);
        clientResponse.setResult(message);
        rpcResponse.setData(clientResponse);
        log.info("return query moment mass task create result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 获取客户朋友圈的发布状态 https://developer.work.weixin.qq.com/document/path/93333
     *
     * @param corpId
     * @param agentId
     * @param request
     * @return
     */
    public RpcResponse queryMomentMassTaskPublishResult(String corpId,
                                                        String agentId,
                                                        QueryMomentMassTaskMemberResultRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("failed to get moment task  publish result for request is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComMomentMassTaskPublishResultClientResponse clientResponse =
                new WeComMomentMassTaskPublishResultClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("start query moment publish result. requestBody={}, paramsHeader={}, url={}", requestBody,
                paramsHeader, WeComHttpConstants.QUERY_MOMENT_MASS_TASK_PUBLISH_RESULT_URL);
        String response = null;
        try {
            response =
                    OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_MOMENT_MASS_TASK_PUBLISH_RESULT_URL,
                            paramsHeader, requestBody);
        } catch (Exception e) {
            log.error("failed to query moment publish result. corpId={}, agentId={}, request={}", corpId,
                    agentId, request, e);
        }
        if (StringUtils.isBlank(response)) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("query moment publish result response from weCom. response={}", response);

        QueryMomentMassTaskMemberResultResponse queryMomentMassTaskMemberResultResponse =
                JsonUtils.toObject(response, QueryMomentMassTaskMemberResultResponse.class);
        if (!queryMomentMassTaskMemberResultResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query moment mass task publish result. response={}", response);
        }
        rpcResponse.setCode(queryMomentMassTaskMemberResultResponse.getErrcode());
        rpcResponse.setMessage(queryMomentMassTaskMemberResultResponse.getErrmsg());
        clientResponse.setNextCursor(queryMomentMassTaskMemberResultResponse.getNextCursor());
        List<WeComMomentMassTaskPublishResultClientResponse.TaskListMessage> taskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryMomentMassTaskMemberResultResponse.getTaskList())) {
            for (QueryMomentMassTaskMemberResultResponse.TaskListMessage item :
                    queryMomentMassTaskMemberResultResponse.getTaskList()) {
                WeComMomentMassTaskPublishResultClientResponse.TaskListMessage message =
                        new WeComMomentMassTaskPublishResultClientResponse.TaskListMessage();
                BeanUtils.copyProperties(item, message);
                taskList.add(message);
            }
        }
        clientResponse.setTaskList(taskList);
        rpcResponse.setData(clientResponse);
        log.info("return query moment publish result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 获取客户朋友圈的发布状态 https://developer.work.weixin.qq.com/document/path/93333
     *
     * @param corpId
     * @param agentId
     * @param request
     * @return
     */
    public RpcResponse queryMomentMassTaskSendResult(String corpId, String agentId,
                                                     QueryMomentMassTaskExternalUserResultRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("failed to get moment task  send result for request is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComMomentMassTaskSendResultClientResponse clientResponse = new WeComMomentMassTaskSendResultClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("start query moment send result. requestBody={}, paramsHeader={}, url={}", requestBody,
                paramsHeader, WeComHttpConstants.QUERY_MOMENT_MASS_TASK_SEND_RESULT_URL);
        String response = null;
        try {
            response =
                    OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_MOMENT_MASS_TASK_SEND_RESULT_URL,
                            paramsHeader, requestBody);
        } catch (Exception e) {
            log.error("failed to query moment send result. corpId={}, agentId={}, request={}", corpId,
                    agentId, request, e);
        }
        if (StringUtils.isBlank(response)) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("query moment send result response from weCom. response={}", response);

        QueryMomentMassTaskSendResultResponse queryMomentMassTaskSendResultResponse =
                JsonUtils.toObject(response, QueryMomentMassTaskSendResultResponse.class);
        if (!queryMomentMassTaskSendResultResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query moment mass task send result. response={}", response);
        }
        rpcResponse.setCode(queryMomentMassTaskSendResultResponse.getErrcode());
        rpcResponse.setMessage(queryMomentMassTaskSendResultResponse.getErrmsg());
        clientResponse.setNextCursor(queryMomentMassTaskSendResultResponse.getNextCursor());
        List<WeComMomentMassTaskSendResultClientResponse.CustomerListMessage> taskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryMomentMassTaskSendResultResponse.getCustomerList())) {
            for (QueryMomentMassTaskSendResultResponse.CustomerListMessage item :
                    queryMomentMassTaskSendResultResponse.getCustomerList()) {
                WeComMomentMassTaskSendResultClientResponse.CustomerListMessage message =
                        new WeComMomentMassTaskSendResultClientResponse.CustomerListMessage();
                BeanUtils.copyProperties(item, message);
                taskList.add(message);
            }
        }
        clientResponse.setCustomerList(taskList);
        rpcResponse.setData(clientResponse);
        log.info("return query moment send result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 获取客户朋友圈的互动数据
     * https://developer.work.weixin.qq.com/document/path/93333
     *
     * @param corpId  企业id
     * @param agentId 应用id
     * @param request 请求的参数
     * @return
     */
    public RpcResponse queryMomentMassTaskCommentsResult(String corpId, String agentId,
                                                         QueryMomentMassTaskCommentsRequest request) {
        if (StringUtils.isBlank(corpId) || StringUtils.isBlank(agentId) || request == null) {
            log.info("query moment task comments is failed for request is null");
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_PARAM_IS_EMPTY);
        }
        WeComMomentMassTaskCommentsClientResponse clientResponse =
                new WeComMomentMassTaskCommentsClientResponse();
        RpcResponse rpcResponse = RpcResponse.success();

        String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);
        Map<String, String> paramsHeader = new HashMap<>();
        paramsHeader.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
        String requestBody = JsonUtils.toJSONString(request);
        log.info("start query moment task comments result. requestBody={}, paramsHeader={}, url={}", requestBody,
                paramsHeader, WeComHttpConstants.QUERY_MOMENT_MASS_TASK_COMMENTS_URL);
        String response = null;
        try {
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.QUERY_MOMENT_MASS_TASK_COMMENTS_URL,
                    paramsHeader, requestBody);
        } catch (Exception e) {
            log.error("failed to query moment publish result. corpId={}, agentId={}, request={}", corpId,
                    agentId, request, e);
        }
        if (StringUtils.isBlank(response)) {
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
        }
        log.info("query moment mass task comments result response from weCom. response={}", response);

        QueryMomentMassTaskForCommentsResponse queryMomentMassTaskForCommentsResponse =
                JsonUtils.toObject(response, QueryMomentMassTaskForCommentsResponse.class);
        if (!queryMomentMassTaskForCommentsResponse.getErrcode().equals(ErrorCodeEnum.OK.getCode())) {
            log.error("failed to query moment mass task comments result. response={}", response);
        }
        rpcResponse.setCode(queryMomentMassTaskForCommentsResponse.getErrcode());
        rpcResponse.setMessage(queryMomentMassTaskForCommentsResponse.getErrmsg());

        List<WeComMomentMassTaskCommentsClientResponse.CommentMessage> commentList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryMomentMassTaskForCommentsResponse.getCommentList())) {
            for (QueryMomentMassTaskForCommentsResponse.ResultCommentList item :
                    queryMomentMassTaskForCommentsResponse.getCommentList()) {
                WeComMomentMassTaskCommentsClientResponse.CommentMessage message =
                        new WeComMomentMassTaskCommentsClientResponse.CommentMessage();
                BeanUtils.copyProperties(item, message);
                commentList.add(message);
            }
        }
        clientResponse.setCommentList(commentList);

        List<WeComMomentMassTaskCommentsClientResponse.CommentMessage> likeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryMomentMassTaskForCommentsResponse.getLikeList())) {
            for (QueryMomentMassTaskForCommentsResponse.ResultCommentList item :
                    queryMomentMassTaskForCommentsResponse.getLikeList()) {
                WeComMomentMassTaskCommentsClientResponse.CommentMessage message =
                        new WeComMomentMassTaskCommentsClientResponse.CommentMessage();
                BeanUtils.copyProperties(item, message);
                likeList.add(message);
            }
        }
        clientResponse.setLikeList(likeList);

        rpcResponse.setData(clientResponse);
        log.info("return query moment mass task comments result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 提醒成员群发
     * https://developer.work.weixin.qq.com/document/path/97610
     *
     * @param corpId
     * @param agentId
     * @param request
     * @return
     */
    public RpcResponse memberRemindMessage(String corpId, String agentId, RemindMemberMessageRequest request) {
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("send remind message. headerParams={}, requestBody={}", JSON.toJSONString(params), requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.REMIND_MESSAGE_URL, params,
                    requestBody);
            if (StringUtils.isBlank(response)) {
                log.error("failed to send remind message is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("send remind message response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response,
                    WeComBaseResponse.class);
            if (weComBaseResponse != null) {
                rpcResponse.setCode(weComBaseResponse.getErrcode());
                rpcResponse.setMessage(weComBaseResponse.getErrmsg());
            }

        } catch (Exception e) {
            log.error("failed to send remind msg. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("return send remind msg rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 停止企业群发
     * https://developer.work.weixin.qq.com/document/path/97611
     *
     * @param corpId
     * @param agentId
     * @param request
     * @return
     */
    public RpcResponse stopMassTask(String corpId, String agentId, RemindMemberMessageRequest request) {
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("stop mass task message. headerParams={}, requestBody={}", JSON.toJSONString(params), requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.STOP_MASS_TASK_MESSAGE_URL, params,
                    requestBody);
            if (StringUtils.isBlank(response)) {
                log.error("failed to stop mass task message is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("send stop mass task message response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response,
                    SendAgentMessageResponse.class);
            if (weComBaseResponse != null) {
                rpcResponse.setCode(weComBaseResponse.getErrcode());
                rpcResponse.setMessage(weComBaseResponse.getErrmsg());
            }
        } catch (Exception e) {
            log.error("failed to send stop mass task message. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("finish to stop mass task message rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }

    /**
     * 停止朋友圈
     * https://developer.work.weixin.qq.com/document/path/97612
     *
     * @param corpId
     * @param agentId
     * @param request
     * @return
     */
    public RpcResponse stopMomentMassTask(String corpId, String agentId, StopMomentMassTaskRequest request) {
        RpcResponse rpcResponse = RpcResponse.success();
        try {
            String accessToken = accessTokenManagerService.getAgentAccessToken(corpId, agentId);

            Map<String, String> params = Maps.newHashMap();
            params.put(WeComHttpConstants.AGENT_ACCESS_TOKEN, accessToken);
            String requestBody = JsonUtils.toJSONString(request);
            log.info("stop moment mass task message. headerParams={}, requestBody={}", JSON.toJSONString(params),
                    requestBody);
            String response = null;
            response = OkHttpUtils.getInstance().postJsonSync(WeComHttpConstants.STOP_MOMENT_MASS_TASK_MESSAGE_URL, params,
                    requestBody);
            if (StringUtils.isBlank(response)) {
                log.error("failed to stop moment mass task message is empty. corpId={}, request={}", corpId, request);
                return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_REQUEST_WECOM);
            }
            log.info("send stop moment mass task message response from weCom. response={}", response);
            WeComBaseResponse weComBaseResponse = JsonUtils.toObject(response,
                    SendAgentMessageResponse.class);
            if (weComBaseResponse != null) {
                rpcResponse.setCode(weComBaseResponse.getErrcode());
                rpcResponse.setMessage(weComBaseResponse.getErrmsg());
            }
        } catch (Exception e) {
            log.error("failed to send stop moment mass task message. corpId={}, request={}", corpId, request
                    , e);
            return RpcResponse.failure(ErrorCodeEnum.ERROR_GATEWAY_INTERNAL_SERVICE);
        }
        log.info("finish to stop moment mass task message rpc result. rpcResponse={}", rpcResponse);
        return rpcResponse;
    }
}
