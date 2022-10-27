package com.easy.marketgo.core.entity.channellive;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Column;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-30 16:01:45
 * @description : WechatWorkContactWayTagEntity.java 活码标签表
 */
@Data
@Table(name = "wecom_channel_live_code_tag")
public class WeComChannelLiveCodeTagEntity extends BaseEntity {

    /**
     * 欢迎语主健ID
     */
    @Column(name = "channel_live_code_uuid", nullable = false)
    private String channelLiveCodeUuid;

    @Column(name = "group_id")
    private String groupId;


    @Column(name = "tag_id")
    private String tagId;


}
