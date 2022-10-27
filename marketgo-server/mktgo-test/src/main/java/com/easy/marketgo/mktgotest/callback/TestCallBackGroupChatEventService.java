package com.easy.marketgo.mktgotest.callback;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.common.utils.CoreXmlUtils;
import com.easy.marketgo.core.util.OkHttpUtils;
import com.easy.marketgo.mktgotest.callback.auth.AesException;
import com.easy.marketgo.mktgotest.callback.auth.WXBizMsgCrypt;
import com.easy.marketgo.mktgotest.callback.model.ParseXmlMessage;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Map;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 9/1/22 9:25 AM
 * Describe:
 */
public class TestCallBackGroupChatEventService {

    private static String sendCreateGroupChatEvent() {
        WXBizMsgCrypt weComcpt = null;
        try {
            byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
            String encode = Base64.encodeWithoutPadding(key);
            System.out.print("encode=" + encode + "\nkey=" + key);
            weComcpt = new WXBizMsgCrypt("O6vWiv7Ox37LZW2i", "rnsf5eI+K2gE+WFomayitXr+Vw+VYbFmHCWvgFeulVI",
                    "wwa67b5f2bf5754641");
            Long currentTime = System.currentTimeMillis()/1000;


            WeComXmlMessage weComXmlMessage = new WeComXmlMessage();
            weComXmlMessage.setToUserName("wwa67b5f2bf5754641");
            weComXmlMessage.setFromUserName("sys");
            weComXmlMessage.setCreateTime(currentTime);
            weComXmlMessage.setMsgType("event");
            weComXmlMessage.setEvent("change_external_chat");
            weComXmlMessage.setChatId("wrqPhANwAAVsjvkGIDP9l01nBrEsNKPQ");
            weComXmlMessage.setChangeType("create");

            String eventMsg = WeComXmlMessage.toXml(weComXmlMessage);
            System.out.print("\neventMsg=" + eventMsg);
            String nonce = "1372623149";
            String encryptMsg = weComcpt.encryptMsg(eventMsg, String.valueOf(currentTime), nonce);
            System.out.print("\nencryptMsg=" + encryptMsg);
            return encryptMsg;
        } catch (AesException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String sendAddMemberEvent() {
        WXBizMsgCrypt weComcpt = null;
        try {
            byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
            String encode = Base64.encodeWithoutPadding(key);
            System.out.print("encode=" + encode + "\nkey=" + key);
            weComcpt = new WXBizMsgCrypt("O6vWiv7Ox37LZW2i", "rnsf5eI+K2gE+WFomayitXr+Vw+VYbFmHCWvgFeulVI",
                    "wwa67b5f2bf5754641");
            Long currentTime = System.currentTimeMillis()/1000;


            WeComXmlMessage weComXmlMessage = new WeComXmlMessage();
            weComXmlMessage.setToUserName("wwa67b5f2bf5754641");
            weComXmlMessage.setFromUserName("sys");
            weComXmlMessage.setCreateTime(currentTime);
            weComXmlMessage.setMsgType("event");
            weComXmlMessage.setEvent("change_external_chat");
            weComXmlMessage.setChatId("wrqPhANwAAVsjvkGIDP9l01nBrEsNKPQ");
            weComXmlMessage.setUpdateDetail("add_member");
            weComXmlMessage.setJoinScene(1);
            weComXmlMessage.setChangeType("update");

            String eventMsg = WeComXmlMessage.toXml(weComXmlMessage);
//            WeComXmlMessage callBack = WeComXmlMessage.fromXml(eventMsg);
            System.out.print("\neventMsg=" + eventMsg);
            String nonce = "1372623149";
            String encryptMsg = weComcpt.encryptMsg(eventMsg, String.valueOf(currentTime), nonce);
            System.out.print("\nencryptMsg=" + encryptMsg);
            return encryptMsg;
        } catch (AesException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String sendExitMemberEvent() {
        WXBizMsgCrypt weComcpt = null;
        try {
            byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
            String encode = Base64.encodeWithoutPadding(key);
            System.out.print("encode=" + encode + "\nkey=" + key);
            weComcpt = new WXBizMsgCrypt("O6vWiv7Ox37LZW2i", "rnsf5eI+K2gE+WFomayitXr+Vw+VYbFmHCWvgFeulVI",
                    "wwa67b5f2bf5754641");
            Long currentTime = System.currentTimeMillis()/1000;

            WeComXmlMessage weComXmlMessage = new WeComXmlMessage();
            weComXmlMessage.setToUserName("wwa67b5f2bf5754641");
            weComXmlMessage.setFromUserName("sys");
            weComXmlMessage.setCreateTime(currentTime);
            weComXmlMessage.setMsgType("event");
            weComXmlMessage.setEvent("change_external_chat");
            weComXmlMessage.setChatId("wrqPhANwAAVsjvkGIDP9l01nBrEsNKPQ");
            weComXmlMessage.setUpdateDetail("del_member");
            weComXmlMessage.setQuitScene(1);
            weComXmlMessage.setChangeType("update");

            String eventMsg = WeComXmlMessage.toXml(weComXmlMessage);

            System.out.print("\neventMsg=" + eventMsg);
            String nonce = "1372623149";
            String encryptMsg = weComcpt.encryptMsg(eventMsg, String.valueOf(currentTime), nonce);
            System.out.print("\nencryptMsg=" + encryptMsg);
            return encryptMsg;
        } catch (AesException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String sendChangeOwnerEvent() {
        WXBizMsgCrypt weComcpt = null;
        try {
            byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
            String encode = Base64.encodeWithoutPadding(key);
            System.out.print("encode=" + encode + "\nkey=" + key);
            weComcpt = new WXBizMsgCrypt("O6vWiv7Ox37LZW2i", "rnsf5eI+K2gE+WFomayitXr+Vw+VYbFmHCWvgFeulVI",
                    "wwa67b5f2bf5754641");
            Long currentTime = System.currentTimeMillis()/1000;

            WeComXmlMessage weComXmlMessage = new WeComXmlMessage();
            weComXmlMessage.setToUserName("wwa67b5f2bf5754641");
            weComXmlMessage.setFromUserName("sys");
            weComXmlMessage.setCreateTime(currentTime);
            weComXmlMessage.setMsgType("event");
            weComXmlMessage.setEvent("change_external_chat");
            weComXmlMessage.setChatId("wrqPhANwAAVsjvkGIDP9l01nBrEsNKPQ");
            weComXmlMessage.setUpdateDetail("change_owner");
            weComXmlMessage.setQuitScene(1);
            weComXmlMessage.setChangeType("update");

            String eventMsg = WeComXmlMessage.toXml(weComXmlMessage);
            System.out.print("\neventMsg=" + eventMsg);
            String nonce = "1372623149";
            String encryptMsg = weComcpt.encryptMsg(eventMsg, String.valueOf(currentTime), nonce);
            System.out.print("\nencryptMsg=" + encryptMsg);
            return encryptMsg;
        } catch (AesException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String sendDismissMemberEvent() {
        WXBizMsgCrypt weComcpt = null;
        try {
            byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 256).getEncoded();
            String encode = Base64.encodeWithoutPadding(key);
            System.out.print("encode=" + encode + "\nkey=" + key);
            weComcpt = new WXBizMsgCrypt("O6vWiv7Ox37LZW2i", "rnsf5eI+K2gE+WFomayitXr+Vw+VYbFmHCWvgFeulVI",
                    "wwa67b5f2bf5754641");
            Long currentTime = System.currentTimeMillis()/1000;

            WeComXmlMessage weComXmlMessage = new WeComXmlMessage();
            weComXmlMessage.setToUserName("wwa67b5f2bf5754641");
            weComXmlMessage.setFromUserName("sys");
            weComXmlMessage.setCreateTime(currentTime);
            weComXmlMessage.setMsgType("event");
            weComXmlMessage.setEvent("change_external_chat");
            weComXmlMessage.setChatId("wrqPhANwAAVsjvkGIDP9l01nBrEsNKPQ");
            weComXmlMessage.setChangeType("dismiss");

            String eventMsg = WeComXmlMessage.toXml(weComXmlMessage);
            System.out.print("\neventMsg=" + eventMsg);
            String nonce = "1372623149";
            String encryptMsg = weComcpt.encryptMsg(eventMsg, String.valueOf(currentTime), nonce);
            System.out.print("\nencryptMsg=" + encryptMsg);
            return encryptMsg;
        } catch (AesException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String createXml(String userName, String encryptMsg, String agentId) {
        // 创建文档。
        org.dom4j.Document document = DocumentHelper.createDocument();
        Element root = document.addElement("xml");
        Element toUserName = root.addElement("ToUserName");
        toUserName.addCDATA(userName);

        Element encrypt = root.addElement("Encrypt");
        encrypt.addCDATA(encryptMsg);

        Element agent = root.addElement("AgentID");
        agent.addCDATA(agentId);

        String docXmlText = document.asXML();
        return docXmlText;
    }

    private static void sendHttp(String signature, String timestamp, String nonce, String body) {
        Map<String, String> params = Maps.newHashMap();
        params.put("corp_id", "wwa67b5f2bf5754641");
        params.put("msg_signature", signature);
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);
        System.out.print("\nhttp request header. headerParams=" + params + ", \nrequestBody=" + body);
        String response = null;
        try {
            response = OkHttpUtils.getInstance().postXml("http://82.156.75.63:8521/mktgo/api/wecom/callback/customer",
                    params, body);
            System.out.print("\nsend http xml response=" + response);
        } catch (Exception e) {
            System.out.print("\nfailed to send http xml e=" + e);
        }
    }

    public static void main(String[] args) {
        String encryptMsg = sendExitMemberEvent();//sendAddMemberEvent();//sendCreateGroupChatEvent();
        if (StringUtils.isBlank(encryptMsg)) {
            System.out.print("encrypt msg is empty");
            return;
        }
        try {
            ParseXmlMessage callBack = CoreXmlUtils.parseXml2Obj(encryptMsg,
                    ParseXmlMessage.class);

            System.out.print("\nEncrypt=" + callBack.getEncrypt());
            System.out.print("\nMsgSignature=" + callBack.getMsgSignature());
            System.out.print("\nTimeStamp=" + callBack.getTimeStamp());
            System.out.print("\nNonce=" + callBack.getNonce());

            String result = createXml("wwa67b5f2bf5754641", callBack.getEncrypt(), "1000004");
            System.out.print("\nresult=" + result);

            sendHttp(callBack.getMsgSignature(), callBack.getTimeStamp(), callBack.getNonce(), result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
