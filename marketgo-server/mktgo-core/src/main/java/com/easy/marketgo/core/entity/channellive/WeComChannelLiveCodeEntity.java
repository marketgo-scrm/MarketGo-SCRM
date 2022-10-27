package com.easy.marketgo.core.entity.channellive;

import com.easy.marketgo.core.entity.UuidBaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:21:37
 * @description : WeComContactWayEntity.java 活码基本信息表
 */
@Data
@Table(name = "wecom_channel_live_code")
@Accessors(chain = true)
public class WeComChannelLiveCodeEntity extends UuidBaseEntity {

    private static final long serialVersionUID = -3233844559393888372L;

    /**
     * 关联项目ID
     */
    private String projectUuid;

    /**
     * 对应企业微信ID
     */
    private String corpId;
    private String  agentId;

    private String name;

    /**
     * 是否跳过验证 1：自动通过 0：手动验证
     */
    private Boolean skipVerify;

    private String members;
    private Boolean addLimitStatus;
    private String addLimitMembers;
    private String backupMembers;
    private String tags;

    /**
     * 选择欢迎语：0:不开启 1：渠道欢迎语  2：员工欢迎语
     */
    private Integer welcomeType;
    private String welcomeContent;
    private Integer onlineType;

    private String state;
    /**
     * 微信返回的渠道码ID
     */
    private String configId;
    /**
     * 活码二维码地址
     */
    private String qrCode;

    private String logoMedia;

}
