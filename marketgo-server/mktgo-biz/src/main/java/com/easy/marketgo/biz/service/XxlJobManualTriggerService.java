package com.easy.marketgo.biz.service;

import com.easy.marketgo.biz.config.XxlJobConfig;
import com.easy.marketgo.common.utils.JsonUtils;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.ExecutorBizClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/10/22 10:29 PM
 * Describe:
 */
@Service
@Slf4j
public class XxlJobManualTriggerService {

    @Autowired
    private XxlJobConfig xxlJobConfig;

    public void manualTriggerHandler(String handler) {
        log.info("trigger sync contacts.");
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setExecutorHandler(handler);
        triggerParam.setExecutorParams("手动触发同步通讯录的任务");
        // 任务阻塞策略，可选值参考 com.xxl.job.core.enums.ExecutorBlockStrategyEnum
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.COVER_EARLY.name());
        // 任务模式，可选值参考 com.xxl.job.core.glue.GlueTypeEnum
        triggerParam.setGlueType(GlueTypeEnum.BEAN.name());
        // GLUE脚本代码
        triggerParam.setGlueSource(null);
        // GLUE脚本更新时间，用于判定脚本是否变更以及是否需要刷新
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        // 本次调度日志ID
        triggerParam.setLogId(triggerParam.getJobId());
        // 本次调度日志时间
        triggerParam.setLogDateTime(System.currentTimeMillis());

        String adminClientAddressUrl = "http://" + xxlJobConfig.getIp() + ":" + xxlJobConfig.getPort();
        String accessToken = xxlJobConfig.getAccessToken();
        log.info("adminClientAddressUrl={},accessToken={}", adminClientAddressUrl, accessToken);
        ExecutorBiz executorBiz = new ExecutorBizClient(adminClientAddressUrl, accessToken);
        ReturnT<String> retval = executorBiz.run(triggerParam);
        log.info("retval={}", JsonUtils.toJSONString(retval));
    }
}
