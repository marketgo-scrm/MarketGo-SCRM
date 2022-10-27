package com.easy.marketgo.common.message;

import com.easy.marketgo.common.converter.LongArrayConverter;
import com.easy.marketgo.common.converter.XStreamCDataConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/24/22 10:50 PM
 * Describe:
 */
@Data
@Slf4j
@XStreamAlias("xml")
public class WeComMemberCallBackMessage implements Serializable {
    @XStreamAlias("ToUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String toUserName;

    @XStreamAlias("FromUserName")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String fromUserName;

    @XStreamAlias("CreateTime")
    private Long createTime;

    @XStreamAlias("MsgType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String msgType;

    @XStreamAlias("Event")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String event;

    @XStreamAlias("ChangeType")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String changeType;

    /**
     * 变更信息的成员UserID.
     */
    @XStreamAlias("UserID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String userId;

    /**
     * 成员部门列表，变更时推送，仅返回该应用有查看权限的部门id.
     */
    @XStreamAlias("Department")
    @XStreamConverter(value = LongArrayConverter.class)
    private Long[] departments;

    /**
     * 新的UserID，变更时推送（userid由系统生成时可更改一次）.
     */
    @XStreamAlias("NewUserID")
    @XStreamConverter(value = XStreamCDataConverter.class)
    private String newUserId;
}
