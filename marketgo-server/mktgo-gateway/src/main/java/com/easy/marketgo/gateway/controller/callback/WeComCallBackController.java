package com.easy.marketgo.gateway.controller.callback;

import com.easy.marketgo.common.utils.CoreXmlUtils;
import com.easy.marketgo.gateway.controller.BaseController;
import com.easy.marketgo.react.bean.WeChatCropEventCallBack;
import com.easy.marketgo.react.service.callback.CallbackForwardService;
import com.easy.marketgo.react.service.callback.CallbackParserMessageService;
import lombok.extern.log4j.Log4j2;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author ssk
 */
@Controller
@RequestMapping(value = "/mktgo/api/wecom/callback")
@Log4j2
public class WeComCallBackController extends BaseController {

    @Autowired
    private CallbackParserMessageService callbackParserMessageService;

    @Autowired
    private CallbackForwardService callbackForwardService;

    @RequestMapping(produces = "text/plain;charset=utf-8", value = "/verify/{corpId}")
    @ResponseBody
    public String cropVerify(
            @PathVariable(required = false) String corpId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) throws Exception {

        var requestUri = getRequest().getRequestURI();
        log.info("prefix=请求URL地址信息为|msg={}|", requestUri);

        log.info("prefix=微信服务器响应信息为|nonce={}|timestamp={}|msg_signature={}|echostr={}|", nonce, timestamp, signature,
                echostr);

//        var wxcpt = new WXCropBizMsgCrypt(properties.getCropToken(), properties.getCropAesKey(), properties
//        .getCorpId());
//        var sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
//        log.info("verifyurl echostr: " + sEchoStr);
        return "sEchoStr";
    }


    @RequestMapping(produces = "text/plain;charset=utf-8", value = "/owner/suite/verify/{corpId}")
    @ResponseBody
    public String ownerSuiteVerify(

            @PathVariable(required = false) String corpId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) throws Exception {

        var requestUri = getRequest().getRequestURI();
        log.info("prefix=请求URL地址信息为|msg={}|", requestUri);
        log.info("prefix=微信服务器响应信息为|nonce={}|timestamp={}|msg_signature={}|echostr={}|", nonce, timestamp, signature,
                echostr);

//        WxCpTpService cpTpService = wxCpTpConfiguration.getCpTpService(suiteId);
//
//        var wxcpt = new WXCropBizMsgCrypt(
//                cpTpService.getWxCpTpConfigStorage().getToken(),
//                cpTpService.getWxCpTpConfigStorage().getAesKey(),
//                cpTpService.getWxCpTpConfigStorage().getCorpId());
//        var sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
//        log.info("verifyurl echostr: " + sEchoStr);
        return "sEchoStr";

    }


    /**
     * 企业微信事件 接收取消授权通知、授权成功通知、授权更新通知，也用于接收验证票据
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(produces = "application/xml; charset=UTF-8", value = "/owner/suite/event-auth/{corpId}")
    @ResponseBody
    public String cropEventAuth(
            @PathVariable(required = false) String corpId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestBody(required = false) String requestBody,
            @RequestParam(name = "echostr", required = false) String echostr) throws Exception {

        var requestUri = getRequest().getRequestURI();
        log.info("prefix=请求URL地址信息为|msg={}|", requestUri);

        if (StringUtils.isNotEmpty(echostr)) {
            return cropVerify(signature, timestamp, nonce, echostr, corpId);
        }

        log.info("prefix=微信服务器响应信息为|nonce={}|timestamp={}|msg_signature={}|xml={}|", nonce, timestamp, signature,
                requestBody);


        return "success";
    }


    /**
     * 数据回调URL 用于接收企业微信应用的用户消息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(produces = "application/xml; charset=UTF-8", value = "/suite/data/callback/{corpId}")
    @ResponseBody
    public String suiteDataCallback(
            @PathVariable(required = false) String corpId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestBody(required = false) String requestBody,
            @RequestParam(name = "echostr", required = false) String echostr) throws Exception {

        var requestUri = getRequest().getRequestURI();
        log.info("prefix=请求URL地址信息为|msg={}|", requestUri);

        if (StringUtils.isNotEmpty(echostr)) {
            return ownerSuiteVerify(corpId, signature, timestamp, nonce, echostr);
        }
        log.info("prefix=微信服务器响应信息为|nonce={}|timestamp={}|msg_signature={}|xml={}|", nonce, timestamp, signature,
                requestBody);
        var callBack = CoreXmlUtils.parseXml2Obj(requestBody, WeChatCropEventCallBack.class);
        try {
//            WxCpTpService cpTpService = wxCpTpConfiguration.getCpTpService(suiteId);
//            if (StringUtils.isNotEmpty(callBack.getToUserName()) && StringUtils.equals(callBack.getToUserName(),
//            properties.getCorpId())) {
//
//
//                var wxcpt = new WXCropBizMsgCrypt(cpTpService.getWxCpTpConfigStorage().getToken(), cpTpService
//                .getWxCpTpConfigStorage().getAesKey(),
//                        callBack.getToUserName());
//                var mingWenXml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
//                weChatWorkCallBackService.receiveSuiteEvent(mingWenXml, cpTpService);
//
//                log.info("明文信息为{}", mingWenXml);
//            } else if (StringUtils.isNotEmpty(callBack.getToUserName()) && StringUtils.equals(callBack
//            .getToUserName(), suiteId)) {
//
//                var wxcpt = new WXCropBizMsgCrypt(cpTpService.getWxCpTpConfigStorage().getToken(), cpTpService
//                .getWxCpTpConfigStorage().getAesKey(),
//                        suiteId);
//                var mingWenXml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
//                weChatWorkCallBackService.receiveSuiteEvent(mingWenXml, cpTpService);
//
//                log.info("明文信息为{}", mingWenXml);
//            } else {
//                var wxcpt = new WXCropBizMsgCrypt(cpTpService.getWxCpTpConfigStorage().getToken(), cpTpService
//                        .getWxCpTpConfigStorage()
//                        .getAesKey(), callBack.getToUserName());
//                var mingWenXml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
//                weChatWorkCallBackService.receiveSuiteEvent(mingWenXml, cpTpService);
//
//                log.info("明文信息为{}", mingWenXml);
//            }


            return "success";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "success";

    }


    /**
     * 指令回调URL
     * 在发生授权、通讯录变更、ticket变化等事件时，企业微信服务器会向应用的“指令回调URL”推送相应的事件消息。消息结构体将使用创建应用时的EncodingAESKey进行加密（特别注意,
     * 在第三方回调事件中使用加解密算法，receiveid的内容为suiteid），请参考接收消息解析数据包。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(produces = "application/xml; charset=UTF-8", value = "/suite/ticket/{suiteId}")
    @ResponseBody
    public String suiteTicket(
            @PathVariable(required = false) String suiteId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestBody(required = false) String requestBody,
            @RequestParam(name = "echostr", required = false) String echostr) throws Exception {

        var requestUri = getRequest().getRequestURI();
        log.info("prefix=请求URL地址信息为|msg={}|", requestUri);

        if (StringUtils.isNotEmpty(echostr)) {
            return ownerSuiteVerify(suiteId, signature, timestamp, nonce, echostr);
        }


        var callBack = CoreXmlUtils.parseXml2Obj(requestBody, WeChatCropEventCallBack.class);
        log.info("prefix=微信服务器响应信息为|nonce={}|timestamp={}|msg_signature={}|xml={}|", nonce, timestamp, signature,
                requestBody);
//        log.info("prefix=微信服务器响应信息为:{}", JsonUtil.toJSON(callBack));
//        try {
//            if (StringUtils.isNotEmpty(callBack.getSuiteId())) {
//                var wxcpt = new WXCropBizMsgCrypt(WechatWorkConstant.SUITE_TOKEN, WechatWorkConstant
//                .SUITE_ENCODING_AES_KEY, WechatWorkConstant.CROP_ID);
//                var mingWenXml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
//                log.info("企业服务商内部应用 明文信息为{}", mingWenXml);
//            } else if (StringUtils.isNotEmpty(callBack.getToUserName()) && Objects.equals(callBack.getToUserName(),
//            WechatWorkConstant.CROP_ID)) {
//                var wxcpt = new WXCropBizMsgCrypt(WechatWorkConstant.SUITE_TOKEN, WechatWorkConstant
//                .SUITE_ENCODING_AES_KEY, WechatWorkConstant.CROP_ID);
//                var mingWenXml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
//                log.info("企业服务商内部应用 明文信息为{}", mingWenXml);
//
//
//            } else if (StringUtils.isNotEmpty(callBack.getToUserName()) && !Objects.equals(callBack.getToUserName()
//            , WechatWorkConstant.CROP_ID)) {
//                WxCpTpService cpTpService = wxCpTpConfiguration.getCpTpService(suiteId);
//                var wxcpt = new WXCropBizMsgCrypt(cpTpService.getWxCpTpConfigStorage().getToken(), cpTpService
//                .getWxCpTpConfigStorage().getAesKey(),
//                        cpTpService.getWxCpTpConfigStorage().getSuiteId());
//                var mingWenXml = wxcpt.DecryptMsg(signature, timestamp, nonce, requestBody);
//                log.info("解析明文信息为：{}",mingWenXml);
//
//                weChatWorkCallBackService.receiveSuiteEvent(mingWenXml, cpTpService);
//            }
//            return "success";
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
        return "success";

    }

    @GetMapping(value = {"/contacts"})
    @ResponseBody
    public String acceptContactsAuthForUrl(@RequestParam("msg_signature") String msgSignature,
                                           @RequestParam String timestamp,
                                           @RequestParam String nonce,
                                           @RequestParam String echostr,
                                           @RequestParam("corp_id") String corpId) {
        log.info(
                "acceptContactsAuthForUrl, msgSignature={}, timestamp={}, nonce={}, echostr={}, corpId={}",
                msgSignature, timestamp, nonce, echostr, corpId);
        callbackForwardService.acceptContactsCallbackAuthUrl(msgSignature, timestamp, nonce, echostr, corpId);
        return callbackParserMessageService.acceptContactsCallbackAuthUrl(msgSignature, timestamp, nonce, echostr,
                corpId);
    }

    @PostMapping(value = "/contacts")
    @ResponseBody
    public String acceptContactsAuthEvent(@RequestParam("msg_signature") String msgSignature,
                                          @RequestParam String timestamp,
                                          @RequestParam String nonce,
                                          @RequestParam("corp_id") String corpId,
                                          @RequestBody String msgEvent) {
        log.info(
                "acceptContactsAuthEvent, msgSignature={}, timestamp={}, nonce={}, msgEvent={}",
                msgSignature, timestamp, nonce, msgEvent);
        callbackForwardService.acceptContactsCallbackEvent(msgSignature, timestamp, nonce, msgEvent, corpId);
        callbackParserMessageService.acceptContactsCallbackEvent(msgSignature, timestamp, nonce, msgEvent, corpId);
        return "success";
    }

    @GetMapping(value = {"/customer"})
    @ResponseBody
    public String acceptCustomerAuthForUrl(@RequestParam("msg_signature") String msgSignature,
                                           @RequestParam String timestamp,
                                           @RequestParam String nonce,
                                           @RequestParam String echostr,
                                           @RequestParam("corp_id") String corpId) {
        log.info("acceptCustomerAuthForUrl, msgSignature={}, timestamp={}, nonce={}, echostr={}, " +
                        "corpId={}",
                msgSignature, timestamp, nonce, echostr, corpId);
        callbackForwardService.acceptCustomerCallbackAuthUrl(msgSignature, timestamp, nonce, echostr,
                corpId);
        return callbackParserMessageService.acceptCustomerCallbackAuthUrl(msgSignature, timestamp, nonce, echostr,
                corpId);
    }

    @PostMapping(value = "/customer")
    @ResponseBody
    public String acceptCustomerAuthEvent(@RequestParam("msg_signature") String msgSignature,
                                          @RequestParam String timestamp,
                                          @RequestParam String nonce,
                                          @RequestParam("corp_id") String corpId,
                                          @RequestBody String msgEvent) {
        log.info("acceptCustomerAuthEvent, msgSignature={}, timestamp={}, nonce={}, msgEvent={}, corpId={}",
                msgSignature, timestamp, nonce, msgEvent, corpId);
        callbackForwardService.acceptCustomerCallbackEvent(msgSignature, timestamp, nonce, msgEvent, corpId);
        callbackParserMessageService.acceptCustomerCallbackEvent(msgSignature, timestamp, nonce, msgEvent, corpId);
        return "success";
    }

}
