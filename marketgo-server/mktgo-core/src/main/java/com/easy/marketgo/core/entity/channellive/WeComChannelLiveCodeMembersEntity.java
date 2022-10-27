package com.easy.marketgo.core.entity.channellive;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Date;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:20:03
 * @description : WechatWorkContactWayUserEntity.java 活码使用员工配置表
 */
@Data
@Table(name = "wecom_channel_live_code_members")
public class WeComChannelLiveCodeMembersEntity extends BaseEntity {

    private String channelLiveCodeUuid;
    private String memberId;
    private String memberName;
    private Boolean isBackup;
    private Boolean onlineStatus;
    private Date onlineDay;
    private Integer addLimitCount;
}
