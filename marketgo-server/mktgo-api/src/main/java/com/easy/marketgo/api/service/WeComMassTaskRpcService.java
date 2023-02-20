package com.easy.marketgo.api.service;

import com.easy.marketgo.api.model.request.masstask.*;
import com.easy.marketgo.api.model.response.*;
/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-27 19:19
 * Describe:
 */
public interface WeComMassTaskRpcService {

    // 发送群发好友、客户群任务
    RpcResponse sendMassTask(WeComMassTaskClientRequest request);

    // 发送群发朋友圈任务
    RpcResponse sendMomentMassTask(WeComMomentMassTaskClientRequest request);

    //获取群发成员发送任务列表
    RpcResponse queryMassTaskForMemberResult(WeComQueryMemberResultClientRequest request);

    //获取企业群发成员执行结果
    RpcResponse queryMassTaskForExternalUserResult(WeComQueryExternalUserResultClientRequest request);

    //获取朋友圈任务创建结果
    RpcResponse queryMomentMassTaskForCreateResult(WeComMomentMassTaskCreateResultClientRequest request);

    //获取朋友圈任务发布结果
    RpcResponse queryMomentMassTaskForPublishResult(WeComMomentMassTaskPublishResultClientRequest request);

    //获取朋友圈任务发布可见客户结果
    RpcResponse queryMomentMassTaskForSendResult(WeComMomentMassTaskSendResultClientRequest request);

    //获取客户朋友圈的互动数据
    RpcResponse queryMomentMassTaskForCommentsResult(WeComMomentMassTaskCommentsClientRequest request);

    //提醒成员群发
    RpcResponse remindMemberMessage(WeComRemindMemberMessageClientRequest request);

    //停止企业群发
    RpcResponse stopMassTask(WeComRemindMemberMessageClientRequest request);

    //停止朋友圈群发
    RpcResponse stopMomentMassTask(WeComStopMomentMassTaskClientRequest request);
}
