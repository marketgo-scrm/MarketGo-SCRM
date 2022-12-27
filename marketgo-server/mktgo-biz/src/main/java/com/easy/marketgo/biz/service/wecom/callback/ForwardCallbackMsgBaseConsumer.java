package com.easy.marketgo.biz.service.wecom.callback;

import com.easy.marketgo.common.constants.Constants;
import com.easy.marketgo.core.model.callback.WeComForwardCallbackMsg;
import com.easy.marketgo.core.redis.RedisService;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 12/5/22 10:44 AM
 * Describe:
 */
@Slf4j
@Component
public class ForwardCallbackMsgBaseConsumer {

    @Autowired
    private RedisService redisService;

    private ExecutorService executorService;

    private static final Integer CAPACITY_ONE = 1000;
    private static final Integer CORE_THREAD_NUM = 10;

    @PostConstruct
    public void init() {
        initThreadPool();
    }

    private void initThreadPool() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setThreadFactory(Thread::new)
                .setNameFormat("wx_query_mass_task_pool_%d")
                .setDaemon(true)
                .build();
        long keepAliveTime = 10_000L;
        long terminationTimeOut = 30_000L;
        ThreadPoolExecutor replyThreadPool = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 4, keepAliveTime,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(CAPACITY_ONE), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        executorService = getExitingExecutorService(replyThreadPool, terminationTimeOut, TimeUnit.MILLISECONDS);
    }

    protected void sendWeComMemberCallbackMsg(WeComForwardCallbackMsg sendData) {

        if (sendData == null) {
            return;
        }

        String value = redisService.get(String.format(Constants.WECOM_CALLBACK_FORWARD_URL, sendData.getCorpId()));

        if (StringUtils.isBlank(value)) {
            return;
        }
        List<String> url = Arrays.asList(value.split(","));
        if (sendData.getMsgType() == HttpMethod.GET) {
            sendWeComMemberCallbackMsgForGetRequest(sendData, url);
        } else {
            sendWeComMemberCallbackMsgForPostRequest(sendData, url);
        }
    }

    protected void sendWeComCustomerCallbackMsg(WeComForwardCallbackMsg sendData) {

        if (sendData == null) {
            return;
        }

        String value = redisService.get(String.format(Constants.WECOM_CALLBACK_CUSTOMER_FORWARD_URL, sendData.getCorpId()));

        if (StringUtils.isBlank(value)) {
            return;
        }
        List<String> url = Arrays.asList(value.split(","));
        if (sendData.getMsgType() == HttpMethod.GET) {
            sendWeComMemberCallbackMsgForGetRequest(sendData, url);
        } else {
            sendWeComMemberCallbackMsgForPostRequest(sendData, url);
        }
    }

    private void sendWeComMemberCallbackMsgForGetRequest(WeComForwardCallbackMsg sendData, List<String> url) {
        Map<String, String> params = Maps.newHashMap();
        params.put("msg_signature", sendData.getMsgSignature());
        params.put("timestamp", sendData.getTimestamp());
        params.put("nonce", sendData.getNonce());
        params.put("echostr", sendData.getEchostr());
        params.put("corp_id", sendData.getCorpId());

        for (String item : url) {
            Runnable task =
                    new WeComSendGetRequest(params, item);
            try {
                executorService.submit(task);
            } catch (Exception e) {
                log.error("failed to forward callback message from pool. url={}", item, e);
            }
        }
    }

    private void sendWeComMemberCallbackMsgForPostRequest(WeComForwardCallbackMsg sendData, List<String> url) {
        Map<String, String> params = Maps.newHashMap();
        params.put("msg_signature", sendData.getMsgSignature());
        params.put("timestamp", sendData.getTimestamp());
        params.put("nonce", sendData.getNonce());
        params.put("echostr", sendData.getEchostr());
        params.put("corp_id", sendData.getCorpId());

        for (String item : url) {
            Runnable task =
                    new WeComSendPostRequest(params, item, sendData.getMsgEvent());
            try {
                executorService.submit(task);
            } catch (Exception e) {
                log.error("failed to forward callback message from pool. url={}", item, e);
            }
        }
    }

    public class WeComSendGetRequest implements Runnable {
        private Map<String, String> params;
        private String url;


        public WeComSendGetRequest(Map<String, String> params, String url) {
            this.params = params;
            this.url = url;
        }

        @Override
        public void run() {
            log.info("start to send forward callback message.params={}, url={}", params, url);

            String response = null;
            try {
                response = OkHttpUtils.getInstance().getDataSync(url, params);
            } catch (Exception e) {
                log.error("failed to send forward callback message.params={}, url={}", params, url, e);
            }
            log.info("finish to send forward callback message get request. params={}, url={}, response={}", params, url,
                    response);
        }
    }

    public class WeComSendPostRequest implements Runnable {
        private Map<String, String> params;
        private String url;
        private String bodyData;


        public WeComSendPostRequest(Map<String, String> params, String bodyData, String url) {
            this.params = params;
            this.url = url;
            this.bodyData = bodyData;
        }

        @Override
        public void run() {
            log.info("start to send forward callback message.params={}, url={}", params, url);

            String response = null;
            try {
                response = OkHttpUtils.getInstance().postJsonSync(url, params, bodyData);
            } catch (Exception e) {
                log.error("failed to send forward callback message.params={}, url={}", params, url, e);
            }
            log.info("finish to send forward callback message post request. params={}, url={}, response={}", params, url
                    , response);
        }
    }
}
