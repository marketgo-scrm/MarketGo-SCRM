import com.easy.marketgo.common.message.WeComXmlMessage;
import com.easy.marketgo.gateway.MtkgoGatewayApplication;
import com.easy.marketgo.react.service.callback.WeComCallBackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = MtkgoGatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContactWayEventCallbackTest {

    @Autowired
    private WeComCallBackService weComCallBackService;

    /**
     * <xml>
     * <ToUserName><![CDATA[toUser]]></ToUserName>
     * <FromUserName><![CDATA[sys]]></FromUserName>
     * <CreateTime>1660989425</CreateTime>
     * <MsgType><![CDATA[event]]></MsgType>
     * <Event><![CDATA[change_external_contact]]></Event>
     * <ChangeType><![CDATA[add_external_contact]]></ChangeType>
     * <UserID><![CDATA[zhangsan]]></UserID>
     * <ExternalUserID><![CDATA[woAJ2GCAAAXtWyujaWJHDDGi0mAAAA]]></ExternalUserID>
     * <State><![CDATA[teststate]]></State>
     * <WelcomeCode><![CDATA[WELCOMECODE]]></WelcomeCode>
     * </xml>
     * 免验证
     * <xml>
     * <ToUserName><![CDATA[toUser]]></ToUserName>
     * <FromUserName><![CDATA[sys]]></FromUserName>
     * <CreateTime>1660989425</CreateTime>
     * <MsgType><![CDATA[event]]></MsgType>
     * <Event><![CDATA[change_external_contact]]></Event>
     * <ChangeType><![CDATA[add_half_external_contact]]></ChangeType>
     * <UserID><![CDATA[zhangsan]]></UserID>
     * <ExternalUserID><![CDATA[woAJ2GCAAAXtWyujaWJHDDGi0mACAAAA]]></ExternalUserID>
     * <State><![CDATA[teststate]]></State>
     * <WelcomeCode><![CDATA[WELCOMECODE]]></WelcomeCode>
     * </xml>
     */
    @Test
    public void testVerifyAddExtUser() throws InterruptedException {

        String xml = "<xml>\n" +
                "        <ToUserName><![CDATA[wwa67b5f2bf5754641]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1660989425</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_external_contact]]></Event>\n" +
                "        <ChangeType><![CDATA[add_external_contact]]></ChangeType>\n" +
                "        <UserID><![CDATA[wangwanzheng]]></UserID>\n" +
                "        <ExternalUserID><![CDATA[wmqPhANwAA60WqyO-IvxYD1Qc9r_Q3Hg]]></ExternalUserID>\n" +
                "        <State><![CDATA[dJGVKpucQVATT3YW9EOyC2Z6]]></State>\n" +
                "        <WelcomeCode><![CDATA[123测试]]></WelcomeCode>\n" +
                "</xml>";

        WeComXmlMessage message = WeComXmlMessage.fromXml(xml);


        System.out.println(message.toString());

        weComCallBackService.receiveEvent(xml, "wwa67b5f2bf5754641");

//        TimeUnit.MINUTES.sleep(1);
    }

    /**
     * <xml>
     * <ToUserName><![CDATA[toUser]]></ToUserName>
     * <FromUserName><![CDATA[sys]]></FromUserName>
     * <CreateTime>1660989425</CreateTime>
     * <MsgType><![CDATA[event]]></MsgType>
     * <Event><![CDATA[change_external_contact]]></Event>
     * <ChangeType><![CDATA[add_half_external_contact]]></ChangeType>
     * <UserID><![CDATA[zhangsan]]></UserID>
     * <ExternalUserID><![CDATA[woAJ2GCAAAXtWyujaWJHDDGi0mACAAAA]]></ExternalUserID>
     * <State><![CDATA[teststate]]></State>
     * <WelcomeCode><![CDATA[WELCOMECODE]]></WelcomeCode>
     * </xml>
     */
    @Test
    public void testNoVerifyAddExtUser() throws InterruptedException {

        String xml = "<xml>\n" +
                "        <ToUserName><![CDATA[wwa67b5f2bf5754641]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1660989425</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_external_contact]]></Event>\n" +
                "        <ChangeType><![CDATA[add_half_external_contact]]></ChangeType>\n" +
                "        <UserID><![CDATA[wangwanzheng]]></UserID>\n" +
                "        <ExternalUserID><![CDATA[wmqPhANwAA60WqyO-IvxYD1Qc9r_Q3Hg]]></ExternalUserID>\n" +
                "        <State><![CDATA[dJGVKpucQVATT3YW9EOyC2Z6]]></State>\n" +
                "        <WelcomeCode><![CDATA[123测试]]></WelcomeCode>\n" +
                "</xml>";
        WeComXmlMessage message = WeComXmlMessage.fromXml(xml);


        System.out.println(message.toString());
        weComCallBackService.receiveEvent(xml, "wwa67b5f2bf5754641");
//        TimeUnit.MINUTES.sleep(1);
    }

    /**
     * <xml>
     * <ToUserName><![CDATA[toUser]]></ToUserName>
     * <FromUserName><![CDATA[sys]]></FromUserName>
     * <CreateTime>1660989425</CreateTime>
     * <MsgType><![CDATA[event]]></MsgType>
     * <Event><![CDATA[change_external_contact]]></Event>
     * <ChangeType><![CDATA[del_external_contact]]></ChangeType>
     * <UserID><![CDATA[zhangsan]]></UserID>
     * <ExternalUserID><![CDATA[woAJ2GCAAAXtWyujaWJHDDGi0mACAAAA]]></ExternalUserID>
     * <Source><![CDATA[DELETE_BY_TRANSFER]]></Source>
     * </xml>
     */
    @Test
    public void delExternalContact() throws InterruptedException {

        String xml = "<xml>\n" +
                "        <ToUserName><![CDATA[wwa67b5f2bf5754641]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1660989425</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_external_contact]]></Event>\n" +
                "        <ChangeType><![CDATA[del_external_contact]]></ChangeType>\n" +
                "        <UserID><![CDATA[wangwanzheng]]></UserID>\n" +
                "        <ExternalUserID><![CDATA[wmqPhANwAA60WqyO-IvxYD1Qc9r_Q3Hg]]></ExternalUserID>\n" +
                "        <Source><![CDATA[DELETE_BY_TRANSFER]]></Source>\n" +
                "</xml>";
        weComCallBackService.receiveEvent(xml, "wwa67b5f2bf5754641");
//        TimeUnit.MINUTES.sleep(1);
    }

    /**
     * <xml>
     * <ToUserName><![CDATA[toUser]]></ToUserName>
     * <FromUserName><![CDATA[sys]]></FromUserName>
     * <CreateTime>1660989425</CreateTime>
     * <MsgType><![CDATA[event]]></MsgType>
     * <Event><![CDATA[change_external_contact]]></Event>
     * <ChangeType><![CDATA[del_follow_user]]></ChangeType>
     * <UserID><![CDATA[zhangsan]]></UserID>
     * <ExternalUserID><![CDATA[woAJ2GCAAAXtWyujaWJHDDGi0mACHAAA]]></ExternalUserID>
     * </xml>
     */
    @Test
    public void delFollowUser() throws InterruptedException {

        String xml = "<xml>\n" +
                "        <ToUserName><![CDATA[wwa67b5f2bf5754641]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1660989425</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_external_contact]]></Event>\n" +
                "        <ChangeType><![CDATA[del_follow_user]]></ChangeType>\n" +
                "        <UserID><![CDATA[wangwanzheng]]></UserID>\n" +
                "        <ExternalUserID><![CDATA[wmqPhANwAA60WqyO-IvxYD1Qc9r_Q3Hg]]></ExternalUserID>\n" +
                "</xml>    \n";
        weComCallBackService.receiveEvent(xml, "wwa67b5f2bf5754641");
//        TimeUnit.MINUTES.sleep(1);
    }

}

