package com.easy.marketgo.react.config;

import com.easy.marketgo.common.constants.WeComConsts;
import com.easy.marketgo.react.message.WeComMessageRouter;
import com.easy.marketgo.react.message.handler.AbstractWeComHandler;
import com.easy.marketgo.react.message.handler.externalcontact.WeComAddExternalContactHalfHandler;
import com.easy.marketgo.react.message.handler.externalcontact.WeComAddExternalContactHandler;
import com.easy.marketgo.react.message.handler.externalcontact.WeComDelExternalContactHandler;
import com.easy.marketgo.react.message.handler.externalcontact.WeComDelFollowUserHandler;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-15 21:30:52
 * @description : WxCpConfiguration.java
 */
@Configuration
public class WeComConfiguration {


    public static WeComMessageRouter router = null;

    public static WeComMessageRouter getRouter() {

        return router;
    }

    @Autowired
    private WeComAddExternalContactHandler weComAddExternalContactHandler;
    @Autowired
    private WeComAddExternalContactHalfHandler weComAddExternalContactHalfHandler;
    @Autowired
    private WeComDelExternalContactHandler weComDelExternalContactHandler;
    @Autowired
    private WeComDelFollowUserHandler weComDelFollowUserHandler;

    @PostConstruct
    public void initServices() {

        router = newRouter();
    }

    /**
     * 添加自己实现的处理器 extends {@link AbstractWeComHandler}
     * implements
     */
    private WeComMessageRouter newRouter() {

        final val newRouter = new WeComMessageRouter();
        newRouter.rule()
                 .async(false)
                 .event(WeComConsts.EventType.CHANGE_EXTERNAL_CONTACT)
                 .changeType(WeComConsts.ExternalContactChangeType.ADD_HALF_EXTERNAL_CONTACT)
                 .handler(this.weComAddExternalContactHalfHandler)
                 .end();

        newRouter.rule()
                 .async(false)
                 .event(WeComConsts.EventType.CHANGE_EXTERNAL_CONTACT)
                 .changeType(WeComConsts.ExternalContactChangeType.ADD_EXTERNAL_CONTACT)
                 .handler(this.weComAddExternalContactHandler)
                 .end();

        newRouter.rule()
                 .async(false)
                 .event(WeComConsts.EventType.CHANGE_EXTERNAL_CONTACT)
                 .changeType(WeComConsts.ExternalContactChangeType.DEL_EXTERNAL_CONTACT)
                 .handler(this.weComDelExternalContactHandler)
                 .end();
        newRouter.rule()
                 .async(false)
                 .event(WeComConsts.EventType.CHANGE_EXTERNAL_CONTACT)
                 .changeType(WeComConsts.ExternalContactChangeType.DEL_FOLLOW_USER)
                 .handler(this.weComDelFollowUserHandler)
                 .end();
        return newRouter;
    }

}
