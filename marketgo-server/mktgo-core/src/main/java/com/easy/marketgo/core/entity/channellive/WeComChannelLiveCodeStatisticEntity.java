package com.easy.marketgo.core.entity.channellive;

import com.easy.marketgo.core.entity.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-31 17:08:53
 * @description : WecomChannelLiveCodeStatisticEntity.java
 */
@Data
@Accessors(chain = true)
@Table("wecom_channel_live_code_statistic")
public class WeComChannelLiveCodeStatisticEntity extends BaseEntity {
    private String memberId;
    private String memberName;
    private Date eventDate;
    private Integer dailyIncreasedExtUserCount;
    private Integer dailyDecreaseExtUserCount;
    private String channelLiveCodeUuid;
    private String corpId;
}
